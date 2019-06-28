import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlashModel {
    private HTTPController httpClient;

    public FlashModel (String user, String password) throws IOException, InterruptedException {
        httpClient = new HTTPController(user,password);
        httpClient.login();
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
        Document XML = Jsoup.parse(httpClient.getXMLByMethod("GetAdventures"));
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
