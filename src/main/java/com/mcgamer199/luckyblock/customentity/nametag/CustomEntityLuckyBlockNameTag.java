package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.api.customentity.CustomEntityManager;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.LocationUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class CustomEntityLuckyBlockNameTag extends CustomEntity {

    @Getter
    private String text;
    @Getter
    private LuckyBlock luckyBlock;
    private double[] offset = new double[3];
    private ArmorStand armorStand;
    @Getter
    private DisplayType type;

    @Override
    public EntityType entityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ArmorStand armorStand = (ArmorStand) spawnLocation.getBlock().getWorld().spawnEntity(spawnLocation, entityType());
        armorStand.setCustomName(this.text);
        armorStand.setCustomNameVisible(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        this.armorStand = armorStand;
        return armorStand;
    }

    public void spawn(LuckyBlock luckyBlock, double[] offset, DisplayType type) {
        this.luckyBlock = luckyBlock;
        this.offset = offset;
        this.type = type;
        spawn(luckyBlock.getLocation().add(offset[0], offset[1], offset[2]));
        reloadText();
    }

    @Override
    public void onTick() {
        this.armorStand.setCustomName(this.text);
        if (this.luckyBlock != null) {
            this.linkedEntity.teleport(this.luckyBlock.getLocation().add(this.offset[0], this.offset[1], this.offset[2]));
            if (!this.luckyBlock.isValid()) {
                CustomEntityManager.removeCustomEntity(this);
            }
        }
    }

    @Override
    public int getTickTime() {
        return 1;
    }

    @Override
    public DamageCause[] getImmunityTo() {
        return DamageCause.values();
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("LB_Block", LocationUtils.asString(this.luckyBlock.getLocation()));
        c.set("Loc1", this.offset[0]);
        c.set("Loc2", this.offset[1]);
        c.set("Loc3", this.offset[2]);
        c.set("DisplayType", type.name());
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        String code = c.getString("LB_Block");
        this.offset[0] = c.getDouble("Loc1");
        this.offset[1] = c.getDouble("Loc2");
        this.offset[2] = c.getDouble("Loc3");
        this.armorStand = (ArmorStand) this.linkedEntity;
        this.type = DisplayType.valueOf(c.getString("DisplayType"));
        Block block = LocationUtils.blockFromString(code);
        if (block != null) {
            LuckyBlock luckyBlock = LuckyBlock.getByBlock(block);
            if(luckyBlock != null) {
                this.luckyBlock = luckyBlock;
            }
        }

        this.reloadText();
    }

    public void setType(DisplayType type) {
        if(!this.type.equals(type)) {
            this.type = type;
            reloadText();
        }
    }

    public void reloadText() {
        if(luckyBlock != null) {
            switch (type) {
                case DROP_NAME: {
                    if (this.luckyBlock.hasDropOption("Title")) {
                        this.text = ChatColor.translateAlternateColorCodes('&', luckyBlock.getDropOptions().getString("Title", "&cnull"));
                    } else if (this.luckyBlock.customDrop != null) {
                        this.text = ChatColor.RED + this.luckyBlock.customDrop.getName();
                    } else {
                        this.text = ChatColor.RED + this.luckyBlock.getLuckyBlockDrop().name();
                    }
                    break;
                }
                case LUCK: {
                    this.text = this.luckyBlock.getType().getLuckString(this.luckyBlock.getLuck());
                    break;
                }
                case LUCKY_BLOCK_TYPE: {
                    this.text = this.luckyBlock.getType().getName();
                }
            }
        }
    }

    public enum DisplayType {
        DROP_NAME,
        LUCKY_BLOCK_TYPE,
        LUCK;
    }
}
