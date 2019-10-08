package model.game_parser.equipment_parser.item;

public class ItemAttributes {
    private int id;
    private boolean isEquipped;
    private boolean isUnique;
    private int screenX;

    public ItemAttributes(int id, boolean isEquipped, boolean isUnique, int screenX) {
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
