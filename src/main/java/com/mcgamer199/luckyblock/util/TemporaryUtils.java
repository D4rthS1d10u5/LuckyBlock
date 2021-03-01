package com.mcgamer199.luckyblock.util;

import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.api.IObjects;
import com.mcgamer199.luckyblock.api.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.api.customdrop.CustomDropManager;
import com.mcgamer199.luckyblock.customentity.boss.CustomEntityBossWitch;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LuckyBlockDrop;
import com.mcgamer199.luckyblock.listeners.BreakLuckyBlock;
import com.mcgamer199.luckyblock.resources.DebugData;
import com.mcgamer199.luckyblock.resources.IDebug;
import com.mcgamer199.luckyblock.resources.Schematic;
import com.mcgamer199.luckyblock.tags.ChestFiller;
import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Здесь будет временный код утилит,
 * который в будущем будет распределен
 */
@UtilityClass
public class TemporaryUtils {

    public static void spawnLB(LuckyBlock luckyBlock, final Location bloc) {
        String s = luckyBlock.getCustomDrop() != null ? luckyBlock.getCustomDrop().getName() :
                luckyBlock.getLuckyBlockDrop() != null ? luckyBlock.getLuckyBlockDrop().name() : null;

        Scheduler.later(() -> {
            bloc.getWorld().dropItem(bloc, luckyBlock.getType().toItemStack(luckyBlock.getLuck(), null, s));
            bloc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, bloc, 150, 0.3D, 0.3D, 0.3D, 0.0D);
        }, 3);
    }

    public static int[] tower_rblock(String type) {
        int[] i = new int[]{Material.STAINED_CLAY.getId(), RandomUtils.nextInt(15)};
        if (type.equalsIgnoreCase("b")) {
            i = new int[]{Material.GLASS.getId(), 0};
        } else if (type.equalsIgnoreCase("c")) {
            i = new int[]{getRandomMaterial(Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.LAPIS_BLOCK, Material.EMERALD_BLOCK).getId(), 0};
        } else if (type.equalsIgnoreCase("d")) {
            i = new int[]{Material.SANDSTONE.getId(), RandomUtils.nextInt(3)};
        } else if (type.equalsIgnoreCase("e")) {
            i = new int[]{Material.WOOL.getId(), RandomUtils.nextInt(16)};
        } else if (type.equalsIgnoreCase("f")) {
            i = new int[]{Material.WOOD.getId(), RandomUtils.nextInt(6)};
        }

        return i;
    }

    public static Material getRandomMaterial(Material... mats) {
        return RandomUtils.getRandomObject(mats);
    }

    public static void jail(Block b) {
        int y;
        int x;
        for (y = -1; y < 2; ++y) {
            for (x = -1; x < 2; ++x) {
                b.getLocation().add(y, -1.0D, x).getBlock().setType(Material.SMOOTH_BRICK);
            }
        }

        for (y = 0; y < 2; ++y) {
            for (x = -1; x < 2; ++x) {
                for (int z = -1; z < 2; ++z) {
                    if (x != 0 || z != 0) {
                        b.getLocation().add(x, y, z).getBlock().setType(Material.IRON_FENCE);
                    }
                }
            }
        }
    }

    public static void run(final Block block, LuckyBlock luckyBlock, Player player, LuckyBlockDrop drop, CustomDrop customDrop, boolean first) {
        Location bloc = block.getLocation();
        if (player != null && luckyBlock.hasDropOption("Message")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', luckyBlock.getDropOptions().getString("Message", "")));
        }

        String[] particles = luckyBlock.getDropOptions().getStringArray("IParticles");
        for (String particle : particles) {
            if(particle != null) {
                BreakLuckyBlock.spawnParticle(bloc, particle);
            }
        }

        String playerName = luckyBlock.getDropOptions().getString("Player", null);
        if (player == null && playerName != null) {
            Player search = Bukkit.getPlayer(playerName);
            if(search != null) {
                player = search;
            }
        }

        if (luckyBlock.hasDropOption("With") && first) {
            String[] dropNames = luckyBlock.getDropOptions().getStringArray("With");
            for (String dropName : dropNames) {
                if (dropName != null) {
                    CustomDrop custom = CustomDropManager.getByName(dropName);
                    if(custom != null) {
                        run(block, luckyBlock, player, null, custom, false);
                    } else {
                        run(block, luckyBlock, player, LuckyBlockDrop.getByName(dropName), null, false);
                    }
                }
            }
        }

        if (LuckyBlockPlugin.isDebugEnabled()) {
            IDebug.sendDebug("Lucky block broken",
                    new DebugData("Player", player != null ? player.getName() : "none"),
                    new DebugData("Location", LocationUtils.asString(bloc)),
                    new DebugData("LBType", luckyBlock.getType().getId() + ", " + ChatColor.stripColor(luckyBlock.getType().getName())),
                    new DebugData("Placed By", luckyBlock.getPlacementInfo()),
                    new DebugData("Title", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', luckyBlock.getDropOptions().getString("Title", "unknown")))),
                    new DebugData("Drop Type", luckyBlock.customDrop != null ? luckyBlock.customDrop.getName() : luckyBlock.getLuckyBlockDrop().name()),
                    new DebugData("Luck", String.valueOf(luckyBlock.getLuck())),
                    new DebugData("Owner", luckyBlock.hasOwner() ? luckyBlock.owner.toString() : "none"));
        }

        if (customDrop == null && drop != null) {
            drop.execute(luckyBlock, player);
        } else {
            luckyBlock.getCustomDrop().execute(luckyBlock, player);
        }
    }

    public static void generateWitchDungeon(Location loc) {
        Location l = new Location(loc.getWorld(), loc.getX() - 22.0D, loc.getY(), loc.getZ() - 22.0D);
        Schematic.loadArea(IObjects.getStoredFile("witch_structure"), l);
        FileConfiguration c = YamlConfiguration.loadConfiguration(IObjects.getStoredFile("witch_chests"));
        Location l1 = new Location(loc.getWorld(), loc.getX() + 4.0D, loc.getY() + 22.0D, loc.getZ() - 4.0D);
        Location l2 = new Location(loc.getWorld(), loc.getX() - 4.0D, loc.getY() + 22.0D, loc.getZ() + 4.0D);
        Location l3 = new Location(loc.getWorld(), loc.getX() + 4.0D, loc.getY() + 22.0D, loc.getZ() + 4.0D);
        Location l4 = new Location(loc.getWorld(), loc.getX() - 4.0D, loc.getY() + 22.0D, loc.getZ() - 4.0D);
        Block block1 = l1.getBlock();
        Block block2 = l2.getBlock();
        Block block3 = l3.getBlock();
        Block block4 = l4.getBlock();
        Chest chest1 = (Chest) block1.getState();
        Chest chest2 = (Chest) block2.getState();
        Chest chest3 = (Chest) block3.getState();
        Chest chest4 = (Chest) block4.getState();
        ChestFiller filler = new ChestFiller(c.getConfigurationSection("Chests"), chest1);
        filler.loc1 = "FoodsChest";
        filler.fill();
        filler.chest = chest2;
        filler.loc1 = "ArmorChest";
        filler.fill();
        filler.chest = chest3;
        filler.loc1 = "ToolsWeaponsChest";
        filler.fill();
        filler.chest = chest4;
        filler.loc1 = "ItemsChest";
        filler.fill();
        CustomEntityBossWitch witch = new CustomEntityBossWitch();
        witch.spawn(loc.add(0.0D, 8.0D, 0.0D));
    }
}
