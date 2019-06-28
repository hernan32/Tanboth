public class Adventure {
    private int difficulty;
    private int duration;
    private int experience;
    private int fightChance;
    private int gold;
    private int questID;

    public Adventure(int difficulty, int duration, int experience, int fightChance, int gold, int questID) {
        this.difficulty = difficulty;
        this.duration = duration;
        this.experience = experience;
        this.fightChance = fightChance;
        this.gold = gold;
        this.questID = questID;
    }

    public int getDifficulty() {return difficulty;}

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
}
