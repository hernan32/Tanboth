package model.equipment_parser.item;

public class Item {
    private int id;
    private boolean isEquipped;
    private boolean isUnique;
    private int screenX;

    public Item(int id, boolean isEquipped, boolean isUnique, int screenX) {
        this.id = id;
        this.isEquipped = isEquipped;
        this.isUnique = isUnique;
        this.screenX = screenX;
    }

    public int getId() {
        return id;
    }

    public int getScreenX() {
        return screenX;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public boolean isUnique() {
        return isUnique;
    }

}
