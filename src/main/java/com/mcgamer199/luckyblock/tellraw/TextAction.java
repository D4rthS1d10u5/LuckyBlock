package com.mcgamer199.luckyblock.tellraw;

public class TextAction {
    protected EnumTextEvent event;
    protected EnumTextAction action;
    public Object value;

    public TextAction(EnumTextEvent event, EnumTextAction action, String value) {
        this.event = event;
        this.action = action;
        this.value = value;
    }

    public TextAction(EnumTextEvent event, EnumTextAction action, Object value) {
        this.event = event;
        this.action = action;
        this.value = value;
    }

    public EnumTextAction getAction() {
        return this.action;
    }

    public EnumTextEvent getEvent() {
        return this.event;
    }
}
