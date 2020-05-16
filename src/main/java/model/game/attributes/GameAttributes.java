package model.game.attributes;

import model.game.attributes.equipment.EquipmentAttributes;
import model.game.attributes.quest.QuestAttributes;
import model.game.attributes.user.UserAttributes;

public class GameAttributes {

    private final QuestAttributes questAttributes;
    private final UserAttributes userAttributes;
    private final EquipmentAttributes equipmentAttributes;
    private int sleep;

    public GameAttributes() {
        sleep = 60;
        userAttributes = new UserAttributes();
        equipmentAttributes = new EquipmentAttributes();
        questAttributes = new QuestAttributes();
    }

    public EquipmentAttributes getEquipmentAttributes() {
        return equipmentAttributes;
    }

    public UserAttributes getUserAttributes() {
        return userAttributes;
    }

    public QuestAttributes getQuestAttributes() {
        return questAttributes;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

}
