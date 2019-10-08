package model.game_parser.adventure_parser.adventure;

public abstract class AdventureCriteria {
    public abstract AdventureAttributes getBestFor(AdventureAttributes localAdventure, AdventureAttributes parameterAdventure);
}