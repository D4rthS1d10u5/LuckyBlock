package com.mcgamer199.luckyblock.tellraw;

public enum EnumTextEvent {
    CLICK_EVENT("clickEvent"),
    HOVER_EVENT("hoverEvent");

    private final String name;

    EnumTextEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
