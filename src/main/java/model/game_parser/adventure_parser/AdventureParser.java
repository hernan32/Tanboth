package model.game_parser.adventure_parser;


import model.TanothHttpClientSingleton;
import model.game_parser.GameAction;
import model.game_parser.Validation;
import model.game_parser.adventure_parser.adventure.AdventureAttributes;
import model.game_parser.adventure_parser.adventure.AdventureCriteria;
import model.game_parser.adventure_parser.adventure.FightResult;
import model.game_parser.adventure_parser.adventure.exception.AdventureRunningException;
import model.game_parser.adventure_parser.adventure.exception.FightResultException;
import model.game_parser.adventure_parser.adventure.exception.IllusionCaveRunningException;
import model.game_parser.adventure_parser.adventure.exception.IllusionDisabledException;
import model.game_parser.game.exception.TimeOutException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdventureParser implements Validation {

    private TanothHttpClientSingleton httpClient;

    public AdventureParser(TanothHttpClientSingleton httpClient) {
        this.httpClient = httpClient;
    }

    enum METHOD_LIST {
        GetMapDetails, GetWorkData,
        GetDungeon, StartIllusionCave,
        StartAdventure, GetAdventures
    }


    public AdventureAttributes startAdventureByCriteria(AdventureCriteria adventureCriteria) throws InterruptedException, AdventureRunningException, IOException, TimeOutException, FightResultException, IllusionCaveRunningException {
        List<AdventureAttributes> AdventureList;
        AdventureAttributes adventureByCriteria = new AdventureAttributes(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 0, 0);
        AdventureList = getAdventures();
        for (AdventureAttributes adventure : AdventureList) {
            if (adventure.getDifficulty() != 2)
                adventureByCriteria = adventure.getBest(adventureCriteria, adventureByCriteria); // =2 Hardest
        }
        startAdventure(adventureByCriteria);
        return adventureByCriteria;
    }

    private void startAdventure(AdventureAttributes adventure) throws IOException, InterruptedException, AdventureRunningException, TimeOutException, FightResultException, IllusionCaveRunningException {
        String GetAdventures = METHOD_LIST.GetAdventures.name();
        String StartAdventure = METHOD_LIST.StartAdventure.name();
        GameAction GameAction = new GameAction.newBuilder(GetAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        validateResponse(XML, GetAdventures);
        GameAction = new GameAction.newBuilder(StartAdventure, httpClient.getSessionID())
                .addParameter(adventure.getQuestID())
                .build();
        httpClient.getXMLByAction(GameAction);
    }

    private List<AdventureAttributes> getAdventures() throws IOException, InterruptedException, AdventureRunningException, TimeOutException, IllusionCaveRunningException, FightResultException {
        String GetAdventures = METHOD_LIST.GetAdventures.name();
        GameAction GameAction = new GameAction.newBuilder(GetAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        List<AdventureAttributes> adventures = new ArrayList<>();
        AdventureAttributes adventure;
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

            adventure = new AdventureAttributes(difficulty, duration, experience, fightChance, gold, questID);
            adventures.add(adventure);
        }
        return adventures;
    }

    public int getAdventuresMadeToday() throws
            IOException, InterruptedException, AdventureRunningException, FightResultException, IllusionCaveRunningException, TimeOutException {
        String GetAdventures = METHOD_LIST.GetAdventures.name();
        GameAction GameAction = new GameAction.newBuilder(GetAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        validateResponse(XML, GetAdventures);
        return Integer.parseInt(XML.getElementsContainingOwnText("adventures_made_today").first().parent().select("i4").text());
    }

    public int getFreeAdventuresPerDay() throws IOException, InterruptedException, AdventureRunningException, FightResultException, IllusionCaveRunningException, TimeOutException {
        String GetAdventures = METHOD_LIST.GetAdventures.name();
        GameAction GameAction = new GameAction.newBuilder(GetAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        validateResponse(XML, GetAdventures);
        return Integer.parseInt(XML.getElementsContainingOwnText("free_adventures_per_day").first().parent().select("i4").text());
    }

    public int getBossMapMadeToday() throws IOException, InterruptedException, AdventureRunningException, FightResultException, TimeOutException, IllusionCaveRunningException, IllusionDisabledException {
        String GetMapDetails = METHOD_LIST.GetMapDetails.name();
        GameAction GameAction = new GameAction.newBuilder(GetMapDetails, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        if (isEnabledIllusionCave(XML))
            throw new IllusionDisabledException("[ERROR] Illusion Cave Disabled @" + GetMapDetails);
        validateResponse(XML, GetMapDetails);
        return Integer.parseInt(XML.getElementsContainingOwnText("illusion_cave_bloodstone_cost").first().parent().select("i4").text());
    }

    public void startIllusionCave() throws IOException, InterruptedException, AdventureRunningException, TimeOutException, FightResultException, IllusionCaveRunningException {
        String StartIllusionCave = METHOD_LIST.StartIllusionCave.name();
        GameAction gameAction = new GameAction.newBuilder(StartIllusionCave, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, StartIllusionCave);
    }

    public FightResult getFightResult() throws AdventureRunningException, IOException, InterruptedException, IllusionCaveRunningException, TimeOutException, FightResultException {
        String GetAdventures = METHOD_LIST.GetAdventures.name();
        GameAction GameAction = new GameAction.newBuilder(GetAdventures, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        validateResponse(XML, GetAdventures);
        return new FightResult();
    }

    public boolean isEnabledIllusionCave(Document XML) {
        String disabled = "<!--?xml version=\"1.0\" encoding=\"utf-8\"?-->\n" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <methodresponse>\n" +
                "   <params>\n" +
                "    <param>\n" +
                "    <value>\n" +
                "     <struct>\n" +
                "      <member>\n" +
                "       <name>\n" +
                "        show_illusion_cave\n" +
                "       </name>\n" +
                "       <value>\n" +
                "        <boolean>\n" +
                "         0\n" +
                "        </boolean>\n" +
                "       </value>\n" +
                "      </member>\n" +
                "     </struct>\n" +
                "    </value>\n" +
                "   </params>\n" +
                "  </methodresponse> \n" +
                " </body>\n" +
                "</html>";
        return XML.toString().equals(disabled);
    }

    public void validateResponse(Document XML, String Action) throws TimeOutException, IllusionCaveRunningException, FightResultException, AdventureRunningException {
        if (isActiveAdventure(XML)) throw new AdventureRunningException("[ERROR] Quest Running @" + Action);
        else if (isActiveIllusionCave(XML))
            throw new IllusionCaveRunningException("[ERROR] Illusion Cave Running @" + Action);
        else if (isFightResult(XML)) throw new FightResultException("[ERROR] Fight Result @" + Action);
        else if (timeOut(XML)) throw new TimeOutException("[ERROR] Connection Time Out @" + Action);
    }
}
