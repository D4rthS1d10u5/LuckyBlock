package com.mcgamer199.luckyblock.api.customdrop;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@UtilityClass
@Log
public class CustomDropManager {

    private static final Map<String, CustomDrop> customDrops = new HashMap<>();

    public static void registerDrop(@NotNull CustomDrop customDrop) {
        String customDropName = customDrop.getName();
        if(customDropName == null) {
            throw new NullPointerException("Drop name cannot be null");
        }

        if(customDrops.containsKey(customDropName)) {
            log.warning("Duplicated name of custom drop. Registration attempt was cancelled.");
            return;
        }

        customDrops.put(customDropName.toUpperCase(), customDrop);
    }

    public static List<CustomDrop> getCustomDrops() {
        return Collections.unmodifiableList(new ArrayList<>(customDrops.values()));
    }

    public static CustomDrop getByName(String name) {
        return customDrops.get(name);
    }

    public static boolean isValid(String name) {
        return name != null && customDrops.containsKey(name.toUpperCase());
    }
}
