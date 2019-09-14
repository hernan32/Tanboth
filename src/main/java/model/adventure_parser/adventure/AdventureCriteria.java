package model.adventure_parser.adventure;

public abstract class AdventureCriteria {
    public abstract Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure);
}