package model.game.parser.user;

import model.TanothHttpClientSingleton;
import model.game.attributes.user.UserAttributes;
import model.game.parser.GameAction;
import model.game.parser.Validation;
import model.game.parser.exception.TimeOutException;
import model.game.parser.quest.exception.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class UserParser implements Validation {

    private final TanothHttpClientSingleton httpClient;
    private final UserAttributes userAttributes;

    enum METHOD_LIST {
        GetInboxHeaders, GetOutboxHeaders,
        GetGuild, GetHighscore, GetPvpData, GetMount,
        GetPremiumData, RaiseAttribute, GetUserAttributes,
        MiniUpdate
    }

    public UserParser(TanothHttpClientSingleton httpClient, UserAttributes userAttributes) {
        this.httpClient = httpClient;
        this.userAttributes = userAttributes;
    }


    // STR DEX CON INT
    public void increaseStats() throws IOException, InterruptedException, RewardResultException, AdventureRunningException, IllusionCaveRunningException, WorkingException, IllusionDisabledException, TimeOutException, FightResultException {
        boolean lowGold = false;
        GameAction gameAction;
        String RaiseAttribute = METHOD_LIST.RaiseAttribute.name();

        while (!lowGold) {
            lowGold = true;
            if (userAttributes.getSTRcost() <= userAttributes.getGold()) {
                gameAction = new GameAction.newBuilder(RaiseAttribute, httpClient.getSessionID()).addParameter("STR").build();
                httpClient.getXMLByAction(gameAction);
                userAttributes.setGold(userAttributes.getGold() - userAttributes.getSTRcost());
                lowGold = false;
            }
            if (userAttributes.getDEXcost() <= userAttributes.getGold()) {
                gameAction = new GameAction.newBuilder(RaiseAttribute, httpClient.getSessionID()).addParameter("DEX").build();
                httpClient.getXMLByAction(gameAction);
                userAttributes.setGold(userAttributes.getGold() - userAttributes.getSTRcost());
                lowGold = false;
            }
            if (userAttributes.getCONcost() <= userAttributes.getGold()) {
                gameAction = new GameAction.newBuilder(RaiseAttribute, httpClient.getSessionID()).addParameter("CON").build();
                httpClient.getXMLByAction(gameAction);
                userAttributes.setGold(userAttributes.getGold() - userAttributes.getSTRcost());
                lowGold = false;
            }
            if (userAttributes.getINTcost() <= userAttributes.getGold()) {
                gameAction = new GameAction.newBuilder(RaiseAttribute, httpClient.getSessionID()).addParameter("INT").build();
                httpClient.getXMLByAction(gameAction);
                userAttributes.setGold(userAttributes.getGold() - userAttributes.getSTRcost());
                lowGold = false;
            }
            getUserAttributes();
        }
    }

    public void getUserAttributes() throws IOException, InterruptedException, TimeOutException, AdventureRunningException, IllusionCaveRunningException, FightResultException, WorkingException, RewardResultException, IllusionDisabledException {
        String getUserAttributes = METHOD_LIST.GetUserAttributes.name();
        GameAction gameAction = new GameAction.newBuilder(getUserAttributes, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getUserAttributes);
        String[] attributeName = {"cost_dex", "cost_int", "cost_str", "cost_con", "gold"};
        int[] userAttribute = new int[attributeName.length];
        for (int i = 0; i < 5; i++) {
            userAttribute[i] = Integer.parseInt(XML.select("name:contains(" + attributeName[i] + ")").first().parent().select("member > value > i4").text());
        }
        userAttributes.setDEXcost(userAttribute[0]);
        userAttributes.setINTcost(userAttribute[1]);
        userAttributes.setSTRcost(userAttribute[2]);
        userAttributes.setCONcost(userAttribute[3]);
        userAttributes.setGold(userAttribute[4]);
    }

    public void getBloodStones() throws IOException, InterruptedException, RewardResultException, IllusionCaveRunningException, AdventureRunningException, WorkingException, IllusionDisabledException, TimeOutException, FightResultException {
        String getBloodStones = METHOD_LIST.MiniUpdate.name();
        GameAction gameAction = new GameAction.newBuilder(getBloodStones, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(gameAction));
        validateResponse(XML, getBloodStones);
        userAttributes.setBloodStones(Integer.parseInt(XML.getElementsContainingOwnText("bs").first().parent().select("i4").text()));
    }
}
