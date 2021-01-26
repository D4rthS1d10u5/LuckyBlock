package com.mcgamer199.luckyblock.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LanguageChangedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String lang;

    public LanguageChangedEvent(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return this.lang;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
