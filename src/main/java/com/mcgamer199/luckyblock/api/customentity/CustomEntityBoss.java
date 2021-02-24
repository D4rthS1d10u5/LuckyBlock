package com.mcgamer199.luckyblock.api.customentity;

import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;

public interface CustomEntityBoss {

    LivingEntity getBossEntity();

    BossBar getBossBar();

    int getBossBarRange();

    void onEntityHitWithBeam(LivingEntity livingEntity, String tag);

    default boolean hasBossBar() {
        return getBossBar() != null;
    }

    default void tickBossBarTimer() {
        if(hasBossBar()) {
            LivingEntity boss = getBossEntity();
            getBossBar().setTitle(boss.getCustomName());
            getBossBar().setProgress(boss.getHealth() / boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        }
    }
}
