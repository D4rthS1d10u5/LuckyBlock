package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.chatcomponent.ChatComponent;
import com.mcgamer199.luckyblock.api.chatcomponent.Click;
import com.mcgamer199.luckyblock.api.chatcomponent.Hover;
import com.mcgamer199.luckyblock.util.SoundUtils;
import com.mcgamer199.luckyblock.command.engine.ILBCmd;
import com.mcgamer199.luckyblock.customdrop.CustomDrop;
import com.mcgamer199.luckyblock.customentity.EntityElementalCreeper;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBDrop;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.ActionPerformer;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.resources.LBEntitiesSpecial;
import com.mcgamer199.luckyblock.resources.Schematic;
import com.mcgamer199.luckyblock.structures.LuckyWell;
import com.mcgamer199.luckyblock.util.ItemStackUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Arrays;

public class DropEvents1 extends ColorsClass {
    public static final byte maxRadius = 48;

    public DropEvents1() {
    }

    static void run(Block block, LuckyBlock luckyBlock, Player player, LBDrop drop, CustomDrop customDrop, boolean first) {
        Location bloc = block.getLocation();
        FileConfiguration file = luckyBlock.getFile();
        String clss;
        if (drop == LBDrop.SOUND) {
            clss = null;
            if (luckyBlock.getDropOption("Listener") != null) {
                if (luckyBlock.getDropOption("Listener").getValues()[0].toString().equalsIgnoreCase("player")) {
                    clss = "player";
                } else if (luckyBlock.getDropOption("Listener").getValues()[0].toString().equalsIgnoreCase("nearby")) {
                    clss = "nearby";
                }
            }

            if (clss.equalsIgnoreCase("player")) {
                if (player != null && luckyBlock.getDropOption("SoundName") != null) {
                    player.playSound(player.getLocation(), Sound.valueOf(luckyBlock.getDropOption("SoundName").getValues()[0].toString().toUpperCase()), 1.0F, 1.0F);
                }
            } else if (clss.equalsIgnoreCase("nearby")) {
                SoundUtils.playFixedSound(bloc, Sound.valueOf(luckyBlock.getDropOption("SoundName").getValues()[0].toString().toUpperCase()), 1.0F, 1.0F, 30);
            }
        } else if (drop == LBDrop.XP_RAIN) {
            HTasks.f(bloc.add(0.0D, 1.0D, 0.0D), luckyBlock);
        } else {
            Material blockMaterial;
            byte blockType;
            if (drop == LBDrop.SET_BLOCK) {
                blockType = 0;
                blockMaterial = null;
                if (luckyBlock.hasDropOption("BlockMaterial")) {
                    blockMaterial = Material.getMaterial(luckyBlock.getDropOption("BlockMaterial").getValues()[0].toString().toUpperCase());
                    if (blockMaterial != null) {
                        block.setType(blockMaterial);
                    } else if (player != null) {
                        send_no(player, "drops.setblock.invalid_material");
                    }

                    if (luckyBlock.hasDropOption("BlockData")) {
                        blockType = Byte.parseByte(luckyBlock.getDropOption("BlockData").getValues()[0].toString());
                        block.setData(blockType);
                    }

                    if (blockMaterial != null && luckyBlock.hasDropOption("ShowParticles") && luckyBlock.getDropOption("ShowParticles").getValues()[0].toString().equalsIgnoreCase("true")) {
                        MaterialData md = new MaterialData(blockMaterial, blockType);
                        bloc.getWorld().spawnParticle(Particle.BLOCK_CRACK, bloc, 100, 0.3D, 0.1D, 0.3D, 0.0D, md);
                    }
                }
            } else {
                int playerX;
                int playerY;
                int fuseTicks;
                if (drop == LBDrop.FALLING_ANVILS) {
                    blockType = 8;
                    fuseTicks = 20;
                    if (luckyBlock.hasDropOption("Height")) {
                        fuseTicks = Integer.parseInt(luckyBlock.getDropOption("Height").getValues()[0].toString());
                    }

                    Location l = bloc.add(0.0D, fuseTicks, 0.0D);
                    if (luckyBlock.hasDropOption("LocationType") && luckyBlock.getDropOption("LocationType").getValues()[0].toString().equalsIgnoreCase("player") && player != null) {
                        l = player.getLocation().add(0.0D, fuseTicks, 0.0D);
                    }

                    if (luckyBlock.hasDropOption("AnvilData")) {
                        blockType = Byte.parseByte(luckyBlock.getDropOption("AnvilData").getValues()[0].toString());
                    }

                    for (playerX = -1; playerX < 2; ++playerX) {
                        for (playerY = -1; playerY < 2; ++playerY) {
                            FallingBlock b = l.getWorld().spawnFallingBlock(new Location(l.getWorld(), l.getX() + (double) playerX + 0.5D, l.getY(), l.getZ() + (double) playerY + 0.5D), Material.ANVIL, blockType);
                            b.setDropItem(false);
                        }
                    }
                } else {
                    int fuse;
                    if (drop == LBDrop.DISPENSER) {
                        block.setType(Material.DISPENSER);
                        block.setData((byte) 1);
                        Dispenser d = (Dispenser) block.getState();
                        fuse = 64;
                        if (luckyBlock.hasDropOption("Times")) {
                            fuse = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
                        }

                        fuse = fuse;

                        while (fuse > 0) {
                            if (fuse > 63) {
                                d.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                                fuse -= 64;
                            } else {
                                d.getInventory().addItem(new ItemStack(Material.ARROW, fuse));
                                fuse = 0;
                            }
                        }

                        HTasks.g(d);
                    } else {
                        int wtfIsThisInt;
                        if (drop == LBDrop.POTION_EFFECT) {
                            if (player != null && luckyBlock.getDropOption("Effects") != null) {
                                Object[] d = luckyBlock.getDropOption("Effects").getValues();
                                Object[] var30 = d;
                                playerX = d.length;

                                for (fuse = 0; fuse < playerX; ++fuse) {
                                    Object b = var30[fuse];
                                    String s = b.toString();
                                    if (s != null) {
                                        String[] t = s.split("%");
                                        if (PotionEffectType.getByName(t[0].toUpperCase()) != null) {
                                            boolean var16 = false;

                                            byte am;
                                            try {
                                                wtfIsThisInt = Integer.parseInt(t[1]);
                                                am = Byte.parseByte(t[2]);
                                            } catch (NumberFormatException var20) {
                                                send_no(player, "invalid_number");
                                                return;
                                            }

                                            player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(t[0].toUpperCase()), wtfIsThisInt, am));
                                        } else {
                                            send_no(player, "drops.potion_effect.invalid_effect");
                                        }
                                    }
                                }
                            }
                        } else {
                            int t;
                            if (drop == LBDrop.DAMAGE_1) {
                                if (player != null && luckyBlock.getDropOption("Times") != null) {
                                    t = 11;
                                    if (luckyBlock.hasDropOption("Ticks")) {
                                        t = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
                                    }

                                    HTasks.h(player, Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString()), t);
                                }
                            } else if (drop == LBDrop.FIRE) {
                                t = 10;
                                if (luckyBlock.hasDropOption("Range")) {
                                    t = Integer.parseInt(luckyBlock.getDropOption("Range").getValues()[0].toString());
                                }

                                HTasks.i(bloc, t);
                            } else if (drop == LBDrop.EXPLOSION) {
                                float power = 4.0F;
                                if (luckyBlock.hasDropOption("ExplosionPower")) {
                                    power = Float.parseFloat(luckyBlock.getDropOption("ExplosionPower").getValues()[0].toString());
                                }

                                bloc.getWorld().createExplosion(bloc, power);
                            } else if (drop == LBDrop.BOB) {
                                LBEntitiesSpecial.spawnBob(bloc, false);
                            } else if (drop == LBDrop.PETER) {
                                LBEntitiesSpecial.spawnPeter(bloc, false);
                            } else if (drop == LBDrop.KILL) {
                                if (player != null) {
                                    player.setHealth(0.0D);
                                }
                            } else {
                                int z;
                                Location location;
                                if (drop == LBDrop.LAVA_HOLE) {
                                    if (player != null) {
                                        t = player.getLocation().getBlockX();
                                        fuse = player.getLocation().getBlockY();
                                        fuse = player.getLocation().getBlockZ();
                                        byte rad = 2;
                                        boolean cobs = true;
                                        Material ma = Material.STONE;
                                        byte data = 0;
                                        if (luckyBlock.hasDropOption("BordersMaterial")) {
                                            ma = Material.getMaterial(luckyBlock.getDropOption("BordersMaterial").getValues()[0].toString().toUpperCase());
                                        }

                                        if (luckyBlock.hasDropOption("BordersData")) {
                                            data = Byte.parseByte(luckyBlock.getDropOption("BordersData").getValues()[0].toString());
                                        }

                                        if (luckyBlock.hasDropOption("WithWebs") && luckyBlock.getDropOption("WithWebs").getValues()[0].toString().equalsIgnoreCase("false")) {
                                            cobs = false;
                                        }

                                        if (luckyBlock.hasDropOption("Radius")) {
                                            rad = Byte.parseByte(luckyBlock.getDropOption("Radius").getValues()[0].toString());
                                        }

                                        if (rad > 48) {
                                            rad = 48;
                                        }

                                        int y;
                                        Location blockLocation;
                                        for (wtfIsThisInt = rad * -1 - 1; wtfIsThisInt < rad + 2; ++wtfIsThisInt) {
                                            for (z = rad * -1 - 1; z < rad + 2; ++z) {
                                                for (y = fuse - 1; y > 0; --y) {
                                                    blockLocation = new Location(player.getLocation().getWorld(), t + wtfIsThisInt, y, fuse + z);
                                                    blockLocation.getBlock().setType(ma);
                                                    blockLocation.getBlock().setData(data);
                                                }
                                            }
                                        }

                                        for (wtfIsThisInt = rad * -1; wtfIsThisInt < rad + 1; ++wtfIsThisInt) {
                                            for (z = rad * -1; z < rad + 1; ++z) {
                                                for (y = fuse; y > 0; --y) {
                                                    blockLocation = new Location(player.getLocation().getWorld(), t + wtfIsThisInt, y, fuse + z);
                                                    blockLocation.getBlock().setType(Material.AIR);
                                                }
                                            }
                                        }

                                        if (cobs) {
                                            for (wtfIsThisInt = rad * -1; wtfIsThisInt < rad + 1; ++wtfIsThisInt) {
                                                for (z = rad * -1; z < rad + 1; ++z) {
                                                    blockLocation = new Location(player.getLocation().getWorld(), t + wtfIsThisInt, 4.0D, fuse + z);
                                                    blockLocation.getBlock().setType(Material.WEB);
                                                }
                                            }
                                        }

                                        Location loc = new Location(player.getLocation().getWorld(), t - rad, 5.0D, fuse);
                                        Material mat = Material.WALL_SIGN;
                                        if (loc.getBlock().getRelative(BlockFace.WEST).getType() == Material.AIR) {
                                            loc.getBlock().getRelative(BlockFace.WEST).setType(Material.STONE);
                                        }

                                        loc.getBlock().setType(mat);
                                        loc.getBlock().setData((byte) 5);
                                        Sign sign = (Sign) loc.getBlock().getState();
                                        if (luckyBlock.hasDropOption("Texts")) {
                                            Object[] text = luckyBlock.getDropOption("Texts").getValues();

                                            for (int x = 0; x < text.length; ++x) {
                                                if (text[x] != null) {
                                                    sign.setLine(x, ChatColor.translateAlternateColorCodes('&', text[x].toString()));
                                                }
                                            }
                                        }

                                        sign.update();

                                        for (wtfIsThisInt = rad * -1; wtfIsThisInt < rad + 1; ++wtfIsThisInt) {
                                            for (z = rad * -1; z < rad + 1; ++z) {
                                                for (y = 2; y > 0; --y) {
                                                    loc = new Location(player.getLocation().getWorld(), t + wtfIsThisInt, y, fuse + z);
                                                    loc.getBlock().setType(Material.LAVA);
                                                }
                                            }
                                        }
                                    }
                                } else if (drop == LBDrop.VOID_HOLE) {
                                    if (player != null) {
                                        blockType = 2;
                                        blockMaterial = Material.AIR;
                                        byte data = 0;
                                        if (luckyBlock.hasDropOption("BordersMaterial")) {
                                            blockMaterial = Material.getMaterial(luckyBlock.getDropOption("BordersMaterial").getValues()[0].toString().toUpperCase());
                                        }

                                        if (luckyBlock.hasDropOption("BordersData")) {
                                            data = Byte.parseByte(luckyBlock.getDropOption("BordersData").getValues()[0].toString());
                                        }

                                        if (luckyBlock.hasDropOption("Radius")) {
                                            blockType = Byte.parseByte(luckyBlock.getDropOption("Radius").getValues()[0].toString());
                                        }

                                        if (blockType > 48) {
                                            blockType = 48;
                                        }

                                        playerX = player.getLocation().getBlockX();
                                        playerY = player.getLocation().getBlockY();
                                        int playerZ = player.getLocation().getBlockZ();
                                        int x;
                                        if (blockMaterial != Material.AIR) {
                                            for (x = blockType * -1 - 1; x < blockType + 2; ++x) {
                                                for (x = blockType * -1 - 1; x < blockType + 2; ++x) {
                                                    for (z = playerY; z > -1; --z) {
                                                        location = new Location(player.getLocation().getWorld(), playerX + x, z, playerZ + x);
                                                        location.getBlock().setType(blockMaterial);
                                                        location.getBlock().setData(data);
                                                    }
                                                }
                                            }
                                        }

                                        for (x = blockType * -1; x < blockType + 1; ++x) {
                                            for (x = blockType * -1; x < blockType + 1; ++x) {
                                                for (z = playerY; z > -1; --z) {
                                                    location = new Location(player.getLocation().getWorld(), playerX + x, z, playerZ + x);
                                                    location.getBlock().setType(Material.AIR);
                                                }
                                            }
                                        }
                                    }
                                } else if (drop == LBDrop.ADD_XP) {
                                    if (player != null && luckyBlock.hasDropOption("Amount")) {
                                        t = Integer.parseInt(luckyBlock.getDropOption("Amount").getValues()[0].toString());
                                        player.giveExp(t);
                                    }
                                } else if (drop == LBDrop.ADD_LEVEL) {
                                    if (player != null && luckyBlock.hasDropOption("Amount")) {
                                        t = Integer.parseInt(luckyBlock.getDropOption("Amount").getValues()[0].toString());
                                        player.giveExpLevels(t);
                                    }
                                } else {
                                    byte data;
                                    if (drop == LBDrop.ELEMENTAL_CREEPER) {
                                        EntityElementalCreeper e = new EntityElementalCreeper();
                                        e.spawn(bloc);
                                        if (luckyBlock.hasDropOption("BlockMaterial")) {
                                            blockMaterial = Material.getMaterial(luckyBlock.getDropOption("BlockMaterial").getValues()[0].toString().toUpperCase());
                                            e.changeMaterial(blockMaterial, e.getBlockData());
                                        }

                                        if (luckyBlock.hasDropOption("BlockData")) {
                                            data = Byte.parseByte(luckyBlock.getDropOption("BlockData").getValues()[0].toString());
                                            e.changeMaterial(e.getBlockMaterial(), data);
                                        }
                                    } else {
                                        Material mat;
                                        if (drop == LBDrop.SET_NEARBY_BLOCKS) {
                                            if (luckyBlock.hasDropOption("BlockMaterial")) {
                                                mat = Material.getMaterial(luckyBlock.getDropOption("BlockMaterial").getValues()[0].toString().toUpperCase());
                                                data = 0;
                                                fuse = 10;
                                                playerX = 8;
                                                String mode = "surface";
                                                if (luckyBlock.hasDropOption("BlockData")) {
                                                    data = Byte.parseByte(luckyBlock.getDropOption("BlockData").getValues()[0].toString());
                                                }

                                                if (luckyBlock.hasDropOption("Range")) {
                                                    fuse = Integer.parseInt(luckyBlock.getDropOption("Range").getValues()[0].toString());
                                                }

                                                if (luckyBlock.hasDropOption("Delay")) {
                                                    playerX = Integer.parseInt(luckyBlock.getDropOption("Delay").getValues()[0].toString());
                                                }

                                                if (luckyBlock.hasDropOption("Mode")) {
                                                    mode = luckyBlock.getDropOption("Mode").getValues()[0].toString();
                                                }

                                                HTasks.j(bloc, fuse, mat, data, playerX, mode);
                                            }
                                        } else if (drop == LBDrop.SINK_IN_GROUND) {
                                            if (player != null) {
                                                HTasks.k(player);
                                            }
                                        } else {
                                            String f;
                                            if (drop == LBDrop.PERFORM_ACTION) {
                                                if (luckyBlock.hasDropOption("ObjType") && ObjectType.getByName(luckyBlock.getDropOption("ObjType").getValues()[0].toString()) != null) {
                                                    ObjectType objType = ObjectType.getByName(luckyBlock.getDropOption("ObjType").getValues()[0].toString());
                                                    if (objType == ObjectType.PLAYER) {
                                                        objType.setObj(player);
                                                    }

                                                    if (objType == ObjectType.BLOCK) {
                                                        objType.setObj(block);
                                                    }

                                                    if (objType.getObj() != null) {
                                                        f = null;
                                                        Object actionValue = null;
                                                        if (luckyBlock.hasDropOption("ActionName")) {
                                                            f = luckyBlock.getDropOption("ActionName").getValues()[0].toString();
                                                        }

                                                        if (luckyBlock.hasDropOption("ActionValue")) {
                                                            actionValue = luckyBlock.getDropOption("ActionValue").getValues()[0];
                                                        }

                                                        if (f != null && !ActionPerformer.perform(objType, f, actionValue)) {
                                                            send(player, "drops.perform_action.error");
                                                        }
                                                    }
                                                }
                                            } else if (drop == LBDrop.LB_STRUCTURE) {
                                                if (luckyBlock.hasDropOption("Class")) {
                                                    clss = luckyBlock.getDropOption("Class").getValues()[0].toString();
                                                    DropEvents.b(clss, bloc);
                                                }
                                            } else if (drop == LBDrop.LAVA_POOL) {
                                                for (t = -3; t < 4; ++t) {
                                                    for (fuse = -3; fuse < 4; ++fuse) {
                                                        player.getLocation().add(t, -1.0D, fuse).getBlock().setType(Material.STATIONARY_LAVA);
                                                    }
                                                }
                                            } else if (drop == LBDrop.DROPPER) {
                                                block.setType(Material.DROPPER);
                                                block.setData((byte) 1);
                                                Dropper d = (Dropper) block.getState();
                                                fuse = 64;
                                                if (luckyBlock.hasDropOption("Times")) {
                                                    fuse = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
                                                }

                                                fuse = fuse;

                                                while (fuse > 0) {
                                                    if (fuse > 63) {
                                                        d.getInventory().addItem(new ItemStack(Material.DIAMOND, 64));
                                                        fuse -= 64;
                                                    } else {
                                                        d.getInventory().addItem(new ItemStack(Material.DIAMOND, fuse));
                                                        fuse = 0;
                                                    }
                                                }

                                                HTasks.l(d);
                                            } else if (drop == LBDrop.LUCKY_WELL) {
                                                LuckyWell well = new LuckyWell();
                                                well.build(bloc);
                                                if (player != null) {
                                                    ItemStack item = ItemStackUtils.addEnchant(ItemStackUtils.createItem(Material.GOLD_NUGGET, 1, 0, "" + gold + bold + "Coin", Arrays.asList(gray + "Drop it in the well")), Enchantment.DURABILITY, 1);
                                                    ItemMeta itemM = item.getItemMeta();
                                                    itemM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                                    item.setItemMeta(itemM);
                                                    player.getInventory().addItem(item);
                                                }
                                            } else if (drop == LBDrop.WATER_TRAP) {
                                                if (player != null) {
                                                    for (t = -1; t < 2; ++t) {
                                                        for (fuse = -1; fuse < 2; ++fuse) {
                                                            for (fuse = 0; fuse < 3; ++fuse) {
                                                                if (t != 0 || fuse != 0) {
                                                                    player.getLocation().add(t, fuse, fuse).getBlock().setType(Material.OBSIDIAN);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    for (t = -1; t < 2; ++t) {
                                                        for (fuse = -1; fuse < 2; ++fuse) {
                                                            if (t != 0 || fuse != 0) {
                                                                player.getLocation().add(t, 1.0D, fuse).getBlock().setType(Material.GLASS);
                                                            }
                                                        }
                                                    }

                                                    player.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                                                    player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().setType(Material.OBSIDIAN);
                                                    player.getLocation().add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.WATER);
                                                }
                                            } else if (drop == LBDrop.KARL) {
                                                LBEntitiesSpecial.spawnKarl(player, bloc, false);
                                            } else if (drop == LBDrop.HELL_HOUND) {
                                                LBEntitiesSpecial.spawnHellHound(player, bloc, false);
                                            } else if (drop == LBDrop.METEORS_1) {
                                                t = 15;
                                                if (luckyBlock.hasDropOption("Times")) {
                                                    t = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
                                                }

                                                HTasks.Met(bloc, t);
                                            } else if (drop == LBDrop.ILLUMINATI) {
                                                if (player != null) {
                                                    ChatComponent component = new ChatComponent();
                                                    component.addText("§5[§6Illuminati§5]", Hover.show_text, "§cIlluminati confirmed!\n\n§eDon't click!", Click.run_command, String.format("/%s illuminati", ILBCmd.lcmd));
                                                    component.send(player);
                                                }
                                            } else if (drop == LBDrop.SCHEMATIC_STRUCTURE) {
                                                if (LuckyBlockPlugin.isWorldEditValid()) {
                                                    if (luckyBlock.hasDropOption("LocationType") && luckyBlock.hasDropOption("File")) {
                                                        int[] i = new int[3];
                                                        if (luckyBlock.hasDropOption("Loc") && luckyBlock.getDropOption("Loc").getValues().length == 3) {
                                                            Object[] a = luckyBlock.getDropOption("Loc").getValues();
                                                            i[0] = Integer.parseInt(a[0].toString());
                                                            i[1] = Integer.parseInt(a[1].toString());
                                                            i[2] = Integer.parseInt(a[2].toString());
                                                        }

                                                        f = luckyBlock.getDropOption("File").getValues()[0].toString();
                                                        File fi = new File(LuckyBlockPlugin.d() + "Drops/" + f + ".schematic");
                                                        String s = luckyBlock.getDropOption("LocationType").getValues()[0].toString();
                                                        Location a = null;
                                                        if (s.equalsIgnoreCase("PLAYER")) {
                                                            if (player != null) {
                                                                a = player.getLocation();
                                                            }
                                                        } else if (s.equalsIgnoreCase("BLOCK")) {
                                                            a = bloc;
                                                        }

                                                        if (a != null) {
                                                            Schematic.loadArea(fi, a.add(i[0], i[1], i[2]));
                                                        }
                                                    }
                                                } else if (player != null) {
                                                    send(player, "schematic_error");
                                                }
                                            } else if (drop == LBDrop.RIP) {
                                                HTasks.rip(player, block);
                                            } else if (drop != LBDrop.NONE) {
                                                TNTPrimed tnt;
                                                if (drop == LBDrop.TNT_IN_THE_MIDDLE) {
                                                    mat = Material.DIAMOND_BLOCK;
                                                    data = 0;
                                                    fuse = 65;
                                                    float power = 5.0F;
                                                    if (luckyBlock.hasDropOption("BlocksMaterial")) {
                                                        mat = Material.getMaterial(luckyBlock.getDropOption("BlocksMaterial").getValues()[0].toString());
                                                    }

                                                    if (luckyBlock.hasDropOption("BlocksData")) {
                                                        data = Byte.parseByte(luckyBlock.getDropOption("BlocksData").getValues()[0].toString());
                                                    }

                                                    if (luckyBlock.hasDropOption("Fuse")) {
                                                        fuse = Integer.parseInt(luckyBlock.getDropOption("Fuse").getValues()[0].toString());
                                                    }

                                                    if (luckyBlock.hasDropOption("ExplosionPower")) {
                                                        power = Float.parseFloat(luckyBlock.getDropOption("ExplosionPower").getValues()[0].toString());
                                                    }

                                                    block.getRelative(BlockFace.EAST).setType(mat);
                                                    block.getRelative(BlockFace.EAST).setData(data);
                                                    block.getRelative(BlockFace.WEST).setType(mat);
                                                    block.getRelative(BlockFace.WEST).setData(data);
                                                    block.getRelative(BlockFace.SOUTH).setType(mat);
                                                    block.getRelative(BlockFace.SOUTH).setData(data);
                                                    block.getRelative(BlockFace.NORTH).setType(mat);
                                                    block.getRelative(BlockFace.NORTH).setData(data);
                                                    block.getRelative(BlockFace.UP).setType(mat);
                                                    block.getRelative(BlockFace.UP).setData(data);
                                                    block.getRelative(BlockFace.DOWN).setType(mat);
                                                    block.getRelative(BlockFace.DOWN).setData(data);
                                                    tnt = (TNTPrimed) block.getWorld().spawnEntity(bloc.add(0.5D, 0.0D, 0.5D), EntityType.PRIMED_TNT);
                                                    tnt.setFuseTicks(fuse);
                                                    tnt.setYield(power);
                                                } else if (drop == LBDrop.FLYING_TNTS) {
                                                    fuse = 80;
                                                    if (luckyBlock.hasDropOption("Times")) {
                                                        t = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
                                                    }

                                                    if (luckyBlock.hasDropOption("Fuse")) {
                                                        fuse = Integer.parseInt(luckyBlock.getDropOption("Fuse").getValues()[0].toString());
                                                    }

                                                    for (fuse = 8; fuse > 0; --fuse) {
                                                        Bat b = (Bat) block.getWorld().spawnEntity(bloc, EntityType.BAT);
                                                        b.setHealth(0.5D);
                                                        b.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0));
                                                        tnt = (TNTPrimed) block.getWorld().spawnEntity(bloc, EntityType.PRIMED_TNT);
                                                        tnt.setFuseTicks(fuse);
                                                        b.addPassenger(tnt);
                                                    }
                                                } else if (drop == LBDrop.ANVIL_JAIL) {
                                                    if (player != null) {
                                                        double h = 35.0D;
                                                        if (luckyBlock.hasDropOption("Height")) {
                                                            h = Double.parseDouble(luckyBlock.getDropOption("Height").getValues()[0].toString());
                                                        }

                                                        Block b = player.getLocation().getBlock();
                                                        jail(b);
                                                        b.getWorld().spawnFallingBlock(player.getLocation().add(0.0D, h, 0.0D), new MaterialData(Material.ANVIL));
                                                    }
                                                } else if (drop == LBDrop.LAVA_JAIL) {
                                                    if (player != null) {
                                                        t = 55;
                                                        if (luckyBlock.hasDropOption("Ticks")) {
                                                            t = Integer.parseInt(luckyBlock.getDropOption("Ticks").getValues()[0].toString());
                                                        }

                                                        Block b = player.getLocation().getBlock();
                                                        jail(b);
                                                        HTasks.n(player.getLocation().add(0.0D, 2.0D, 0.0D), t);
                                                    }
                                                } else if (drop == LBDrop.EQUIP_ITEM) {
                                                    luckyBlock.hasDropOption("Path");
                                                } else {
                                                    LBDrop var10000 = LBDrop.DONT_MINE;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private static void jail(Block b) {
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
}
