package model.game_parser.equipment_parser.item;


import org.jsoup.nodes.Element;

public abstract class ItemQualityCriteria {
    public abstract boolean isMatchingQuality(Element e, ItemQualityCriteria iq);
}