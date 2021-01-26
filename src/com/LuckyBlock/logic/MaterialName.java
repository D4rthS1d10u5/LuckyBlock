package com.LuckyBlock.logic;

import org.bukkit.Material;

public class MaterialName {
    public MaterialName() {
    }

    public static String getName(Material mat) {
        String s = mat.name();
        String[] d = s.split("WOOD_");
        if (d.length == 2) {
            s = "wooden_" + d[1];
        }

        d = s.split("GOLD_");
        if (d.length == 2) {
            s = "golden_" + d[1];
        }

        s = s.toLowerCase();
        return s;
    }
}
