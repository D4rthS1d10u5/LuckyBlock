package com.mcgamer199.luckyblock.api;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class PluginConstants {

    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
}
