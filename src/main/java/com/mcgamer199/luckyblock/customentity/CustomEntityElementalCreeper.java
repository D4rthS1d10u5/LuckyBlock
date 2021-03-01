package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CustomEntityElementalCreeper extends CustomEntity {

    private int life = -1;
    private Material blockMaterial = Material.DIRT;
    private byte blockData = 0;

    @Override
    public EntityType entityType() {
        return EntityType.CREEPER;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Creeper creeper = (Creeper) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        creeper.setCustomName("§cElemental Creeper");
        creeper.setCustomNameVisible(true);
        creeper.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(70.0D);
        creeper.setHealth(70.0D);
        creeper.setPowered(true);
        return creeper;
    }

    @Override
    public void onTick() {
        if(this.life != -1 && this.life-- <= 0) {
            CustomEntityManager.removeCustomEntity(this);
        }
    }

    @Override
    public int getTickTime() {
        return 1;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return new DamageCause[] {DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA, DamageCause.PROJECTILE};
    }

    @Override
    public void onExplode(EntityExplodeEvent event) {
        CustomEntityManager.removeCustomEntity(this);
        List<Block> blocks = new ArrayList<>(event.blockList());
        event.blockList().clear();
        blocks.forEach(block -> {
            block.setType(blockMaterial);
            block.setData(blockData);
        });
    }

    @Override
    public void onSave(ConfigurationSection section) {
        section.set("BlockMaterial", this.blockMaterial.name());
        section.set("BlockData", this.blockData);
        section.set("Life", this.life);
    }

    @Override
    public void onLoad(ConfigurationSection section) {
        this.blockMaterial = Material.getMaterial(section.getString("BlockMaterial").toUpperCase());
        this.blockData = (byte) section.getInt("BlockData");
        this.life = section.getInt("Life");
    }
}
