package com.LuckyBlock.customdrop;

import com.LuckyBlock.LB.LBDrop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomDropManager {
    private static List<CustomDrop> customdrops = new ArrayList();

    public CustomDropManager() {
    }

    public static void registerDrop(CustomDrop customdrop) {
        String error = null;
        if (customdrop.getName() == null) {
            error = "n";
        }

        if (error == null) {
            for(int x = 0; x < customdrops.size(); ++x) {
                if (((CustomDrop)customdrops.get(x)).getName().equalsIgnoreCase(customdrop.getName())) {
                    error = "d";
                    break;
                }
            }
        }

        if (error == null) {
            LBDrop[] var5;
            int var4 = (var5 = LBDrop.values()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                LBDrop drop = var5[var3];
                if (customdrop.getName().equalsIgnoreCase(drop.name())) {
                    error = "d";
                    break;
                }
            }
        }

        if (error != null) {
            if (error.equalsIgnoreCase("d")) {
                throw new Error("Could not register CustomDrop: " + customdrop.getName() + " (Name duplication exception)");
            }

            if (error.equalsIgnoreCase("n")) {
                throw new NullPointerException("Drop name cannot be null!");
            }
        } else {
            customdrops.add(customdrop);
        }

    }

    public static List<CustomDrop> getCustomDrops() {
        List<CustomDrop> drops = new ArrayList();
        Iterator var2 = customdrops.iterator();

        while(var2.hasNext()) {
            CustomDrop drop = (CustomDrop)var2.next();
            drops.add(drop);
        }

        return drops;
    }

    public static CustomDrop getByName(String name) {
        for(int x = 0; x < customdrops.size(); ++x) {
            if (((CustomDrop)customdrops.get(x)).getName().equalsIgnoreCase(name)) {
                return (CustomDrop)customdrops.get(x);
            }
        }

        return null;
    }

    public static boolean isValid(String name) {
        return getByName(name) != null;
    }
}
