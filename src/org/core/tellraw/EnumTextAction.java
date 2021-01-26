package org.core.tellraw;

public enum EnumTextAction {
    SHOW_TEXT("show_text"),
    RUN_COMMAND("run_command"),
    OPEN_URL("open_url"),
    SUGGEST_COMMAND("suggest_command"),
    SHOW_ITEM("show_item");

    private final String name;

    EnumTextAction(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
