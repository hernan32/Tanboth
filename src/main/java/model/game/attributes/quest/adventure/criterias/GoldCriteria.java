package model.game.attributes.quest.adventure.criterias;

import model.game.attributes.quest.adventure.Adventure;
import model.game.attributes.quest.adventure.AdventureCriteria;

public class GoldCriteria extends AdventureCriteria {
    public Adventure getBestFor(Adventure localAdventure, Adventure parameterAdventure) {
        if (localAdventure.getGold() > parameterAdventure.getGold()) return localAdventure;
        else return parameterAdventure;
    }
}