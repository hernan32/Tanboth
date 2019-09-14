package model;

import model.adventure_parser.adventure.exception.AdventureRunningException;
import model.adventure_parser.adventure.exception.FightResultException;
import model.adventure_parser.adventure.exception.IllusionCaveRunningException;
import model.exception.TimeOutException;
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

    default boolean timeOut(Document XML) {
        return XML.getElementsContainingOwnText("no_valid_session").size() > 0;
    }

    default void validateResponse(Document XML, String Action) throws TimeOutException, IllusionCaveRunningException, FightResultException, AdventureRunningException {
        if (timeOut(XML)) throw new TimeOutException("[ERROR] Connection Time Out @" + Action);
    }
}
