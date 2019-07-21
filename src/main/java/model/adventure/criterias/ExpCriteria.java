package model.adventure.criterias;

import model.adventure.Adventure;
import model.adventure.Criteria;

public class ExpCriteria extends Criteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getExperience() > parameterAdventure.getExperience()) return localAdventure;
        else return parameterAdventure;
    }
}