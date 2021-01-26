package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.api.sound.SoundManager;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.logic.MyTasks;
import com.mcgamer199.luckyblock.tags.ItemStackGetter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EntityRandomItem extends CustomEntity {
    public List<ItemStack> items = new ArrayList();
    int x = 0;
    int f = 0;
    private final Random random = new Random();

    public EntityRandomItem() {
    }

    public Entity spawnFunction(Location loc) {
        if (this.items.size() > 0) {
            ArmorStand armor = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0.5D, 0.0D, 0.5D), EntityType.ARMOR_STAND);
            armor.setVisible(false);
            armor.setSmall(true);
            this.x = this.random.nextInt((int) ((double) this.items.size() * 1.5D));
            return armor;
        } else {
            return null;
        }
    }

    protected int getTickTime() {
        return 5;
    }

    protected void onTick() {
        if (this.entity.isValid()) {
            ArmorStand as = (ArmorStand) this.entity;
            if (this.items.size() == 1) {
                this.entity.getWorld().dropItem(this.entity.getLocation(), this.items.get(0));
            } else if (this.items.size() > 1) {
                EulerAngle angle = new EulerAngle(0.0D, 0.0D, 0.0D);
                int l = this.items.size();
                Location lo = this.entity.getLocation();
                if (this.x > 0) {
                    if (this.items.get(this.f) != null) {
                        if (this.items.get(this.f).getType().isBlock()) {
                            angle.setX(-15.0D);
                            angle.setY(-45.0D);
                            angle.setZ(0.0D);
                        } else {
                            angle.setX(-10.0D);
                            angle.setY(-90.0D);
                            angle.setZ(0.0D);
                        }
                    }

                    as.setRightArmPose(angle);
                    as.setItemInHand(this.items.get(this.f));
                    SoundManager.playFixedSound(lo, MyTasks.getSound("lb_drop_randomitem1"), 1.0F, 0.0F, 20);
                    --this.x;
                    ++this.f;
                    if (this.f >= l) {
                        this.f = 0;
                    }
                } else {
                    if (this.f > 0) {
                        this.entity.getWorld().dropItem(lo, this.items.get(this.f - 1));
                    } else {
                        this.entity.getWorld().dropItem(lo, this.items.get(this.items.size() - 1));
                    }

                    SoundManager.playFixedSound(lo, MyTasks.getSound("lb_drop_randomitem2"), 1.0F, 2.0F, 20);
                    this.entity.remove();
                }
            }
        }

    }

    protected void onSave(ConfigurationSection c) {
        int i = 0;
        Iterator var4 = this.items.iterator();

        while (var4.hasNext()) {
            ItemStack item = (ItemStack) var4.next();
            if (item != null) {
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

    protected void onLoad(ConfigurationSection c) {
        if (c.getConfigurationSection("Items") != null) {
            Iterator var3 = c.getConfigurationSection("Items").getKeys(false).iterator();

            while (var3.hasNext()) {
                String s = (String) var3.next();
                ItemStack item = ItemStackGetter.getItemStack(c.getConfigurationSection("Items").getConfigurationSection(s));
                this.items.add(item);
            }
        }

        this.x = c.getInt("X");
        this.f = c.getInt("F");
    }
}
