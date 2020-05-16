package model.game.attributes.quest.adventure;

public abstract class AdventureCriteria {
    public abstract Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure);
}