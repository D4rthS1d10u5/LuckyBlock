package com.mcgamer199.luckyblock.api.chatcomponent;

import net.md_5.bungee.api.chat.HoverEvent;

public enum Hover {
    show_text(HoverEvent.Action.SHOW_TEXT),
    show_achievement(HoverEvent.Action.SHOW_ACHIEVEMENT),
    show_item(HoverEvent.Action.SHOW_ITEM),
    show_entity(HoverEvent.Action.SHOW_ENTITY),;

    HoverEvent.Action action;

    Hover(HoverEvent.Action action) {this.action = action;}

    public String get(Object o) {
        return String.valueOf(o);
    }
}
