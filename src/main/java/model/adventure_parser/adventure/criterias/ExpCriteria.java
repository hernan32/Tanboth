package model.adventure_parser.adventure.criterias;

import model.adventure_parser.adventure.Adventure;
import model.adventure_parser.adventure.AdventureCriteria;

public class ExpCriteria extends AdventureCriteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getExperience() > parameterAdventure.getExperience()) return localAdventure;
        else return parameterAdventure;
    }
}