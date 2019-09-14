package model;

import model.adventure_parser.AdventureParser;
import model.equipment_parser.EquipmentParser;
import model.user_parser.UserParser;

import java.io.IOException;

public class GameParser {
    private TanothHttpClientSingleton httpClient;
    private AdventureParser adventureParser;
    private UserParser userParser;
    private EquipmentParser equipmentParser;

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
}

