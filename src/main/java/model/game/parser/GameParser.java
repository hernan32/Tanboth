package model.game.parser;

import model.TanothHttpClientSingleton;
import model.game.attributes.GameAttributes;
import model.game.parser.equipment.EquipmentParser;
import model.game.parser.quest.QuestParser;
import model.game.parser.user.UserParser;

import java.io.IOException;

public class GameParser {
    private final TanothHttpClientSingleton httpClient;
    private final QuestParser questParser;
    private final UserParser userParser;
    private final EquipmentParser equipmentParser;
    private final GameAttributes gameAttributes;

    /*
      Method List:
      MiniUpdate GetInboxHeaders GetOutboxHeaders SellItem
      GetGuild GetHighscore GetMapDetails GetPvpData MoveItem
      GetWorkData MerchItems GetMount GetDungeon StartIllusionCave
      GetPremiumData GetParty GetEquipment error RaiseAttribute
      GetUserAttributes GetChatsecret StartAdventure GetAdventures
     */

    public GameParser() throws IOException, InterruptedException {
        httpClient = TanothHttpClientSingleton.getInstance();
        gameAttributes = new GameAttributes();
        questParser = new QuestParser(httpClient, gameAttributes.getQuestAttributes());
        userParser = new UserParser(httpClient, gameAttributes.getUserAttributes());
        equipmentParser = new EquipmentParser(httpClient, gameAttributes.getEquipmentAttributes());
    }

    public UserParser getUserParser() {
        return userParser;
    }

    public QuestParser getQuestParser() {
        return questParser;
    }

    public EquipmentParser getEquipmentParser() {
        return equipmentParser;
    }

    public void reconnect() throws IOException, InterruptedException {
        httpClient.login();
    }

    public GameAttributes getGameAttributes() {
        return gameAttributes;
    }
}

