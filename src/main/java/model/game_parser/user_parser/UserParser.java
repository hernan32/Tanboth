package model.game_parser.user_parser;

import model.TanothHttpClientSingleton;
import model.game_parser.GameAction;
import model.game_parser.Validation;
import model.game_parser.adventure_parser.adventure.exception.AdventureRunningException;
import model.game_parser.adventure_parser.adventure.exception.FightResultException;
import model.game_parser.adventure_parser.adventure.exception.IllusionCaveRunningException;
import model.game_parser.game.exception.TimeOutException;
import model.game_parser.user_parser.user.UserAttributes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class UserParser implements Validation {

    private TanothHttpClientSingleton httpClient;

    enum METHOD_LIST {
        GetInboxHeaders, GetOutboxHeaders,
        GetGuild, GetHighscore, GetPvpData, GetMount,
        GetPremiumData, RaiseAttribute, GetUserAttributes,
    }

    public UserParser(TanothHttpClientSingleton httpClient) {
        this.httpClient = httpClient;
    }

    // STR DEX CON INT
    public void increaseStats() throws IOException, InterruptedException, TimeOutException, AdventureRunningException, IllusionCaveRunningException, FightResultException {
        boolean lowGold = false;
        GameAction gameAction;
        UserAttributes attr = getUserAttributes();
        int gold = attr.getGold();
        String RaiseAttribute = METHOD_LIST.RaiseAttribute.name();

        while (!lowGold) {
            lowGold = true;
            if (attr.getSTRcost() <= gold) {
                gameAction = new GameAction.newBuilder(RaiseAttribute, httpClient.getSessionID()).addParameter("STR").build();
                httpClient.getXMLByAction(gameAction);
                gold -= attr.getSTRcost();
                lowGold = false;
            }
            if (attr.getDEXcost() <= gold) {
                gameAction = new GameAction.newBuilder(RaiseAttribute, httpClient.getSessionID()).addParameter("DEX").build();
                httpClient.getXMLByAction(gameAction);
                gold -= attr.getDEXcost();
                lowGold = false;
            }
            if (attr.getCONcost() <= gold) {
                gameAction = new GameAction.newBuilder(RaiseAttribute, httpClient.getSessionID()).addParameter("CON").build();
                httpClient.getXMLByAction(gameAction);
                gold -= attr.getCONcost();
                lowGold = false;
            }
            if (attr.getINTcost() <= gold) {
                gameAction = new GameAction.newBuilder(RaiseAttribute, httpClient.getSessionID()).addParameter("INT").build();
                httpClient.getXMLByAction(gameAction);
                gold -= attr.getINTcost();
                lowGold = false;
            }
        }
    }

    private UserAttributes getUserAttributes() throws IOException, InterruptedException, TimeOutException, AdventureRunningException, IllusionCaveRunningException, FightResultException {
        String GetUserAttributes = METHOD_LIST.GetUserAttributes.name();
        GameAction GameAction = new GameAction.newBuilder(GetUserAttributes, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        validateResponse(XML, GetUserAttributes);
        String[] nameStats = {"cost_dex", "cost_int", "cost_str", "cost_con", "gold"};
        int[] userStats = new int[nameStats.length];
        for (int i = 0; i < 5; i++) {
            userStats[i] = Integer.parseInt(XML.select("name:contains(" + nameStats[i] + ")").first().parent().select("member > value > i4").text());
        }
        return new UserAttributes(userStats[0], userStats[1], userStats[2], userStats[3], userStats[4]);
    }

}
