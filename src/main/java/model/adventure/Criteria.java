package model.adventure;

public abstract class Criteria {
    public abstract Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure);
}