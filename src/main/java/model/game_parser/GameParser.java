package model.game_parser;

import model.TanothHttpClientSingleton;
import model.game_parser.adventure_parser.AdventureParser;
import model.game_parser.equipment_parser.EquipmentParser;
import model.game_parser.game.GameAttributes;
import model.game_parser.user_parser.UserParser;

import java.io.IOException;

public class GameParser {
    private TanothHttpClientSingleton httpClient;
    private AdventureParser adventureParser;
    private UserParser userParser;
    private EquipmentParser equipmentParser;
    private GameAttributes gameAttributes;

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
        adventureParser = new AdventureParser(httpClient);
        userParser = new UserParser(httpClient);
        equipmentParser = new EquipmentParser(httpClient);
        gameAttributes = new GameAttributes();

    }

    public UserParser getUserParser() {
        return userParser;
    }

    public AdventureParser getAdventureParser() {
        return adventureParser;
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

