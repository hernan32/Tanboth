package model.adventure.criterias;

import model.adventure.Adventure;
import model.adventure.Criteria;

public class ExpCriteria extends Criteria {
    public int getValueFor(Adventure adventure) {
        return adventure.getExperience();
    }
}