package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import com.mcgamer199.luckyblock.tags.ItemStackGetter;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.EffectUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomEntityRandomItem extends CustomEntity {

    private final List<ItemStack> items = new ArrayList<>();
    private int x, f;

    @Override
    public EntityType entityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        ArmorStand armor = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation.add(0.5D, 0.0D, 0.5D), entityType());
        armor.setVisible(false);
        armor.setSmall(true);

        this.x = RandomUtils.nextInt((int) ((double) this.items.size() * 1.5D));

        return armor;
    }

    @Override
    public void spawn(Location location) {
        if(items.size() > 0) {
            super.spawn(location);
        }
    }

    @Override
    public void onTick() {
        ArmorStand stand = (ArmorStand) this.linkedEntity;
        if (this.items.size() == 1) {
            this.linkedEntity.getWorld().dropItem(this.linkedEntity.getLocation(), this.items.get(0));
        } else if (this.items.size() > 1) {
            EulerAngle angle = new EulerAngle(0.0D, 0.0D, 0.0D);
            int l = this.items.size();
            Location lo = this.linkedEntity.getLocation();
            if (this.x > 0) {
                if (this.items.get(this.f) != null) {
                    if (this.items.get(this.f).getType().isBlock()) {
                        angle.setX(-15.0D);
                        angle.setY(-45.0D);
                    } else {
                        angle.setX(-10.0D);
                        angle.setY(-90.0D);
                    }
                    angle.setZ(0.0D);
                }

                stand.setRightArmPose(angle);
                stand.setItemInHand(this.items.get(this.f));
                EffectUtils.playFixedSound(lo, EffectUtils.getSound("lb_drop_randomitem1"), 1.0F, 0.0F, 20);
                --this.x;
                ++this.f;
                if (this.f >= l) {
                    this.f = 0;
                }
            } else {
                if (this.f > 0) {
                    this.linkedEntity.getWorld().dropItem(lo, this.items.get(this.f - 1));
                } else {
                    this.linkedEntity.getWorld().dropItem(lo, this.items.get(this.items.size() - 1));
                }

                EffectUtils.playFixedSound(lo, EffectUtils.getSound("lb_drop_randomitem2"), 1.0F, 2.0F, 20);
                this.linkedEntity.remove();
            }
        }
    }

    @Override
    public int getTickTime() {
        return 5;
    }

    @Override
    public void onSave(ConfigurationSection c) {
        int i = 0;
        for (ItemStack item : this.items) {
            if (!ItemStackUtils.isNullOrAir(item)) {
                if (c.getConfigurationSection("Items.item" + i) == null) {
                    c.createSection("Items.item" + i);
                }

                ItemStackGetter.saveToFileF(item, c.getConfigurationSection("Items.item" + i), -1);
                ++i;
            }
        }

        c.set("X", this.x);
        c.set("F", this.f);
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        if (c.getConfigurationSection("Items") != null) {
            for (String s : c.getConfigurationSection("Items").getKeys(false)) {
                ItemStack item = ItemStackGetter.getItemStack(c.getConfigurationSection("Items").getConfigurationSection(s));
                this.items.add(item);
            }
        }

        this.x = c.getInt("X");
        this.f = c.getInt("F");
    }

    public void addItem(ItemStack itemStack) {
        items.add(itemStack);
    }
}
