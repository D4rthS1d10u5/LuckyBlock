package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.sound.SoundManager;
import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import com.mcgamer199.luckyblock.logic.ITask;
import com.mcgamer199.luckyblock.logic.SchedulerTask;

import java.util.Iterator;
import java.util.Random;

public class HTasks extends ColorsClass {
    private static Random randoms = new Random();

    public HTasks() {
    }

    static Material getRandomMaterial(Material... mats) {
        return mats[random.nextInt(mats.length)];
    }

    static void Tower(final Block block, int dmg, final String type) {
        final Location loc = block.getLocation().add(0.5D, 0.0D, 0.5D);
        block.getWorld().playEffect(loc, Effect.POTION_BREAK, dmg);
        int[] i1 = tower_rblock(type);
        FallingBlock fb = block.getWorld().spawnFallingBlock(loc.add(0.0D, 10.0D, 0.0D), i1[0], (byte)i1[1]);
        fb.setDropItem(false);
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int loop;

            {
                this.loop = LuckyBlock.randoms.nextInt(4) + 6;
            }

            public void run() {
                if (this.loop > 1) {
                    int[] i2 = HTasks.tower_rblock(type);
                    block.getWorld().getHighestBlockAt(loc).getWorld().spawnFallingBlock(loc, i2[0], (byte)i2[1]).setDropItem(false);
                    --this.loop;
                } else if (this.loop == 1) {
                    FallingBlock bb = block.getWorld().getHighestBlockAt(loc).getWorld().spawnFallingBlock(loc, Material.DIAMOND_BLOCK, (byte)0);
                    HTasks.Tower1(bb);
                    --this.loop;
                } else if (this.loop < 1) {
                    task.run();
                }

            }
        }, 6L, 6L));
    }

    private static int[] tower_rblock(String type) {
        int[] i = new int[]{Material.STAINED_CLAY.getId(), random.nextInt(15)};
        if (type.equalsIgnoreCase("b")) {
            i = new int[]{Material.GLASS.getId(), 0};
        } else if (type.equalsIgnoreCase("c")) {
            i = new int[]{getRandomMaterial(Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.LAPIS_BLOCK, Material.EMERALD_BLOCK).getId(), 0};
        } else if (type.equalsIgnoreCase("d")) {
            i = new int[]{Material.SANDSTONE.getId(), random.nextInt(3)};
        } else if (type.equalsIgnoreCase("e")) {
            i = new int[]{Material.WOOL.getId(), random.nextInt(16)};
        } else if (type.equalsIgnoreCase("f")) {
            i = new int[]{Material.WOOD.getId(), random.nextInt(6)};
        }

        return i;
    }

    private static void Tower1(final FallingBlock fb) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                Location loc;
                if (fb.isValid()) {
                    if (fb.isOnGround()) {
                        loc = fb.getLocation();
                        loc = fb.getLocation();
                        loc.getWorld().strikeLightning(loc);
                        task.run();
                    }
                } else {
                    loc = fb.getLocation();
                    loc = fb.getLocation();
                    loc.getWorld().strikeLightning(loc);
                    task.run();
                }

            }
        }, 3L, 1L));
    }

    static void STUCK(final Player player, final Location loc, final int time) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            int a = time;

            public void run() {
                if (this.a > 0) {
                    player.teleport(loc);
                    --this.a;
                } else {
                    task.run();
                }

            }
        }, 5L, 10L));
    }

    static void Met(final Location loc, final int times) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            int i = times;

            public void run() {
                if (this.i > 0) {
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc.add(0.0D, 1.0D, 0.0D), Material.COBBLESTONE, (byte)0);
                    fb.setDropItem(false);
                    int h = HTasks.randoms.nextInt(6) - 3;
                    double g = (double)h / 5.0D;
                    int h1 = HTasks.randoms.nextInt(6) - 3;
                    double g1 = (double)h1 / 5.0D;
                    int h2 = HTasks.randoms.nextInt(15) + 14;
                    double g2 = (double)h2 / 10.0D;
                    fb.setVelocity(new Vector(g, g2, g1));
                    HTasks.Meteor(fb, 15.0F);
                    --this.i;
                } else {
                    task.run();
                }

            }
        }, 2L, 2L));
    }

    static void Meteor(final FallingBlock fb, final float explosionPower) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = 0;

            public void run() {
                int xx;
                if (fb.isValid()) {
                    fb.getWorld().spawnParticle(Particle.SMOKE_LARGE, fb.getLocation(), 170, 0.3D, 0.2D, 0.3D, 0.0D);
                    Iterator var2 = fb.getNearbyEntities(6.0D, 6.0D, 6.0D).iterator();

                    while(var2.hasNext()) {
                        Entity e = (Entity)var2.next();
                        if (e instanceof LivingEntity) {
                            e.setFireTicks(100);
                            e.setFallDistance(15.0F);
                        }
                    }

                    xx = HTasks.randoms.nextInt(4) + 1;
                    Material mat = Material.STONE;
                    if (xx == 1) {
                        mat = Material.COBBLESTONE;
                    }

                    Item item = fb.getWorld().dropItem(fb.getLocation(), new ItemStack(mat));
                    item.setPickupDelay(1000);
                    HTasks.met1(item);
                    HTasks.met3(item);
                } else if ((fb.isDead() || fb.isOnGround()) && this.x == 0) {
                    this.x = 1;
                    xx = fb.getLocation().getBlockX();
                    int y = fb.getLocation().getBlockY();
                    int z = fb.getLocation().getBlockZ();

                    try {
                        boolean breakBlocks = LuckyBlock.instance.config.getBoolean("Allow.ExplosionGrief");
                        boolean setFire = LuckyBlock.instance.config.getBoolean("Allow.ExplosionFire");
                        fb.getWorld().createExplosion((double)xx, (double)y, (double)z, explosionPower, setFire, breakBlocks);
                    } catch (Exception var6) {
                        var6.printStackTrace();
                    }

                    fb.remove();
                    task.run();
                }

            }
        }, 3L, 3L));
    }

    private static void met3(final Item item) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                item.remove();
                task.run();
            }
        }, 300L));
    }

    private static void met1(final Item item) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (item.isValid()) {
                    Iterator var2 = item.getNearbyEntities(7.0D, 7.0D, 7.0D).iterator();

                    while(var2.hasNext()) {
                        Entity e = (Entity)var2.next();
                        if (e instanceof LivingEntity) {
                            e.setFireTicks(600);
                        }
                    }

                    if (item.isOnGround()) {
                        Block block = item.getLocation().add(0.0D, -1.0D, 0.0D).getBlock();
                        if (block != null && block.getType() != item.getItemStack().getType() && block.getType().isSolid() && !block.getType().isTransparent()) {
                            Material mat = block.getType();
                            block.setType(item.getItemStack().getType());
                            HTasks.met2(block, mat, block.getData());
                            task.run();
                        }
                    }
                } else {
                    task.run();
                }

            }
        }, 10L, 10L));
    }

    private static void met2(final Block block, final Material mat, final byte data) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                block.setType(mat);
                block.setData(data);
                task.run();
            }
        }, 360L));
    }

    static void LightningR(final Player player, final Block block, final int count) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = count;

            public void run() {
                if (this.x > 0) {
                    player.getWorld().strikeLightning(block.getLocation());
                    --this.x;
                } else {
                    task.run();
                }

            }
        }, 0L, 4L));
    }

    static void rain(final Location loc, final int times, final int fuse) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = times;

            public void run() {
                if (this.x > 0) {
                    TNTPrimed tnt = (TNTPrimed)loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(fuse);
                    int h = HTasks.randoms.nextInt(4) - 2;
                    double g = (double)h / 10.0D;
                    int h1 = HTasks.randoms.nextInt(4) - 2;
                    double g1 = (double)h1 / 10.0D;
                    tnt.setVelocity(new Vector(g, 1.0D, g1));
                    SoundManager.playFixedSound(loc, HTasks.getSound("lb_drop_tntrain"), 1.0F, 0.0F, 50);
                    --this.x;
                } else {
                    task.run();
                }

            }
        }, 5L, 5L));
    }

    static void a(Player player, final Zombie zombie) {
        SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                zombie.remove();
            }
        }, 80L));
    }

    static void itemRain(final Location loc, final int time, final Material[] mats, Short[] data) {
        int i = 0;
        short d = 0;

        int x;
        for(x = 0; x < mats.length; ++x) {
            if (mats[x] != null) {
                ++i;
            } else {
                x = mats.length;
            }
        }

        x = randoms.nextInt(i);
        if (data.length > x && data[x] != null) {
            d = data[x];
        }

        final SchedulerTask task = new SchedulerTask();
        short finalD = d;
        int finalX = x;
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int times = time;

            public void run() {
                if (this.times > 0) {
                    Material mat = mats[finalX];
                    Item item = loc.getWorld().dropItem(loc, new ItemStack(mat, 1, finalD));
                    item.setVelocity(new Vector(0.0D, 0.8D, 0.0D));
                    --this.times;
                    SoundManager.playFixedSound(loc, HTasks.getSound("lb_drop_itemrain"), 1.0F, 0.0F, 20);
                } else {
                    task.run();
                }

            }
        }, 2L, 2L));
    }

    static void b(final Location loc, final int times, Material[] mats) {
        int i = 0;

        int x;
        for(x = 0; x < mats.length; ++x) {
            if (mats[x] != null) {
                ++i;
            } else {
                x = mats.length;
            }
        }

        x = randoms.nextInt(i);
        final Material m = mats[x];
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = times;

            public void run() {
                if (this.x > 0) {
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, m, (byte)0);
                    fb.setDropItem(false);
                    int h = HTasks.randoms.nextInt(4) - 2;
                    double g = (double)h / 5.0D;
                    int h1 = HTasks.randoms.nextInt(4) - 2;
                    double g1 = (double)h1 / 5.0D;
                    fb.setVelocity(new Vector(g, 1.0D, g1));
                    SoundManager.playFixedSound(loc, HTasks.getSound("lb_drop_blockrain_launch"), 1.0F, 1.0F, 50);
                    HTasks.b1(fb);
                    --this.x;
                } else {
                    task.run();
                }

            }
        }, 3L, 3L));
    }

    static void b1(final FallingBlock fb) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (!fb.isValid()) {
                    MaterialData d = new MaterialData(fb.getMaterial(), fb.getBlockData());
                    fb.getWorld().spawnParticle(Particle.BLOCK_CRACK, fb.getLocation(), 100, 0.3D, 0.1D, 0.3D, 0.0D, d);
                    SoundManager.playFixedSound(fb.getLocation(), HTasks.getSound("lb_drop_blockrain_land"), 1.0F, 1.0F, 60);
                    task.run();
                }

            }
        }, 0L, 2L));
    }

    static void c(final Location loc, final int times, final boolean critical, final boolean bounce) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = times;

            public void run() {
                if (this.x > 0) {
                    Arrow a = (Arrow)loc.getWorld().spawnEntity(loc, EntityType.ARROW);
                    int h = HTasks.randoms.nextInt(16) - 8;
                    double g = (double)h / 50.0D;
                    int h1 = HTasks.randoms.nextInt(16) - 8;
                    double g1 = (double)h1 / 50.0D;
                    a.setVelocity(new Vector(g, 1.2D, g1));
                    a.setCritical(critical);
                    a.setBounce(bounce);
                    SoundManager.playFixedSound(loc, HTasks.getSound("lb_drop_arrowrain"), 1.0F, 1.0F, 50);
                    --this.x;
                } else {
                    task.run();
                }

            }
        }, 1L, 1L));
    }

    static void d(Location loc) {
        final SchedulerTask task = new SchedulerTask();
        final ArmorStand s = (ArmorStand)loc.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX(), loc.getY() - 1.0D, loc.getZ()), EntityType.ARMOR_STAND);
        s.teleport(loc);
        s.setMetadata("hrocket", new FixedMetadataValue(LuckyBlock.instance, "" + s.getUniqueId()));
        s.setVisible(false);
        s.setGravity(false);
        s.setHelmet(new ItemStack(Material.IRON_BLOCK));
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = 80;

            public void run() {
                if (this.x > 0) {
                    Location loc = new Location(s.getWorld(), s.getLocation().getX(), s.getLocation().getY() + 1.0D, s.getLocation().getZ());
                    if (loc.getBlock() != null && loc.getBlock().getType().isSolid() || loc.getBlock().getRelative(BlockFace.UP) != null && loc.getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
                        task.run();
                        s.getWorld().createExplosion(s.getLocation(), 7.0F, true);
                        s.remove();
                    }

                    s.teleport(s.getLocation().add(0.0D, 2.2D, 0.0D));
                    s.getWorld().spawnParticle(Particle.SMOKE_LARGE, s.getLocation(), 35, 1.0D, 0.5D, 1.0D, 0.0D);
                    Iterator var3 = s.getNearbyEntities(2.0D, 4.0D, 2.0D).iterator();

                    while(var3.hasNext()) {
                        Entity e = (Entity)var3.next();
                        if (!(e instanceof Player)) {
                            e.setFireTicks(60);
                        }
                    }

                    --this.x;
                } else {
                    task.run();
                    s.getWorld().spawnParticle(Particle.FLAME, s.getLocation(), 100, 0.3D, 0.3D, 0.3D, 0.0D);
                    s.remove();
                }

            }
        }, 1L, 1L));
    }

    static void f(final Location loc, LB lb) {
        int times = 30;
        if (lb.hasDropOption("Times")) {
            times = Integer.parseInt(lb.getDropOption("Times").getValues()[0].toString());
        }

        final SchedulerTask task = new SchedulerTask();
        int finalTimes = times;
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = finalTimes;

            public void run() {
                if (this.x > 0) {
                    ThrownExpBottle xp = (ThrownExpBottle)loc.getWorld().spawnEntity(loc, EntityType.THROWN_EXP_BOTTLE);
                    int h = HTasks.randoms.nextInt(8) - 4;
                    double g = (double)h / 50.0D;
                    int h1 = HTasks.randoms.nextInt(8) - 4;
                    double g1 = (double)h1 / 60.0D;
                    xp.setVelocity(new Vector(g, 0.9D, g1));
                    xp.setBounce(true);
                    --this.x;
                } else {
                    task.run();
                }

            }
        }, 1L, 2L));
    }

    static void g(final Dispenser dispenser) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (dispenser.getBlock().getType() != Material.DISPENSER) {
                    task.run();
                }

                if (dispenser.getInventory().contains(Material.ARROW)) {
                    dispenser.dispense();
                } else {
                    dispenser.getBlock().breakNaturally((ItemStack)null);
                    task.run();
                }

            }
        }, 3L, 1L));
    }

    static void h(final Player player, final int times, int ticks) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int t = times;

            public void run() {
                if (this.t > 0) {
                    if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) && player.getHealth() > 0.0D) {
                        player.setHealth(Math.max(0.0D, player.getHealth() - 1.0D));
                    }

                    --this.t;
                } else {
                    task.run();
                }

            }
        }, (long)ticks, (long)ticks));
    }

    static void FireWorks(final Block block) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x;

            {
                this.x = HTasks.randoms.nextInt(5) + 5;
            }

            public void run() {
                if (this.x > 0) {
                    for(int x = LuckyBlock.randoms.nextInt(18) + 8; x > 0; --x) {
                        Firework fwork = (Firework)block.getWorld().spawnEntity(block.getLocation(), EntityType.FIREWORK);
                        FireworkMeta fwm = fwork.getFireworkMeta();
                        Random r = new Random();
                        int rt = r.nextInt(4) + 1;
                        FireworkEffect.Type type = FireworkEffect.Type.BALL;
                        if (rt == 1) {
                            type = FireworkEffect.Type.BALL;
                        }

                        if (rt == 2) {
                            type = FireworkEffect.Type.BALL_LARGE;
                        }

                        if (rt == 3) {
                            type = FireworkEffect.Type.BURST;
                        }

                        if (rt == 4) {
                            type = FireworkEffect.Type.CREEPER;
                        }

                        if (rt == 5) {
                            type = FireworkEffect.Type.STAR;
                        }

                        FireworkEffect f = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(Color.fromBGR(LuckyBlock.randoms.nextInt(255), LuckyBlock.randoms.nextInt(255), LuckyBlock.randoms.nextInt(255))).withColor(Color.fromBGR(LuckyBlock.randoms.nextInt(255), LuckyBlock.randoms.nextInt(255), LuckyBlock.randoms.nextInt(255))).withColor(Color.fromBGR(LuckyBlock.randoms.nextInt(255), LuckyBlock.randoms.nextInt(255), LuckyBlock.randoms.nextInt(255))).withFade(Color.fromBGR(LuckyBlock.randoms.nextInt(255), LuckyBlock.randoms.nextInt(255), LuckyBlock.randoms.nextInt(255))).with(type).trail(r.nextBoolean()).build();
                        fwm.clearEffects();
                        fwm.addEffect(f);
                        int rp = r.nextInt(3) + 1;
                        fwm.setPower(rp);
                        fwork.setFireworkMeta(fwm);
                    }

                    --this.x;
                } else {
                    task.run();
                }

            }
        }, 5L, 20L));
    }

    static void Tree(final Block block, final TreeType treetype) {
        if (treetype == TreeType.CHORUS_PLANT) {
            block.getRelative(BlockFace.DOWN).setType(Material.ENDER_STONE);
        } else {
            block.getRelative(BlockFace.DOWN).setType(Material.DIRT);
        }

        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                block.getWorld().generateTree(block.getLocation(), treetype);
                task.run();
            }
        }, 1L));
    }

    static void FakeDiamond(final Item item, int ticks) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                item.remove();
                task.run();
            }
        }, (long)ticks));
    }

    static void Trap(Player player, int ticks) {
        Block block = player.getLocation().getBlock();
        block.getLocation().add(2.0D, 0.0D, 0.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(2.0D, 0.0D, 1.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(2.0D, 0.0D, -1.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(2.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(2.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        int a = -2;
        int b = -2;

        int c;
        int d;
        for(c = 5; c > 0; --c) {
            for(d = 5; d > 0; --d) {
                block.getLocation().add((double)a, -1.0D, (double)b).getBlock().setType(Material.SMOOTH_BRICK);
                ++a;
            }

            a = -2;
            ++b;
        }

        block.getLocation().add(1.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(1.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(0.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(0.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(-1.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(-1.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(-2.0D, 0.0D, 2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(-2.0D, 0.0D, -2.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(-2.0D, 0.0D, 0.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(-2.0D, 0.0D, 1.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(-2.0D, 0.0D, -1.0D).getBlock().setType(Material.SMOOTH_BRICK);
        block.getLocation().add(2.0D, 1.0D, 0.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(2.0D, 1.0D, 1.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(2.0D, 1.0D, -1.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(2.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(2.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(1.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(1.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(0.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(0.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(-1.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(-1.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(-2.0D, 1.0D, 2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(-2.0D, 1.0D, -2.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(-2.0D, 1.0D, 0.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(-2.0D, 1.0D, 1.0D).getBlock().setType(Material.IRON_FENCE);
        block.getLocation().add(-2.0D, 1.0D, -1.0D).getBlock().setType(Material.IRON_FENCE);
        c = -2;
        d = -2;

        int e;
        int f;
        for(e = 5; e > 0; --e) {
            for(f = 5; f > 0; --f) {
                block.getLocation().add((double)c, 2.0D, (double)d).getBlock().setType(Material.SMOOTH_BRICK);
                ++c;
            }

            c = -2;
            ++d;
        }

        e = -1;
        f = -1;

        for(int x = 3; x > 0; --x) {
            for(int y = 3; y > 0; --y) {
                block.getLocation().add((double)e, 3.0D, (double)f).getBlock().setType(Material.SMOOTH_BRICK);
                ++e;
            }

            e = -1;
            ++f;
        }

        block.getLocation().add(0.0D, 3.0D, 0.0D).getBlock().setType(Material.LAVA);
        trap2(block, ticks);
    }

    private static void trap2(final Block block, int ticks) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                block.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().setType(Material.AIR);
                task.run();
            }
        }, (long)ticks));
    }

    static void Bedrock(final Block block) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncDelayedTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                block.setType(Material.BEDROCK);
                if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                    block.getRelative(BlockFace.UP).setType(Material.SIGN_POST);
                    Sign sign = (Sign)block.getRelative(BlockFace.UP).getState();
                    sign.setLine(1, "Well, there's");
                    sign.setLine(2, "your problem");
                    sign.update(true);
                    task.run();
                }

            }
        }, 1L));
    }

    public static void spawnLB(LB lb, final Location bloc) {
        String s = null;
        if (lb.customDrop != null) {
            s = lb.customDrop.getName();
        } else if (lb.getDrop() != null) {
            s = lb.getDrop().name();
        }

        final ItemStack it = lb.getType().toItemStack(lb.getLuck(), (LBType.LBOption[])null, s);
        final ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
            public void run() {
                bloc.getWorld().dropItem(bloc, it);
                bloc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, bloc, 150, 0.3D, 0.3D, 0.3D, 0.0D);
                task.run();
            }
        }, 3L));
    }

    static void i(final Location loc, int range) {
        if (range > 50) {
            range = 50;
        }

        if (range < 0) {
            range = 0;
        }

        final SchedulerTask task = new SchedulerTask();
        int finalRange = range;
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int r = finalRange;
            int g = 1;

            public void run() {
                if (this.r > 0) {
                    for(int x = this.g * -1; x < this.g + 1; ++x) {
                        for(int y = -4; y < 5; ++y) {
                            for(int z = this.g * -1; z < this.g + 1; ++z) {
                                Location l = new Location(loc.getWorld(), loc.getX() + (double)x, loc.getY() + (double)y, loc.getZ() + (double)z);
                                if (l.getBlock().getType().isSolid() && l.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                                    l.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
                                }
                            }
                        }
                    }

                    --this.r;
                    ++this.g;
                } else {
                    task.run();
                }

            }
        }, 3L, 20L));
    }

    static void d_Item(final ItemStack[] items, final Location loc, final LB lb) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = 0;

            public void run() {
                if (items != null && this.x < items.length) {
                    if (items[this.x] != null) {
                        Item item = loc.getWorld().dropItem(loc, items[this.x]);
                        int h = HTasks.randoms.nextInt(4) - 2;
                        double g = (double)h / 50.0D;
                        int h1 = HTasks.randoms.nextInt(4) - 2;
                        double g1 = (double)h1 / 50.0D;
                        item.setVelocity(new Vector(g, 0.4D, g1));
                        if (lb.hasDropOption("ShowItemName") && lb.getDropOption("ShowItemName").getValues()[0].toString().equalsIgnoreCase("true") && items[this.x].hasItemMeta() && items[this.x].getItemMeta().hasDisplayName()) {
                            item.setCustomName(items[this.x].getItemMeta().getDisplayName());
                            item.setCustomNameVisible(true);
                        }

                        SoundManager.playFixedSound(loc, HTasks.getSound("lb_drop_itemrain"), 1.0F, 0.0F, 50);
                        ++this.x;
                    }
                } else {
                    task.run();
                }

            }
        }, 5L, 5L));
    }

    static void j(final Location loc, int range, final Material mat, final byte data, int delay, String mode) {
        if (range > 50) {
            range = 50;
        }

        if (range < 0) {
            range = 0;
        }

        if (mode.equalsIgnoreCase("all") && range > 32) {
            range = 32;
        }

        final SchedulerTask task = new SchedulerTask();
        if (mode.equalsIgnoreCase("surface")) {
            int finalRange = range;
            task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
                int r = finalRange;
                int g = 1;

                public void run() {
                    if (this.r > 0) {
                        for(int x = this.g * -1; x < this.g + 1; ++x) {
                            for(int y = -4; y < 5; ++y) {
                                for(int z = this.g * -1; z < this.g + 1; ++z) {
                                    Location l = new Location(loc.getWorld(), loc.getX() + (double)x, loc.getY() + (double)y, loc.getZ() + (double)z);
                                    if (l.getBlock().getType().isSolid() && l.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                                        l.getBlock().setType(mat);
                                        l.getBlock().setData(data);
                                    }
                                }
                            }
                        }

                        --this.r;
                        ++this.g;
                    } else {
                        task.run();
                    }

                }
            }, (long)delay, (long)delay));
        } else if (mode.equalsIgnoreCase("all")) {
            int finalRange = range;
            task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
                int r = finalRange;
                int g = 1;

                public void run() {
                    if (this.r > 0) {
                        for(int x = this.g * -1; x < this.g + 1; ++x) {
                            for(int y = this.g * -1; y < this.g + 1; ++y) {
                                for(int z = this.g * -1; z < this.g + 1; ++z) {
                                    Location l = new Location(loc.getWorld(), loc.getX() + (double)x, loc.getY() + (double)y, loc.getZ() + (double)z);
                                    if (l.getBlock().getType().isSolid() && l.getBlock().getType() != Material.AIR) {
                                        l.getBlock().setType(mat);
                                        l.getBlock().setData(data);
                                    }
                                }
                            }
                        }

                        --this.r;
                        ++this.g;
                    } else {
                        task.run();
                    }

                }
            }, (long)delay, (long)delay));
        }

    }

    static void k(final Player player) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (player.getLocation().getY() > 0.0D) {
                    player.teleport(player.getLocation().add(0.0D, -1.0D, 0.0D));
                } else {
                    task.run();
                }

            }
        }, 7L, 7L));
    }

    static void l(final Dropper dropper) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (dropper.getBlock().getType() != Material.DROPPER) {
                    task.run();
                }

                if (dropper.getInventory().contains(Material.DIAMOND)) {
                    dropper.drop();
                } else {
                    dropper.getBlock().breakNaturally((ItemStack)null);
                    task.run();
                }

            }
        }, 3L, 1L));
    }

    public static void m(final Location loc, final int times, final LBType type) {
        final SchedulerTask task = new SchedulerTask();
        task.setId(LuckyBlock.instance.getServer().getScheduler().scheduleSyncRepeatingTask(LuckyBlock.instance, new Runnable() {
            int x = times;

            public void run() {
                if (this.x > 0) {
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, type.getType(), (byte)type.getData());
                    fb.setDropItem(false);
                    int h = HTasks.randoms.nextInt(4) - 2;
                    double g = (double)h / 5.0D;
                    int h1 = HTasks.randoms.nextInt(4) - 2;
                    double g1 = (double)h1 / 5.0D;
                    fb.setVelocity(new Vector(g, 1.0D, g1));
                    SoundManager.playFixedSound(loc, HTasks.getSound("lb_drop_lbrain"), 1.0F, 1.0F, 50);
                    HTasks.m_1(fb, type.getType(), (byte)type.getData());
                    --this.x;
                } else {
                    task.run();
                }

            }
        }, 3L, 3L));
    }

    private static void m_1(final FallingBlock b, final Material type, final byte data) {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (!b.isValid()) {
                    Block block = b.getLocation().getBlock();
                    if (block.getType() == type) {
                        LB lb = new LB(LBType.fromMaterialAndData(type, data), block, 0, (Object)null, true, true);
                        lb.playEffects();
                    }

                    task.run();
                }

            }
        }, 5L, 5L));
    }

    static void n(final Location loc, int ticks) {
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
            public void run() {
                loc.getBlock().setType(Material.LAVA);
            }
        }, (long)ticks));
    }

    static void rip(Player player, Block block) {
        block.setType(Material.SKULL);
        block.setData((byte)1);
        block.getRelative(BlockFace.DOWN).setType(Material.DIRT);
        block.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).setType(Material.DIRT);
        block.getRelative(BlockFace.NORTH).setType(Material.STONE);
        block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).setType(Material.STONE);
        block.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
        block.getRelative(BlockFace.UP).setData((byte)3);
        Sign sign = (Sign)block.getRelative(BlockFace.UP).getState();
        sign.setLine(0, "RIP");
        sign.setLine(2, player.getName());
        sign.update(true);
        Skull skull = (Skull)block.getState();
        skull.setSkullType(SkullType.PLAYER);
        skull.setRotation(BlockFace.SOUTH);
        skull.setOwningPlayer(player);
        skull.update(true);
    }
}
