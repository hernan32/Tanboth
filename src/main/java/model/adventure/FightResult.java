package model.adventure;

public class FightResult {
    private int fame;
    private int blocked;
    private int crits;
    private int averageDamage;
    private int bestHit;
    private int magicDamage;
    private boolean itemFound;

    public FightResult(int fame, int blocked, int crits, int averageDamage, int bestHit, int magicDamage, boolean itemFound) {
        this.fame = fame;
        this.blocked = blocked;
        this.crits = crits;
        this.averageDamage = averageDamage;
        this.bestHit = bestHit;
        this.magicDamage = magicDamage;
        this.itemFound = itemFound;
    }

    public FightResult() {
    }

    public int getFame() {
        return fame;
    }

    public void setFame(int fame) {
        this.fame = fame;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public int getCrits() {
        return crits;
    }

    public void setCrits(int crits) {
        this.crits = crits;
    }

    public int getAverageDamage() {
        return averageDamage;
    }

    public void setAverageDamage(int averageDamage) {
        this.averageDamage = averageDamage;
    }

    public int getBestHit() {
        return bestHit;
    }

    public void setBestHit(int bestHit) {
        this.bestHit = bestHit;
    }

    public int getMagicDamage() {
        return magicDamage;
    }

    public void setMagicDamage(int magicDamage) {
        this.magicDamage = magicDamage;
    }

    public boolean isItemFound() {
        return itemFound;
    }

    public void setItemFound(boolean itemFound) {
        this.itemFound = itemFound;
    }
}
