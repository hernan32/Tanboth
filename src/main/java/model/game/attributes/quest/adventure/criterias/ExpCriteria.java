package model.game.attributes.quest.adventure.criterias;

import model.game.attributes.quest.adventure.Adventure;
import model.game.attributes.quest.adventure.AdventureCriteria;

public class ExpCriteria extends AdventureCriteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getExperience() > parameterAdventure.getExperience()) return localAdventure;
        else return parameterAdventure;
    }
}