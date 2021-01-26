package org.core.tellraw;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TellRawSender {
    public TellRawSender() {
    }

    public static RawText[] getFixedRawTexts(RawText[] texts) {
        RawText[] t = null;
        int i = 0;
        int g;
        if (texts != null && texts.length > 0) {
            for(g = 0; g < texts.length; ++g) {
                if (texts[g] != null) {
                    ++i;
                }
            }
        }

        if (i > 0) {
            t = new RawText[i];
            g = 0;

            for(int x = 0; x < texts.length; ++x) {
                if (texts[x] != null) {
                    t[g] = texts[x];
                    ++g;
                }
            }
        }

        return t;
    }

    public static void sendTo(Player to, List<RawText> texts) {
        List<RawText> ftexts = texts;

        for(int x = 0; x < ftexts.size(); ++x) {
            if (ftexts.get(x) == null) {
                ftexts.remove(x);
            }
        }

        RawText[] t = new RawText[ftexts.size()];

        for(int x = 0; x < ftexts.size(); ++x) {
            t[x] = (RawText)ftexts.get(x);
        }

        sendTo(to, t);
    }

    public static void sendTo(Player to, RawText... texts) {
        String cmd = "tellraw " + to.getName() + " [";
        if (texts != null && texts.length > 0) {
            for(int x = 0; x < texts.length; ++x) {
                if (texts[x] != null) {
                    RawText text = texts[x];
                    cmd = cmd + "{\"text\":\"" + text.getText() + "\"";
                    if (text.color != null) {
                        cmd = cmd + ",\"color\":\"" + text.color.name().toLowerCase() + "\"";
                    }

                    if (text.bold) {
                        cmd = cmd + ",\"bold\":true";
                    }

                    if (text.strikethrough) {
                        cmd = cmd + ",s\"trikethrough\":true";
                    }

                    if (text.underline) {
                        cmd = cmd + ",\"underline\":true";
                    }

                    if (text.italic) {
                        cmd = cmd + ",\"italic\":true";
                    }

                    if (text.magic) {
                        cmd = cmd + ",\"obfuscated\":true";
                    }

                    for(int i = 0; i < text.actions.length; ++i) {
                        if (text.actions[i] != null) {
                            TextAction action = text.actions[i];
                            if (action.action == EnumTextAction.SHOW_ITEM) {
                                if (action.value instanceof ItemStack) {
                                    cmd = cmd + ",\"" + action.event.getName() + "\":{\"action\":\"" + action.action.getName() + "\",\"value\":\"" + ItemText.itemToString((ItemStack)action.value) + "\"}";
                                }
                            } else {
                                cmd = cmd + ",\"" + action.event.getName() + "\":{\"action\":\"" + action.action.getName() + "\",\"value\":\"" + action.value.toString() + "\"}";
                            }
                        }
                    }

                    cmd = cmd + "}";
                    if (texts.length > x + 1) {
                        cmd = cmd + ",";
                    }
                }
            }

            cmd = cmd + "]";
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }
}
