package com.mcgamer199.luckyblock.api.chatcomponent;

import net.md_5.bungee.api.chat.ClickEvent;

public enum Click {
    open_url(ClickEvent.Action.OPEN_URL),
    open_file(ClickEvent.Action.OPEN_FILE),
    run_command(ClickEvent.Action.RUN_COMMAND),
    suggest_command(ClickEvent.Action.SUGGEST_COMMAND),
    change_page(ClickEvent.Action.CHANGE_PAGE),;

    ClickEvent.Action action;

    Click(ClickEvent.Action action) {this.action = action;}

    public String get(Object o) {
        return String.valueOf(o);
    }
}
