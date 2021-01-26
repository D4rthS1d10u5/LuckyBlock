package com.mcgamer199.luckyblock.structures;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

/**
 * @deprecated
 */
@Deprecated
public class IlluminateStructure extends Structure {
    public IlluminateStructure() {
    }

    public void build(Location loc) {
        Block block = loc.getBlock();
        int y = 0;

        int times;
        int x;
        int z;
        for (times = 20; times > 0; --times) {
            for (x = times * -1; x < times - 1; ++x) {
                for (z = times * -1; z < times - 1; ++z) {
                    block.getLocation().add(x, y, z).getBlock().setType(Material.SANDSTONE);
                }
            }

            ++y;
        }

        y = 1;

        for (times = 18; times > 0; --times) {
            for (x = times * -1; x < times - 1; ++x) {
                for (z = times * -1; z < times - 1; ++z) {
                    block.getLocation().add(x, y, z).getBlock().setType(Material.AIR);
                }
            }

            ++y;
        }

        block.getLocation().add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.BEDROCK);
        block.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().setType(Material.BEDROCK);
        block.getLocation().add(1.0D, 2.0D, 0.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(1.0D, 3.0D, 0.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(1.0D, 4.0D, 0.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(-1.0D, 2.0D, 0.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(-1.0D, 3.0D, 0.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(-1.0D, 4.0D, 0.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(0.0D, 2.0D, 1.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(0.0D, 3.0D, 1.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(0.0D, 4.0D, 1.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(0.0D, 2.0D, -1.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(0.0D, 3.0D, -1.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(0.0D, 4.0D, -1.0D).getBlock().setType(Material.GLASS);
        block.getLocation().add(0.0D, 5.0D, 0.0D).getBlock().setType(Material.GLASS);
        Zombie zombie = (Zombie) loc.getWorld().spawnEntity(block.getLocation().add(0.5D, 3.0D, 0.5D), EntityType.ZOMBIE);
        zombie.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
        zombie.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
        zombie.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
        zombie.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
        zombie.setCustomName("" + ChatColor.BLUE + ChatColor.BOLD + "[Illuminati]");
        zombie.setRemoveWhenFarAway(false);
        zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4D);
        super.build(loc);
    }
}
