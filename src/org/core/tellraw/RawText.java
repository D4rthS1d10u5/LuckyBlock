package org.core.tellraw;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RawText {

    private String text;
    public ChatColor color;
    public boolean bold;
    public boolean underline;
    public boolean strikethrough;
    public boolean italic;
    public boolean magic;
    protected TextAction[] actions = new TextAction[2];

    public RawText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void addAction(TextAction action) {
        if (this.actions[0] == null) {
            this.actions[0] = action;
        } else {
            if (this.actions[1] != null) {
                throw new Error("Couldn't add action!");
            }

            this.actions[1] = action;
        }

    }

    public TextAction[] getActions() {
        return this.actions;
    }

    public void sendTo(Player... players) {
        Player[] var5 = players;
        int var4 = players.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            Player p = var5[var3];
            TellRawSender.sendTo(p, new RawText[]{this});
        }

    }
}
