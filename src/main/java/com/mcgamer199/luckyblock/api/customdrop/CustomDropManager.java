package com.mcgamer199.luckyblock.api.customdrop;

import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class CustomDropManager {

    private static final List<CustomDrop> customdrops = new ArrayList();

    public static void registerDrop(CustomDrop customdrop) {
        String error = null;
        if (customdrop.getName() == null) {
            error = "n";
        }

        if (error == null) {
            for (int x = 0; x < customdrops.size(); ++x) {
                if (customdrops.get(x).getName().equalsIgnoreCase(customdrop.getName())) {
                    error = "d";
                    break;
                }
            }
        }

        if (error == null) {
            LuckyBlockDrop[] var5;
            int var4 = (var5 = LuckyBlockDrop.values()).length;

            for (int var3 = 0; var3 < var4; ++var3) {
                LuckyBlockDrop drop = var5[var3];
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
        return Collections.unmodifiableList(customdrops);
    }

    public static com.mcgamer199.luckyblock.api.customdrop.CustomDrop getByName(String name) {
        for (int x = 0; x < customdrops.size(); ++x) {
            if (customdrops.get(x).getName().equalsIgnoreCase(name)) {
                return customdrops.get(x);
            }
        }

        return null;
    }

    public static boolean isValid(String name) {
        return getByName(name) != null;
    }
}
