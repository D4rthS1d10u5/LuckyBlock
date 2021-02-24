package com.mcgamer199.luckyblock.customentity;

import com.destroystokyo.paper.ParticleBuilder;
import com.mcgamer199.luckyblock.api.customentity.CustomEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomEntityLuckyVillager extends CustomEntity {

    @Getter @Setter
    private int seconds = 4;

    @Override
    public EntityType entityType() {
        return EntityType.VILLAGER;
    }

    @Override
    public @NotNull Entity summonEntity(@NotNull Location spawnLocation) {
        Villager villager = (Villager) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType());
        villager.setCustomName(ChatColor.YELLOW + "Lucky Villager");
        villager.setCustomNameVisible(true);
        List<MerchantRecipe> recipes = new ArrayList<>();
        MerchantRecipe rec1 = new MerchantRecipe(new ItemStack(Material.DIAMOND_BLOCK), 4);
        rec1.addIngredient(new ItemStack(Material.DIAMOND, 9));
        recipes.add(rec1);
        villager.setRecipes(recipes);
        return villager;
    }

    @Override
    public ParticleBuilder createDeathParticles() {
        return super.createDeathParticles().particle(Particle.HEART);
    }

    @Override
    public void onTick() {
        if(this.seconds-- == 0) {
            linkedEntity.getWorld().createExplosion(linkedEntity, 4.5F);
            linkedEntity.remove();
        }
    }

    @Override
    public int getTickTime() {
        return 20;
    }

    @Override
    public void onSave(ConfigurationSection c) {
        c.set("Seconds", this.seconds);
    }

    @Override
    public void onLoad(ConfigurationSection c) {
        this.seconds = c.getInt("Seconds");
    }
}
