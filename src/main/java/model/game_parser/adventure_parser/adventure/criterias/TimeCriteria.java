package model.game_parser.adventure_parser.adventure.criterias;

import model.game_parser.adventure_parser.adventure.AdventureAttributes;
import model.game_parser.adventure_parser.adventure.AdventureCriteria;

public class TimeCriteria extends AdventureCriteria {
    public AdventureAttributes getBestFor(AdventureAttributes localAdventure, AdventureAttributes parameterAdventure) {
        if (localAdventure.getDuration() > parameterAdventure.getDuration()) {
            return parameterAdventure;
        } else {
            return localAdventure;
        }
    }
}

