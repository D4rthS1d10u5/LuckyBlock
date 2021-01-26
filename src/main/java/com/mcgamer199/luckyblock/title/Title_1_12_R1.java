package com.mcgamer199.luckyblock.title;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title_1_12_R1 implements ITitle {

    public Title_1_12_R1() {
    }

    public void sendTitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + msg + "\"}")), fadeIn, stayTime, fadeOut);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
    }

    public void sendSubtitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + msg + "\"}")), fadeIn, stayTime, fadeOut);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
    }

    public void sendActionBar(Player p, String msg, int fadeIn, int stayTime, int fadeOut) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + msg + "\"}")), fadeIn, stayTime, fadeOut);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
    }
}
