package model.equipment_parser;

import model.GameAction;
import model.TanothHttpClientSingleton;
import model.Validation;
import model.adventure_parser.adventure.exception.AdventureRunningException;
import model.adventure_parser.adventure.exception.FightResultException;
import model.adventure_parser.adventure.exception.IllusionCaveRunningException;
import model.equipment_parser.item.Item;
import model.exception.TimeOutException;
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

    private List<Item> getItems() throws IOException, InterruptedException, AdventureRunningException, FightResultException, TimeOutException, IllusionCaveRunningException {
        String GetEquipment = METHOD_LIST.GetEquipment.name();
        GameAction GameAction = new GameAction.newBuilder(GetEquipment, httpClient.getSessionID()).build();
        Document XML = Jsoup.parse(httpClient.getXMLByAction(GameAction));
        validateResponse(XML, GetEquipment);
        Element dataItemsXML = XML.select("array").select("data").first();
        Elements itemsXML = dataItemsXML.children();
        List<Item> items = new ArrayList<>();
        for (Element e : itemsXML) {
            boolean isEquipped = e.select("member:contains(is_equipped)").select("member > value > boolean").text().equals("1");
            boolean isUnique = e.select("member:contains(is_unique)").select("member > value > boolean").text().equals("1");
            int id = Integer.parseInt(e.select("member:contains(id)").first().select("member > value > i4").text());
            int screenX = Integer.parseInt(e.select("member:contains(screen_x)").select("member > value > i4").text());
            items.add(new Item(id, isEquipped, isUnique, screenX));
        }
        return items;
    }

    private List<Item> getItemsFromInventory() throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException {
        List<Item> Items = getItems();
        List<Item> inventoryItems = new ArrayList<>();
        for (Item i : Items) {
            if (!i.isEquipped()) inventoryItems.add(i);
        }
        return inventoryItems;
    }

    public void sellItemsFromInventory(Boolean isSellingUniques) throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException {
        String SellItem = METHOD_LIST.SellItem.name();
        List<Item> inventoryItems = getItemsFromInventory();
        for (Item item : inventoryItems) {
            if (item.isUnique() == isSellingUniques) {
                GameAction GameAction = new GameAction.newBuilder(SellItem, httpClient.getSessionID()).addParameter(item.getId()).addParameter(0).addParameter(item.getScreenX()).build();
                httpClient.getXMLByAction(GameAction);
            }
        }
    }

    public int getInventorySpace() throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException {
        return getItems().size();
    }

}
