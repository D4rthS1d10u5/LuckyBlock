package com.mcgamer199.luckyblock.logic;

import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.mcgamer199.luckyblock.api.book.BookMaker;
import com.mcgamer199.luckyblock.api.book.BookPage;
import com.mcgamer199.luckyblock.tellraw.EnumTextAction;
import com.mcgamer199.luckyblock.tellraw.EnumTextEvent;
import com.mcgamer199.luckyblock.tellraw.RawText;
import com.mcgamer199.luckyblock.tellraw.TextAction;

public class LBBook extends ColorsClass {
    public LBBook() {
    }

    public static void giveBook(Player player) {
        RawText[] texts = new RawText[8];
        String cc = "●●●●";
        String c = darkblue + cc + cc + cc + cc + cc + cc + cc + "\n";
        texts[0] = new RawText(c);
        texts[1] = new RawText(green + "[" + gold + "Lucky Block" + green + "]\n\n");
        texts[2] = new RawText(blue + "Made by " + red + "MCGamer199\n\n");
        texts[3] = new RawText(darkblue + "-You can use ");
        texts[4] = new RawText(gold + "/lb help\n");
        texts[4].addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, yellow + "Click to run"));
        texts[4].addAction(new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.RUN_COMMAND, "/" + ILBCmd.lcmd + " help"));
        texts[5] = new RawText(darkblue + "for list of commands.\n\n");
        texts[6] = new RawText(darkblue + "-Use Tab key to " + darkblue + "complete an argument.\n\n\n\n");
        texts[7] = new RawText(c);
        BookPage page = new BookPage(texts);
        RawText[] tpage = new RawText[14];
        tpage[0] = new RawText(c);
        tpage[1] = new RawText(darkgreen + "Available lucky blocks:\n");
        int next = 0;

        for(int x = 0; x < LBType.getTypes().size(); ++x) {
            if (x < 12) {
                LBType type = (LBType)LBType.getTypes().get(x);
                tpage[x + 2] = new RawText(type.getName() + "\n");
                tpage[x + 2].addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, yellow + "Click to get"));
                tpage[x + 2].addAction(new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.RUN_COMMAND, "/" + ILBCmd.lcmd + " give " + player.getName() + " 1 0 " + type.getId()));
                next = x + 2;
            }
        }

        ++next;
        String t = "";

        for(int x = next; x < 13; ++x) {
            t = t + "\n";
        }

        tpage[next] = new RawText(t + c);
        BookPage page2 = new BookPage(tpage);
        RawText[] tpage1 = new RawText[]{new RawText(c), new RawText(darkaqua + "Lucky Crafting Table:\n" + green + "-Crafting Recipe:\n" + gold + "8x Gold Ingot\n" + darkpurple + "1x Crafting Table\n"), new RawText(blue + "-Can be upgraded by\n" + blue + "right clicking with 8\n" + blue + "emerald blocks.\n" + blue + "-Max Level is 10\n\n"), new RawText("" + gold + bold + "Click here to get\n\n\n"), null};
        tpage1[3].addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, yellow + "Click here"));
        tpage1[3].addAction(new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.RUN_COMMAND, "/" + ILBCmd.lcmd + " lct"));
        tpage1[4] = new RawText(c);
        BookPage page3 = new BookPage(tpage1);
        BookMaker.IBook book = BookMaker.createNewBook("MCGamer199", "" + yellow + bold + "Lucky Block Book", new BookPage[]{page, page2, page3});
        BookMaker.giveBook(book, player);
    }

    public static void giveLCTBook(Player player) {
        RawText[] texts = new RawText[8];
        texts[0] = new RawText("Lucky Crafting Table");
        texts[1] = new RawText("Click to get");
        texts[1].addAction(new TextAction(EnumTextEvent.CLICK_EVENT, EnumTextAction.RUN_COMMAND, "/" + ILBCmd.lcmd + " lct"));
        texts[1].addAction(new TextAction(EnumTextEvent.HOVER_EVENT, EnumTextAction.SHOW_TEXT, ChatColor.YELLOW + "Click to get a lct!"));
    }
}
