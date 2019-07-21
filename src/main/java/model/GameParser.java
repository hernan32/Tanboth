package model;

import model.adventure.Adventure;
import model.adventure.Criteria;
import model.adventure.exception.AdventureRunningException;
import model.exception.TimeOutException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameParser {
    private TanothHttpClient httpClient;

    public GameParser(String user, String password, String loginURI, String serverNumber) throws IOException, InterruptedException {
        httpClient = new TanothHttpClient(user, password, loginURI, serverNumber);
        httpClient.login();
        httpClient.setServerPath(getServerPath());
    }

    private String getServerPath() {
        Document doc = Jsoup.parse(httpClient.getLoginResponse());
        Element scriptData = doc.select("body").select("script").get(0);
        return StringUtils.substringBetween(scriptData.toString(), "serverpath: \"", "\",");
    }

    private String getSessionID() {
        Document doc = Jsoup.parse(httpClient.getLoginResponse());
        Element scriptData = doc.select("body").select("script").get(0);
        return StringUtils.substringBetween(scriptData.toString(), "sessionID: \"", "\",");
    }

    /*
        Method List:
        MiniUpdate -> "HeaderUpdate"
        GetInboxHeaders
        GetOutboxHeaders
        GetGuild
        GetHighscore
        GetMapDetails
        GetPvpData
        GetWorkData
        MerchItems
        GetMount
        GetDungeon
        GetPremiumData
        GetParty
        GetEquipment
        GetUserAttributes
        GetChatsecret
        StartAdventure
        error
     */

    public List<Adventure> getAdventures() throws IOException, InterruptedException, AdventureRunningException {
        GameAction GameAction = new GameAction.newBuilder("GetAdventures", getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        if (isActiveAdventure(XML)) throw new AdventureRunningException("[ERROR] Quest Running @GettingAdventures");
        List<Adventure> adventures = new ArrayList<>();
        Adventure adventure;
        Elements adventuresXML = XML.select("array").select("struct");
        int difficulty, duration, experience, fightChance, gold, questID;

        for (Element adventureXML : adventuresXML) {
            Elements adventureParametersXML = adventureXML.select("i4");

            difficulty = Integer.parseInt(adventureParametersXML.get(0).text());
            duration = Integer.parseInt(adventureParametersXML.get(1).text());
            experience = Integer.parseInt(adventureParametersXML.get(2).text());
            fightChance = Integer.parseInt(adventureParametersXML.get(3).text());
            gold = Integer.parseInt(adventureParametersXML.get(4).text());
            questID = Integer.parseInt(adventureParametersXML.get(5).text());

            adventure = new Adventure(difficulty, duration, experience, fightChance, gold, questID);
            adventures.add(adventure);
        }

        return adventures;
    }

    public int getInventorySpace() throws IOException, InterruptedException {
        GameAction GameAction = new GameAction.newBuilder("GetEquipment", getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        Element dataItemsXML = XML.select("array").select("data").first();
        Elements itemsXML = dataItemsXML.children();
        return itemsXML.size(); //Max. Inventory Space is "30" (Harcoded)
    }

    public Adventure startAdventureByCriteria(Criteria criteria) throws InterruptedException, AdventureRunningException, IOException, TimeOutException {
        List<Adventure> AdventureList;
        Adventure adventureByCriteria = new Adventure(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 0, 0);
        try {
            AdventureList = getAdventures();
            for (Adventure adventure : AdventureList) {
                if (adventure.getDifficulty() != 2)
                    adventureByCriteria = adventure.getBest(criteria, adventureByCriteria); // =2 Hardest
            }
            startAdventure(adventureByCriteria);
        } catch (AdventureRunningException ex) {
            throw new AdventureRunningException(ex.getMessage());
        } catch (TimeOutException ex) {
            throw new TimeOutException(ex.getMessage());
        }
        return adventureByCriteria;
    }

    private void startAdventure(Adventure adventure) throws IOException, InterruptedException, AdventureRunningException, TimeOutException {
        GameAction GameAction = new GameAction.newBuilder("GetAdventures", getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        if (isActiveAdventure(XML)) throw new AdventureRunningException("[ERROR] Quest Running @StartingQuest");
        else if (timeOut(XML)) throw new TimeOutException("[ERROR] Connection Time Out @StartingQuest");
        GameAction = new GameAction.newBuilder("StartAdventure", getSessionID())
                .addParameter(adventure.getQuestID())
                .build();
        httpClient.getXMLByAction(GameAction);
    }

    public int getFreeAdventuresPerDay() throws IOException, InterruptedException, AdventureRunningException {
        GameAction GameAction = new GameAction.newBuilder("GetAdventures", getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        if (isActiveAdventure(XML))
            throw new AdventureRunningException("[ERROR] Quest Running @GettingFreeAdventuresPerDay");
        return Integer.parseInt(XML.getElementsContainingOwnText("free_adventures_per_day").first().parent().select("i4").text());
    }

    public int getAdventuresMadeToday() throws IOException, InterruptedException, AdventureRunningException {
        GameAction GameAction = new GameAction.newBuilder("GetAdventures", getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        if (isActiveAdventure(XML))
            throw new AdventureRunningException("[ERROR] Quest Running @GettingAdventuresMadeToday");
        return Integer.parseInt(XML.getElementsContainingOwnText("adventures_made_today").first().parent().select("i4").text());
    }

    public boolean timeOut(Document XML) {
        return XML.getElementsContainingOwnText("no_valid_session").size() > 0;
    }

    private boolean isActiveAdventure(Document XML) {
        return XML.getElementsContainingOwnText("running_adventure_id").size() > 0;
    }

    public void reconnect() throws IOException, InterruptedException {
        httpClient.login();
    }
}
