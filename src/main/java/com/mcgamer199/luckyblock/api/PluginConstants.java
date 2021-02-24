package com.mcgamer199.luckyblock.api;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.function.Predicate;

@UtilityClass
public class PluginConstants {

    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static Predicate<CustomEntity> CUSTOM_ENTITY_VALID = customEntity -> customEntity.getLinkedEntity() != null && customEntity.getLinkedEntity().isValid();
}
