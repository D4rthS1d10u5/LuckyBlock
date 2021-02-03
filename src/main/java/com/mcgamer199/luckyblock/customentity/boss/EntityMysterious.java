package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.customentity.CustomEntity;
import com.mcgamer199.luckyblock.util.EntityUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class EntityMysterious extends CustomEntity {
    String status = "none";
    int health = 5;
    ArmorStand boss;

    public EntityMysterious() {
    }

    protected Entity spawnFunction(Location loc) {
        ArmorStand boss = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        boss.setArms(false);
        boss.setBasePlate(false);
        boss.setVisible(false);
        boss.setHelmet(new ItemStack(Material.GRASS));
        this.boss = boss;
        return boss;
    }

    void changeStatus() {
        if (this.status.equalsIgnoreCase("command")) {
            this.boss.setHelmet(new ItemStack(Material.COMMAND));
        } else if (this.status.equalsIgnoreCase("lava")) {
            this.boss.setHelmet(new ItemStack(Material.MAGMA));
        }

    }

    void func_endermen() {
        Scheduler.create(() -> {
            Enderman e1 = (Enderman) EntityMysterious.this.boss.getWorld().spawnEntity(EntityMysterious.this.boss.getLocation(), EntityType.ENDERMAN);
            EntityUtils.followEntity(EntityMysterious.this.boss.getLocation().add(10.0D, 0.0D, 0.0D), e1, 0.3D);
        }).predicate(() -> !EntityMysterious.this.boss.isDead()).timer(140, 100);
    }

    public String getSpawnEggEntity() {
        return "guardian";
    }
}
