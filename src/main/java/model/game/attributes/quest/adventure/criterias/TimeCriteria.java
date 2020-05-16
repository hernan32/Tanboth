package model.game.attributes.quest.adventure.criterias;

import model.game.attributes.quest.adventure.Adventure;
import model.game.attributes.quest.adventure.AdventureCriteria;

public class TimeCriteria extends AdventureCriteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getDuration() > parameterAdventure.getDuration()) {
            return parameterAdventure;
        } else {
            return localAdventure;
        }
    }
}

