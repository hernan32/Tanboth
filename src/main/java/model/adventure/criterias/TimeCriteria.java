package model.adventure.criterias;

import model.adventure.Adventure;
import model.adventure.Criteria;

public class TimeCriteria extends Criteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getDuration() > parameterAdventure.getDuration()) {
            return parameterAdventure;
        } else {
            return localAdventure;
        }
    }
}

