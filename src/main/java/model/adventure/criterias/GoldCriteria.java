package model.adventure.criterias;

import model.adventure.Adventure;
import model.adventure.Criteria;

public class GoldCriteria extends Criteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getGold() > parameterAdventure.getGold()) return localAdventure;
        else return parameterAdventure;
    }
}