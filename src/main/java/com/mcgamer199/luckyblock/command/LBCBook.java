package com.mcgamer199.luckyblock.command;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Click;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.command.engine.LBCommand;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;

public class LBCBook extends LBCommand {
    public LBCBook() {
    }

    public boolean receive(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                giveBook((Player) sender);
                send(sender, "command.book.1");
            } else {
                send_invalid_sender(sender);
            }
        } else if (args.length == 2) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p != null) {
                giveBook(p);
                String a = val("command.book.2", false);
                a = a.replace("%player%", p.getName());
                send_2(sender, a);
            } else {
                send_invalid_player(sender);
            }
        }

        return true;
    }

    public String getCommandName() {
        return "book";
    }

    public int[] getRequiredArgs() {
        return new int[]{1, 2};
    }

    public String getDescription() {
        return val("desc.cmd.book");
    }

    private void giveBook(Player player) {
        ChatComponent firstPage = new ChatComponent();
        firstPage.addText("§1●●●●●●●●●●●●●●●●●●●●●●●●●●●●\n");
        firstPage.addText("§a[§6Lucky Block§a]\n\n");
        firstPage.addText("§bMade by §4MCGamer199\n\n");
        firstPage.addText("§1-You can use /lb help\n", Hover.show_text, "§eClick to run", Click.run_command, String.format("/%s help", ILBCmd.lcmd));
        firstPage.addText("§1for list of commands.\n\n");
        firstPage.addText("§1-Use Tab key to complete an argument.\n\n\n\n");
        firstPage.addText("§1●●●●●●●●●●●●●●●●●●●●●●●●●●●●\n");

        ChatComponent secondPage = new ChatComponent();
        secondPage.addText("§1●●●●●●●●●●●●●●●●●●●●●●●●●●●●\n");
        secondPage.addText("§2Available lucky blocks:\n");

        for (int i = 0; i < LBType.getTypes().size(); i++) {
            if(i < 12) {
                LBType type = LBType.getTypes().get(i);
                secondPage.addText(type.getName() + "\n", Hover.show_text, "§eClick to get", Click.run_command, String.format("/%s give %s 1 0 %s", ILBCmd.lcmd, player.getName(), type.getId()));
            }
        }

        secondPage.addText("§1●●●●●●●●●●●●●●●●●●●●●●●●●●●●\n");

        ChatComponent thirdPage = new ChatComponent();
        thirdPage.addText("§1●●●●●●●●●●●●●●●●●●●●●●●●●●●●\n");
        thirdPage.addText("§3Lucky Crafting Table:\n§a-Crafting recipe:\n§68x Gold Ingot\n§51x Crafting Table\n");
        thirdPage.addText("§9-Can be upgraded by\n§9right clicking with 8\n§9emerald blocks.\n§9-Max level is 10\n\n");
        thirdPage.addText("§6§bClick here go get\n\n\n", Hover.show_text, "§eClick here", Click.run_command, String.format("/%s lct", ILBCmd.lcmd));

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setAuthor("MCGamer199");
        meta.setTitle("§e§bLucky Block Book");
        book.setItemMeta(meta);

        player.getInventory().addItem(ItemStackUtils.setPages(book, Arrays.asList(firstPage, secondPage, thirdPage)));
    }
}
