package model.game.parser;

import com.esotericsoftware.minlog.Log;
import model.game.parser.exception.TimeOutException;
import model.game.parser.quest.exception.*;
import org.jsoup.nodes.Document;

public interface Validation {
    default boolean isActiveAdventure(Document XML) {
        return XML.getElementsContainingOwnText("running_adventure_id").size() > 0;
    }

    default boolean isActiveIllusionCave(Document XML) {
        return XML.getElementsContainingOwnText("running_event_quest_time_remain").size() > 0;
    }

    default boolean isFightResult(Document XML) {
        return XML.getElementsContainingOwnText("fight_result").size() > 0;
    }

    default boolean isRewardResult(Document XML) {
        return XML.getElementsContainingOwnText("location_id").size() + XML.getElementsContainingOwnText("reward_gold").size() > 0;
    }

    default void printXML(Document XML, String Action) {
        Log.warn("XML @" + Action);
        Log.warn(XML.toString().replace("\n", "").replace("\r", "").replace(" ", ""));
    }

    default boolean timeOut(Document XML) {
        return XML.getElementsContainingOwnText("no_valid_session").size() > 0;
    }

    default void validateResponse(Document XML, String Action) throws TimeOutException, IllusionCaveRunningException, FightResultException, AdventureRunningException, WorkingException, RewardResultException, IllusionDisabledException {
        printXML(XML, Action);
        if (timeOut(XML)) throw new TimeOutException("[ERROR] Connection Time Out @" + Action);
    }
}
