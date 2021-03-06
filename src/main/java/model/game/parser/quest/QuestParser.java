package model.game.parser.quest;


import model.TanothHttpClientSingleton;
import model.game.attributes.quest.QuestAttributes;
import model.game.attributes.quest.adventure.Adventure;
import model.game.attributes.quest.adventure.AdventureCriteria;
import model.game.parser.GameAction;
import model.game.parser.Validation;
import model.game.parser.exception.TimeOutException;
import model.game.parser.quest.exception.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestParser implements Validation {

    private final TanothHttpClientSingleton httpClient;
    private final QuestAttributes questAttributes;

    public QuestParser(TanothHttpClientSingleton httpClient, QuestAttributes questAttributes) {
        this.httpClient = httpClient;
        this.questAttributes = questAttributes;
    }

    public void startAdventureByCriteria(AdventureCriteria adventureCriteria) throws InterruptedException, AdventureRunningException, IOException, TimeOutException, FightResultException, IllusionCaveRunningException, WorkingException, RewardResultException, IllusionDisabledException {
        List<Adventure> adventureList;
        Adventure adventureByCriteria = new Adventure(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 0, 0);
        adventureList = getAdventures();
        for (Adventure adventure : adventureList) {
            if (adventure.getDifficulty() != 2)
                adventureByCriteria = adventure.getBest(adventureCriteria, adventureByCriteria); // =2 Hardest
        }
        startAdventure(adventureByCriteria);
        questAttributes.setCurrentAdventure(adventureByCriteria);
    }

    private void startAdventure(Adventure adventure) throws IOException, InterruptedException, AdventureRunningException, TimeOutException, FightResultException, IllusionCaveRunningException, WorkingException, RewardResultException, IllusionDisabledException {
        String getAdventures = METHOD_LIST.GetAdventures.name();
        String startAdventure = METHOD_LIST.StartAdventure.name();
        GameAction gameAction = new GameAction.newBuilder(getAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getAdventures);
        gameAction = new GameAction.newBuilder(startAdventure, httpClient.getSessionID())
                .addParameter(adventure.getQuestID())
                .build();
        httpClient.getXMLByAction(gameAction);
    }

    private List<Adventure> getAdventures() throws IOException, InterruptedException {
        String getAdventures = METHOD_LIST.GetAdventures.name();
        GameAction gameAction = new GameAction.newBuilder(getAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
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

    public void getAdventuresMadeToday() throws IOException, InterruptedException, AdventureRunningException, FightResultException, IllusionCaveRunningException, TimeOutException, WorkingException, RewardResultException, IllusionDisabledException {
        String getAdventures = METHOD_LIST.GetAdventures.name();
        GameAction gameAction = new GameAction.newBuilder(getAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getAdventures);
        questAttributes.setAdventuresMade(Integer.parseInt(XML.getElementsContainingOwnText("adventures_made_today").first().parent().select("i4").text()));
    }

    public void getFreeAdventuresPerDay() throws IOException, InterruptedException, AdventureRunningException, FightResultException, IllusionCaveRunningException, TimeOutException, WorkingException, RewardResultException, IllusionDisabledException {
        String getAdventures = METHOD_LIST.GetAdventures.name();
        GameAction gameAction = new GameAction.newBuilder(getAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getAdventures);
        questAttributes.setFreeAdventures(Integer.parseInt(XML.getElementsContainingOwnText("free_adventures_per_day").first().parent().select("i4").text()));
    }

    public void getIllusionCaveMadeToday() throws IOException, InterruptedException, AdventureRunningException, FightResultException, TimeOutException, IllusionCaveRunningException, WorkingException, RewardResultException, IllusionDisabledException {
        String getMapDetails = METHOD_LIST.GetMapDetails.name();
        GameAction gameAction = new GameAction.newBuilder(getMapDetails, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getMapDetails);
        questAttributes.setIllusionCaveMade(Integer.parseInt(XML.getElementsContainingOwnText("illusion_cave_bloodstone_cost").first().parent().select("i4").text()));
    }

    public void getFreeDungeonsPerDay() throws RewardResultException, IllusionCaveRunningException, AdventureRunningException, WorkingException, IllusionDisabledException, TimeOutException, FightResultException, IOException, InterruptedException {
        String getDungeon = METHOD_LIST.GetDungeon.name();
        GameAction gameAction = new GameAction.newBuilder(getDungeon, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getDungeon);
        questAttributes.setFreeDungeons(Integer.parseInt(XML.getElementsContainingOwnText("free_tries_today").first().parent().select("i4").text()));
    }

    public void getDungeonsMadeToday() throws RewardResultException, IllusionCaveRunningException, AdventureRunningException, WorkingException, IllusionDisabledException, TimeOutException, FightResultException, IOException, InterruptedException {
        String getDungeon = METHOD_LIST.GetDungeon.name();
        GameAction gameAction = new GameAction.newBuilder(getDungeon, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getDungeon);
        questAttributes.setDungeonsMade(Integer.parseInt(XML.getElementsContainingOwnText("dungeon_made_today").first().parent().select("i4").text()));
    }

    public void startIllusionCave() throws IOException, InterruptedException, AdventureRunningException, TimeOutException, FightResultException, IllusionCaveRunningException, WorkingException, IllusionDisabledException, RewardResultException {
        String startIllusionCave = METHOD_LIST.StartIllusionCave.name();
        GameAction gameAction = new GameAction.newBuilder(startIllusionCave, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, startIllusionCave);
    }

    public void startDungeon() throws IOException, InterruptedException, RewardResultException, IllusionCaveRunningException, AdventureRunningException, WorkingException, IllusionDisabledException, TimeOutException, FightResultException {
        String startDungeon = METHOD_LIST.StartDungeon.name();
        GameAction gameAction = new GameAction.newBuilder(startDungeon, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, startDungeon);
    }

    public void getResult() throws AdventureRunningException, IOException, InterruptedException, IllusionCaveRunningException, TimeOutException, FightResultException, WorkingException, RewardResultException, IllusionDisabledException {
        String getAdventures = METHOD_LIST.GetAdventures.name();
        GameAction gameAction = new GameAction.newBuilder(getAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getAdventures);
    }

    public int getWorkingSeconds() throws IOException, InterruptedException, AdventureRunningException, FightResultException, TimeOutException, IllusionCaveRunningException, IllusionDisabledException, RewardResultException {
        String GetWorking = METHOD_LIST.MiniUpdate.name();
        GameAction gameAction = new GameAction.newBuilder(GetWorking, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        try {
            validateResponse(XML, GetWorking);
        } catch (WorkingException ex) {
            return Integer.parseInt(XML.getElementsContainingOwnText("time").first().parent().select("i4").text());
        }
        return 0;
    }

    public int getIllusionCaveSeconds() throws IOException, InterruptedException {
        String getIllusionCave = METHOD_LIST.GetMapDetails.name();
        GameAction gameAction = new GameAction.newBuilder(getIllusionCave, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        return Integer.parseInt(XML.getElementsContainingOwnText("running_event_quest_time_remain").first().parent().select("i4").text());
    }

    public int getAdventureSeconds() throws IOException, InterruptedException {
        String getAdventures = METHOD_LIST.GetAdventures.name();
        GameAction gameAction = new GameAction.newBuilder(getAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        return Integer.parseInt(XML.getElementsContainingOwnText("running_adventure_time_remain").first().parent().select("i4").text());
    }

    private boolean isDisabledIllusionCave(Document XML) {
        Elements illusionCaveData = XML.getElementsContainingOwnText("show_illusion_cave");
        if (illusionCaveData.size() > 0) {
            return Integer.parseInt(illusionCaveData.first().parent().select("boolean").text()) == 0;
        } else return false;
    }

    private boolean isWorking() {
        String getWorking = METHOD_LIST.MiniUpdate.name();
        GameAction gameAction = new GameAction.newBuilder(getWorking, httpClient.getSessionID()).build();
        Document XML = null;
        try {
            XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return XML.getElementsContainingOwnText("work").size() > 0;
    }

    private boolean isEmptyResponse(Document XML) throws WorkingException, TimeOutException, RewardResultException, IllusionCaveRunningException, FightResultException, IllusionDisabledException, AdventureRunningException {
        String wrongResponse = "<!--?xml version=\"1.0\" encoding=\"utf-8\"?-->\n" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <methodresponse>\n" +
                "   <params>\n" +
                "    <param>\n" +
                "    <value>\n" +
                "     <struct />\n" +
                "    </value>\n" +
                "   </params>\n" +
                "  </methodresponse> \n" +
                " </body>\n" +
                "</html>";

        if (XML.toString().equals(wrongResponse)) {
            try {
                getIllusionCaveMadeToday();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void validateResponse(Document XML, String Action) throws
            TimeOutException, IllusionCaveRunningException, FightResultException, AdventureRunningException, WorkingException, RewardResultException, IllusionDisabledException {
        printXML(XML, Action);
        if (isActiveAdventure(XML)) throw new AdventureRunningException("[ERROR] Quest Running @" + Action);
        else if (isWorking()) throw new WorkingException("[ERROR] Working @" + Action);
        else if (isDisabledIllusionCave(XML))
            throw new IllusionDisabledException("[ERROR] Ilussion Cave Disabled @" + Action);
        else if (isFightResult(XML)) throw new FightResultException("[ERROR] Fight Result @" + Action);
        else if (isRewardResult(XML)) throw new RewardResultException("[ERROR] Reward Result @" + Action);
        else if (isActiveIllusionCave(XML) || isEmptyResponse(XML))
            throw new IllusionCaveRunningException("[ERROR] Illusion Cave Running @" + Action);
        else if (timeOut(XML)) throw new TimeOutException("[ERROR] Connection Time Out @" + Action);
    }

    enum METHOD_LIST {
        GetMapDetails, GetWorkData,
        GetDungeon, StartIllusionCave,
        StartAdventure, GetAdventures,
        CancelIllusionCave, StartDungeon,
        MiniUpdate //Experimental
    }
}
