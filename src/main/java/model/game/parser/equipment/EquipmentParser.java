package model.game.parser.equipment;

import model.TanothHttpClientSingleton;
import model.game.attributes.equipment.EquipmentAttributes;
import model.game.attributes.equipment.item.Item;
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

public class EquipmentParser implements Validation {

    private final TanothHttpClientSingleton httpClient;
    private final EquipmentAttributes equipmentAttributes;

    public EquipmentParser(TanothHttpClientSingleton httpClient, EquipmentAttributes equipmentAttributes) {
        this.httpClient = httpClient;
        this.equipmentAttributes = equipmentAttributes;
    }

    public void getItems() throws IOException, InterruptedException, AdventureRunningException, FightResultException, TimeOutException, IllusionCaveRunningException, WorkingException, RewardResultException, IllusionDisabledException {
        String getEquipment = METHOD_LIST.GetEquipment.name();
        GameAction GameAction = new GameAction.newBuilder(getEquipment, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        validateResponse(XML, getEquipment);
        Element dataItemsXML = XML.select("array").select("data").first();
        Elements itemsXML = dataItemsXML.children();
        List<Item> items = new ArrayList<>();
        boolean isEquipped, isUnique;
        int id, screenX;
        int bonusDEX, bonusSTR, bonusCON, bonusINT;
        int sellValue, armor;
        for (Element e : itemsXML) {
            sellValue = armor = bonusDEX = bonusSTR = bonusCON = bonusINT = 0;
            isEquipped = e.select("member:contains(is_equipped)").select("member > value > boolean").text().equals("1");
            isUnique = e.select("member:contains(is_unique)").select("member > value > boolean").text().equals("1");
            id = Integer.parseInt(e.select("member:contains(id)").first().select("member > value > i4").text());
            screenX = Integer.parseInt(e.select("member:contains(screen_x)").select("member > value > i4").text());
            try {
                bonusDEX = Integer.parseInt(e.select("member:contains(bonus_dex)").select("member > value > i4").text());
                bonusSTR = Integer.parseInt(e.select("member:contains(bonus_str)").select("member > value > i4").text());
                bonusCON = Integer.parseInt(e.select("member:contains(bonus_con)").select("member > value > i4").text());
                bonusINT = Integer.parseInt(e.select("member:contains(bonus_int)").select("member > value > i4").text());
                armor = Integer.parseInt(e.select("member:contains(value1)").select("member > value > i4").text());
                sellValue = Integer.parseInt(e.select("member:contains(sellvalue)").select("member > value > i4").text());
            } catch (Exception ex) {
                ex.getCause();
            }
            items.add(new Item(id, isEquipped, isUnique, screenX, sellValue, armor, bonusDEX, bonusSTR, bonusCON, bonusINT));
        }
        equipmentAttributes.setCharacterItems(items);
        equipmentAttributes.setInventoryUsedSpaces(items.size());
    }

    private void getItemsFromInventory() throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException, WorkingException, RewardResultException, IllusionDisabledException {
        getItems();
        List<Item> inventoryItems = new ArrayList<>();
        List<Item> Items = equipmentAttributes.getCharacterItems();
        for (Item i : Items) {
            if (!i.isEquipped()) inventoryItems.add(i);
        }
        equipmentAttributes.setInventoryItems(inventoryItems);
    }

    public void sellItemsFromInventory(Boolean isSellingUniques) throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException, WorkingException, RewardResultException, IllusionDisabledException {
        String SellItem = METHOD_LIST.SellItem.name();
        getItemsFromInventory();
        List<Item> inventoryItems = equipmentAttributes.getInventoryItems();
        for (Item item : inventoryItems) {
            if (item.isUnique() == isSellingUniques) {
                GameAction GameAction = new GameAction.newBuilder(SellItem, httpClient.getSessionID()).addParameter(item.getId()).addParameter(0).addParameter(item.getScreenX()).build();
                httpClient.getXMLByAction(GameAction);
            }
        }
    }

    enum METHOD_LIST {
        SellItem, MoveItem,
        MerchItems, GetEquipment,
        GetPartyItems
    }

}
