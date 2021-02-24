package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.util.MathUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomEntityHealthTag extends CustomEntity {

    @Getter @Setter
    private boolean hideNameWhenInvisible;
    @Getter
    private ArmorStand armorStand;
    private double[] offset = new double[3];
    private LivingEntity attachedEntity;
    @Getter @Setter
    private HealthDisplayMode mode;
    private int heartsAmount = 1;

    @Override
    public EntityType entityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ArmorStand armorStand = (ArmorStand) attachedEntity.getWorld().spawnEntity(spawnLocation.add(offset[0], offset[1], offset[2]), entityType());
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        this.armorStand = armorStand;

        Scheduler.create(() -> {
            if(attachedEntity != null && !attachedEntity.isDead()) {
                armorStand.teleport(attachedEntity.getLocation().add(offset[0], offset[1], offset[1]));
            } else {
                CustomEntityManager.removeCustomEntity(this);
            }
        }).predicate(() -> !attachedEntity.isDead()).timer(1, 1);
        Scheduler.later(() -> armorStand.setCustomNameVisible(true), 10);
        return armorStand;
    }

    public void setHeartsAmount(int heartsAmount) {
        this.heartsAmount = MathUtils.ensureRange(heartsAmount, 1, 20);
    }

    public void attachEntity(@NotNull LivingEntity attachedEntity, @NotNull double[] offset) {
        this.attachedEntity = attachedEntity;
        this.offset = offset;
    }

    private boolean isInvisible() {
        return attachedEntity != null && attachedEntity.hasPotionEffect(PotionEffectType.INVISIBILITY);
    }

    private String getPercentLine(float percent, int displayScale) {
        char healthBar = mode.equals(HealthDisplayMode.DEFAULT) ? '█' : '♥';
        StringBuilder percentLine = new StringBuilder();
        for(float i = 0; i < displayScale; i++) {
            if(i/displayScale < percent) percentLine.append("§a").append(healthBar);
            else percentLine.append("§7").append(healthBar);
        }

        return percentLine.toString();
    }

    @Override
    public void onTick() {
        if(hideNameWhenInvisible && isInvisible()) {
            armorStand.setCustomNameVisible(false);
        } else {
            armorStand.setCustomNameVisible(true);
            float healthPercent = (float) (attachedEntity.getHealth() / attachedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            String displayHealth;
            if(mode.equals(HealthDisplayMode.PERCENT)) {
                displayHealth = String.format("§a%.2f%%", healthPercent);
            } else {
                int displayScale = mode.getDisplayScale();
                if(mode.equals(HealthDisplayMode.CUSTOM_HEARTS)) {
                    displayScale = 100 / heartsAmount;
                }
                displayHealth = getPercentLine(healthPercent, displayScale);
            }

            if(StringUtils.isNotEmpty(displayHealth)) {
                armorStand.setCustomName(displayHealth);
            }
        }
    }

    @Override
    public int getTickTime() {
        return 5;
    }

    @Override
    public void onChunkLoad() {
        for (LivingEntity entity : linkedEntity.getWorld().getEntitiesByClass(LivingEntity.class)) {
            if (entity.getUniqueId().equals(this.attachedEntity.getUniqueId())) {
                this.attachedEntity = entity;
                break;
            }
        }
    }

    @Override
    public final void onSave(ConfigurationSection c) {
        c.set("attachedEntity", this.attachedEntity.getUniqueId().toString());
        c.set("Offset.X", this.offset[0]);
        c.set("Offset.Y", this.offset[1]);
        c.set("Offset.Z", this.offset[2]);
        c.set("ModeBase", mode.name());
        c.set("Hearts", this.heartsAmount);
    }

    @Override
    public final void onLoad(final ConfigurationSection c) {
        this.offset[0] = c.getDouble("Offset.X");
        this.offset[1] = c.getDouble("Offset.Y");
        this.offset[2] = c.getDouble("Offset.Z");
        this.mode = HealthDisplayMode.valueOf(c.getString("ModeBase"));
        this.heartsAmount = c.getInt("Hearts");
        this.armorStand = (ArmorStand) this.linkedEntity;

        Scheduler.later(() -> this.attachedEntity = (LivingEntity) CustomEntityManager.getCustomEntity(UUID.fromString(c.getString("attachedEntity"))).getLinkedEntity(), 15);
    }

    public enum HealthDisplayMode {
        DEFAULT(10),
        THREE_HEARTS(3),
        PERCENT(-1),
        CUSTOM_HEARTS(-1);

        @Getter
        private final int displayScale;

        HealthDisplayMode(int displayScale) {
            this.displayScale = displayScale;
        }
    }
}
