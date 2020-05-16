package model.game.attributes.equipment;

import model.game.attributes.equipment.item.Item;

import java.util.List;

public class EquipmentAttributes {
    private List<Item> characterItems;
    private List<Item> partyItems;
    private List<Item> inventoryItems;
    private int inventoryUsedSpaces;

    public EquipmentAttributes() {
    }

    public List<Item> getCharacterItems() {
        return characterItems;
    }

    public void setCharacterItems(List<Item> characterItems) {
        this.characterItems = characterItems;
    }

    public List<Item> getPartyItems() {
        return partyItems;
    }

    public void setPartyItems(List<Item> partyItems) {
        this.partyItems = partyItems;
    }

    public List<Item> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<Item> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public int getInventoryUsedSpaces() {
        return inventoryUsedSpaces;
    }

    public int setInventoryUsedSpaces(int spaces) {
        return inventoryUsedSpaces = spaces;
    }
}
