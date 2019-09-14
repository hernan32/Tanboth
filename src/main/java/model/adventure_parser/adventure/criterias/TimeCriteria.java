package model.adventure_parser.adventure.criterias;

import model.adventure_parser.adventure.Adventure;
import model.adventure_parser.adventure.AdventureCriteria;

public class TimeCriteria extends AdventureCriteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getDuration() > parameterAdventure.getDuration()) {
            return parameterAdventure;
        } else {
            return localAdventure;
        }
    }
}

