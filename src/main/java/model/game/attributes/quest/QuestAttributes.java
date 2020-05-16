package model.game.attributes.quest;

import model.game.attributes.quest.adventure.Adventure;

public class QuestAttributes {
    private Adventure currentAdventure;
    private int adventuresMade;
    private int freeAdventures;
    private int illusionCaveMade;
    private int dungeonsMade;
    private int freeDungeons;

    public QuestAttributes() {
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

    public int getIllusionCaveMade() {
        return illusionCaveMade;
    }

    public void setIllusionCaveMade(int illusionCaveMade) {
        this.illusionCaveMade = illusionCaveMade;
    }

    public int getDungeonsMade() {
        return dungeonsMade;
    }

    public void setDungeonsMade(int dungeonsMade) {
        this.dungeonsMade = dungeonsMade;
    }

    public int getFreeDungeons() {
        return freeDungeons;
    }

    public void setFreeDungeons(int freeDungeons) {
        this.freeDungeons = freeDungeons;
    }

    public Adventure getCurrentAdventure() {
        return currentAdventure;
    }

    public void setCurrentAdventure(Adventure currentAdventure) {
        this.currentAdventure = currentAdventure;
    }
}
