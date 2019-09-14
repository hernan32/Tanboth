package model.adventure_parser.adventure.criterias;

import model.adventure_parser.adventure.Adventure;
import model.adventure_parser.adventure.AdventureCriteria;

public class GoldCriteria extends AdventureCriteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getGold() > parameterAdventure.getGold()) return localAdventure;
        else return parameterAdventure;
    }
}