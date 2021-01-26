package com.mcgamer199.luckyblock.customentity.boss;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.Immunity;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityUnderwaterFollower extends CustomEntity {
    public int age;
    protected LivingEntity target;

    public EntityUnderwaterFollower() {
    }

    protected Entity spawnFunction(Location loc) {
        Guardian g = (Guardian) loc.getWorld().spawnEntity(loc, EntityType.GUARDIAN);
        g.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(37.0D);
        g.setHealth(37.0D);
        g.setCollidable(false);
        g.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999, 2));
        if (this.target != null) {
            g.setTarget(this.target);
        }

        this.func_age();
        return g;
    }

    private void func_age() {
        if (this.age > 0) {
            ITask task = new ITask();
            task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
                public void run() {
                    EntityUnderwaterFollower.this.remove();
                }
            }, this.age));
        }

    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.BLOCK_EXPLOSION, Immunity.ENTITY_EXPLOSION};
    }

    protected void onSave(ConfigurationSection c) {
        c.set("Age", this.age);
    }

    protected void onLoad(ConfigurationSection c) {
        this.age = c.getInt("Age");
        this.func_age();
    }
}

