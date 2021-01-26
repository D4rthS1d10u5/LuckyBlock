package com.mcgamer199.luckyblock.customentity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import com.mcgamer199.luckyblock.entity.CustomEntity;

import java.util.ArrayList;
import java.util.List;

public class EntityLuckyVillager extends CustomEntity {
    public int seconds = 4;

    public EntityLuckyVillager() {
    }

    public Entity spawnFunction(Location loc) {
        Villager villager = (Villager)loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        villager.setCustomName(ChatColor.YELLOW + "Lucky Villager");
        villager.setCustomNameVisible(true);
        List<MerchantRecipe> recipes = new ArrayList();
        MerchantRecipe rec1 = new MerchantRecipe(new ItemStack(Material.DIAMOND_BLOCK), 4);
        rec1.addIngredient(new ItemStack(Material.DIAMOND, 9));
        recipes.add(rec1);
        villager.setRecipes(recipes);
        return villager;
    }

    protected void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
        this.seconds = 0;
    }

    public Particle getDeathParticles() {
        return Particle.HEART;
    }

    protected void onTick() {
        if (this.seconds > 1) {
            --this.seconds;
            this.save_def();
        } else {
            this.entity.getWorld().createExplosion(this.entity.getLocation(), 4.5F);
            this.entity.remove();
        }

    }

    protected int getTickTime() {
        return 20;
    }

    protected void onSave(ConfigurationSection c) {
        c.set("Seconds", this.seconds);
    }

    protected void onLoad(ConfigurationSection c) {
        this.seconds = c.getInt("Seconds");
    }

    public String getSpawnEggEntity() {
        return "Villager";
    }
}

