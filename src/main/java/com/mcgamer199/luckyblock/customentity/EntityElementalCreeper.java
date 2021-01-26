package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.Immunity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityElementalCreeper extends CustomEntity {
    public int life;
    Material blockMaterial;
    byte blockData;

    public EntityElementalCreeper() {
        this.blockMaterial = Material.DIRT;
        this.blockData = 0;
        this.life = -1;
    }

    public Entity spawnFunction(Location loc) {
        Creeper creeper = (Creeper) loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
        creeper.setCustomName(ChatColor.RED + "Elemental Creeper");
        creeper.setCustomNameVisible(true);
        creeper.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(70.0D);
        creeper.setHealth(70.0D);
        creeper.setPowered(true);
        return creeper;
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.FIRE, Immunity.LAVA, Immunity.FIRE_TICK, Immunity.PROJECTILE};
    }

    protected int getTickTime() {
        return 1;
    }

    public Material getBlockMaterial() {
        return this.blockMaterial;
    }

    public byte getBlockData() {
        return this.blockData;
    }

    protected void onTick() {
        if (this.life > -1) {
            if (this.life > 0) {
                --this.life;
            } else {
                this.remove();
            }
        }

    }

    public void changeMaterial(Material mat, byte data) {
        this.blockMaterial = mat;
        this.blockData = data;
        this.save_def();
    }

    protected void onExplode(EntityExplodeEvent event) {
        List<Block> blocks = new ArrayList();
        Iterator var4 = event.blockList().iterator();

        Block block;
        while (var4.hasNext()) {
            block = (Block) var4.next();
            blocks.add(block);
        }

        event.blockList().clear();
        var4 = blocks.iterator();

        while (var4.hasNext()) {
            block = (Block) var4.next();
            block.setType(this.blockMaterial);
            block.setData(this.blockData);
        }

    }

    protected void onSave(ConfigurationSection c) {
        c.set("BlockMaterial", this.blockMaterial.name());
        c.set("BlockData", this.blockData);
        c.set("Life", this.life);
    }

    protected void onLoad(ConfigurationSection c) {
        this.blockMaterial = Material.getMaterial(c.getString("BlockMaterial").toUpperCase());
        this.blockData = (byte) c.getInt("BlockData");
        this.life = c.getInt("Life");
    }

    public String getSpawnEggEntity() {
        return "Creeper";
    }
}
