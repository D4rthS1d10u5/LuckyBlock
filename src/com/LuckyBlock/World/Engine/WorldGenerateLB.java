package com.LuckyBlock.World.Engine;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.Event.LB.Block.PlaceLuckyBlock;
import com.LuckyBlock.Events.LBGenerateEvent;
import com.LuckyBlock.Events.LBPreGenerateEvent;
import com.LuckyBlock.LB.LB;
import com.LuckyBlock.LB.LBType;
import com.LuckyBlock.World.Engine.LuckyBlockWorld;
import com.LuckyBlock.logic.ColorsClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;

public class WorldGenerateLB extends ColorsClass implements Listener {

    public WorldGenerateLB() {
    }

    @EventHandler
    private void onChunkPopulate(ChunkPopulateEvent event) {
        if (LuckyBlock.allowLBGeneration) {
            World world = event.getWorld();
            if (!LuckyBlockWorld.equals(world.getGenerator())) {
                int x = random.nextInt(10) - 5;
                int z = random.nextInt(10) - 5;
                int x1 = event.getChunk().getX() * 16 + x;
                int z1 = event.getChunk().getZ() * 16 + z;
                int y = world.getHighestBlockAt(x1, z1).getY();
                Block b = world.getBlockAt(x1, y, z1);
                if (b.getType() != Material.AIR) {
                    ++y;
                    b = world.getBlockAt(x1, y, z1);
                }

                LBType type = LBType.getRandomLBToGenerate(world, b.getLocation(), b.getBiome());
                if (type != null) {
                    Block blockUnder = b.getRelative(BlockFace.DOWN);
                    if (!blockUnder.getType().isSolid()) {
                        blockUnder.setType(Material.AIR);
                        return;
                    }

                    if (blockUnder.getType() == Material.LEAVES || blockUnder.getType() == Material.LEAVES_2) {
                        boolean i = false;
                        int a = 0;

                        while(!i) {
                            blockUnder = blockUnder.getRelative(BlockFace.DOWN);
                            if (blockUnder.getType() == Material.AIR && blockUnder.getRelative(BlockFace.DOWN).getType().isSolid()) {
                                b = blockUnder;
                                i = true;
                            }

                            ++a;
                            if (a > 10) {
                                i = true;
                            }
                        }
                    }

                    LBPreGenerateEvent e = new LBPreGenerateEvent(b, world, event.getChunk(), type);
                    Bukkit.getPluginManager().callEvent(e);
                    if (!e.isCancelled() && !type.defaultBlock) {
                        LB lb = LB.placeLB(b.getLocation(), type, (ItemStack)null, "a=Naturally", (String)null, LBType.getRandomP(type.generateWithLuck[0], type.generateWithLuck[1]), new PlaceLuckyBlock.LBOption[0]);
                        LBGenerateEvent e1 = new LBGenerateEvent(lb, world, event.getChunk());
                        Bukkit.getPluginManager().callEvent(e1);
                    }
                }
            }
        }

    }
}
