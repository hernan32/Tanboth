package model.game_parser.equipment_parser;

import model.TanothHttpClientSingleton;
import model.game_parser.GameAction;
import model.game_parser.Validation;
import model.game_parser.adventure_parser.adventure.exception.*;
import model.game_parser.equipment_parser.item.ItemAttributes;
import model.game_parser.game.exception.TimeOutException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentParser implements Validation {

    private TanothHttpClientSingleton httpClient;

    enum METHOD_LIST {
        SellItem, MoveItem,
        MerchItems, GetEquipment
    }


    public EquipmentParser(TanothHttpClientSingleton httpClient) {
        this.httpClient = httpClient;
    }

    private List<ItemAttributes> getItems() throws IOException, InterruptedException, AdventureRunningException, FightResultException, TimeOutException, IllusionCaveRunningException, WorkingException, RewardResultException, IllusionDisabledException {
        String GetEquipment = METHOD_LIST.GetEquipment.name();
        GameAction GameAction = new GameAction.newBuilder(GetEquipment, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        validateResponse(XML, GetEquipment);
        Element dataItemsXML = XML.select("array").select("data").first();
        Elements itemsXML = dataItemsXML.children();
        List<ItemAttributes> items = new ArrayList<>();
        for (Element e : itemsXML) {
            boolean isEquipped = e.select("member:contains(is_equipped)").select("member > value > boolean").text().equals("1");
            boolean isUnique = e.select("member:contains(is_unique)").select("member > value > boolean").text().equals("1");
            int id = Integer.parseInt(e.select("member:contains(id)").first().select("member > value > i4").text());
            int screenX = Integer.parseInt(e.select("member:contains(screen_x)").select("member > value > i4").text());
            items.add(new ItemAttributes(id, isEquipped, isUnique, screenX));
        }
        return items;
    }

    private List<ItemAttributes> getItemsFromInventory() throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException, WorkingException, RewardResultException, IllusionDisabledException {
        List<ItemAttributes> Items = getItems();
        List<ItemAttributes> inventoryItems = new ArrayList<>();
        for (ItemAttributes i : Items) {
            if (!i.isEquipped()) inventoryItems.add(i);
        }
        return inventoryItems;
    }

    public void sellItemsFromInventory(Boolean isSellingUniques) throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException, WorkingException, RewardResultException, IllusionDisabledException {
        String SellItem = METHOD_LIST.SellItem.name();
        List<ItemAttributes> inventoryItems = getItemsFromInventory();
        for (ItemAttributes item : inventoryItems) {
            if (item.isUnique() == isSellingUniques) {
                GameAction GameAction = new GameAction.newBuilder(SellItem, httpClient.getSessionID()).addParameter(item.getId()).addParameter(0).addParameter(item.getScreenX()).build();
                httpClient.getXMLByAction(GameAction);
            }
        }
    }

    public int getInventorySpace() throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException, WorkingException, RewardResultException, IllusionDisabledException {
        return getItems().size();
    }

}
