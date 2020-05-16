package model.game.attributes.user;

public class UserAttributes {
    private int gold;
    private int CON;
    private int DEX;
    private int INT;
    private int STR;
    private int CONcost;
    private int DEXcost;
    private int INTcost;
    private int STRcost;
    private int bloodStones;

    public UserAttributes() {
    }

    public int getBloodStones() {
        return bloodStones;
    }

    public void setBloodStones(int bloodStones) {
        this.bloodStones = bloodStones;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getCON() {
        return CON;
    }

    public void setCON(int CON) {
        this.CON = CON;
    }

    public int getDEX() {
        return DEX;
    }

    public void setDEX(int DEX) {
        this.DEX = DEX;
    }

    public int getINT() {
        return INT;
    }

    public void setINT(int INT) {
        this.INT = INT;
    }

    public int getSTR() {
        return STR;
    }

    public void setSTR(int STR) {
        this.STR = STR;
    }

    public int getCONcost() {
        return CONcost;
    }

    public void setCONcost(int CONcost) {
        this.CONcost = CONcost;
    }

    public int getDEXcost() {
        return DEXcost;
    }

    public void setDEXcost(int DEXcost) {
        this.DEXcost = DEXcost;
    }

    public int getINTcost() {
        return INTcost;
    }

    public void setINTcost(int INTcost) {
        this.INTcost = INTcost;
    }

    public int getSTRcost() {
        return STRcost;
    }

    public void setSTRcost(int STRcost) {
        this.STRcost = STRcost;
    }

}
