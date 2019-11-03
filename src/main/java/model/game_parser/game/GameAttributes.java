package model.game_parser.game;

public class GameAttributes {

    private int adventuresMade;
    private int freeAdventures;
    private int inventorySpaces;
    private int bossMapMade;
    private int sleep;
    private boolean questStarted;

    public GameAttributes() {
        sleep = 60;
        questStarted = false;
    }

    public int getAdventuresMade() {
        return adventuresMade;
    }

    public void setAdventuresMade(int adventuresMade) {
        this.adventuresMade = adventuresMade;
    }

    public int getFreeAdventures() {
        return freeAdventures;
    }

    public void setFreeAdventures(int freeAdventures) {
        this.freeAdventures = freeAdventures;
    }

    public int getInventorySpaces() {
        return inventorySpaces;
    }

    public void setInventorySpaces(int inventorySpaces) {
        this.inventorySpaces = inventorySpaces;
    }

    public int getBossMapMade() {
        return bossMapMade;
    }

    public void setBossMapMade(int bossMapMade) {
        this.bossMapMade = bossMapMade;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public boolean isQuestStarted() {
        return questStarted;
    }

    public void setQuestStarted(boolean questStarted) {
        this.questStarted = questStarted;
    }

    public String getAllAttributesToString() {
        return "[Quest: " + adventuresMade + " / " + freeAdventures + "] [Boss Map: " + bossMapMade + "/1] [Spaces: " + inventorySpaces + "]";
    }

}