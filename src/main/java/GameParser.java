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

    public GameParser(String user, String password) throws IOException, InterruptedException {
        httpClient = new TanothHttpClient(user, password);
        httpClient.login();
        httpClient.setServerPath(getServerPath());
    }

    public String getServerPath() {
        Document doc = Jsoup.parse(httpClient.getLoginResponse());
        Element scriptData = doc.select("body").select("script").get(0);
        return StringUtils.substringBetween(scriptData.toString(), "serverpath: \"", "\",");
    }

    public String getSessionID() {
        Document doc = Jsoup.parse(httpClient.getLoginResponse());
        Element scriptData = doc.select("body").select("script").get(0);
        return StringUtils.substringBetween(scriptData.toString(), "sessionID: \"", "\",");
    }

    /*
        Method List:
        MiniUpdate ¿?
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
     */

    public List<Adventure> getAdventures() throws IOException, InterruptedException {
        GameActionRequest GameAction = new GameActionRequest.newBuilder("GetAdventures", getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        List<Adventure> adventures = new ArrayList<>();
        Adventure adventure;
        Elements adventuresXML = XML.select("array").select("struct");
        int difficulty, duration, experience, fightChance, gold, questID;

        for (Element adventureXML : adventuresXML) {
            Elements adventureParametersXML = adventureXML.select("i4");
            //Adventure Parameters
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

}
