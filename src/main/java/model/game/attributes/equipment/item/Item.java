package model.game.attributes.equipment.item;

public class Item {
    private int id;
    private boolean isEquipped;
    private boolean isUnique;
    private int screenX;
    private int sellValue;
    private int armor; //value1
    private int bonusDEX, bonusINT, bonusSTR, bonusCON;

    public Item(int id, boolean isEquipped, boolean isUnique, int screenX, int sellValue, int armor, int bonusDEX, int bonusINT, int bonusSTR, int bonusCON) {
        this.id = id;
        this.isEquipped = isEquipped;
        this.isUnique = isUnique;
        this.screenX = screenX;
        this.sellValue = sellValue;
        this.armor = armor;
        this.bonusDEX = bonusDEX;
        this.bonusINT = bonusINT;
        this.bonusSTR = bonusSTR;
        this.bonusCON = bonusCON;
    }

    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public int getScreenX() {
        return screenX;
    }

    public void setScreenX(int screenX) {
        this.screenX = screenX;
    }

    public int getSellValue() {
        return sellValue;
    }

    public void setSellValue(int sellValue) {
        this.sellValue = sellValue;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getBonusDEX() {
        return bonusDEX;
    }

    public void setBonusDEX(int bonusDEX) {
        this.bonusDEX = bonusDEX;
    }

    public int getBonusINT() {
        return bonusINT;
    }

    public void setBonusINT(int bonusINT) {
        this.bonusINT = bonusINT;
    }

    public int getBonusSTR() {
        return bonusSTR;
    }

    public void setBonusSTR(int bonusSTR) {
        this.bonusSTR = bonusSTR;
    }

    public int getBonusCON() {
        return bonusCON;
    }

    public void setBonusCON(int bonusCON) {
        this.bonusCON = bonusCON;
    }


}
