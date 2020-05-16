package model.game.attributes.quest.adventure;

public class Adventure {
    private final int difficulty;
    private final int duration;
    private final int experience;
    private final int fightChance;
    private final int gold;
    private final int questID;

    public Adventure(int difficulty, int duration, int experience, int fightChance, int gold, int questID) {
        this.difficulty = difficulty;
        this.duration = duration;
        this.experience = experience;
        this.fightChance = fightChance;
        this.gold = gold;
        this.questID = questID;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getDuration() {
        return duration;
    }

    public int getExperience() {
        return experience;
    }

    public int getFightChance() {
        return fightChance;
    }

    public int getGold() {
        return gold;
    }

    public int getQuestID() {
        return questID;
    }

    public Adventure getBest(AdventureCriteria c, Adventure adventure) {
        return c.getBestFor(this, adventure);
    }


}
