package model.game_parser.adventure_parser.adventure.criterias;

import model.game_parser.adventure_parser.adventure.AdventureAttributes;
import model.game_parser.adventure_parser.adventure.AdventureCriteria;

public class ExpCriteria extends AdventureCriteria {
    public AdventureAttributes getBestFor(AdventureAttributes localAdventure, AdventureAttributes parameterAdventure) {
        if (localAdventure.getExperience() > parameterAdventure.getExperience()) return localAdventure;
        else return parameterAdventure;
    }
}