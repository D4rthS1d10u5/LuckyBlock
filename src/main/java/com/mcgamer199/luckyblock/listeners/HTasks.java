package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.logic.ColorsClass;
import com.mcgamer199.luckyblock.util.RandomUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import com.mcgamer199.luckyblock.util.SoundUtils;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class HTasks extends ColorsClass {
    private static final Random randoms = new Random();

    public HTasks() {
    }

    static Material getRandomMaterial(Material... mats) {
        return mats[random.nextInt(mats.length)];
    }

    static void Tower(final Block block, int dmg, final String type) {
        final Location loc = block.getLocation().add(0.5D, 0.0D, 0.5D);
        block.getWorld().playEffect(loc, Effect.POTION_BREAK, dmg);
        int[] i1 = tower_rblock(type);
        FallingBlock fb = block.getWorld().spawnFallingBlock(loc.add(0.0D, 10.0D, 0.0D), i1[0], (byte) i1[1]);
        fb.setDropItem(false);

        Scheduler.timer(new BukkitRunnable() {
            private int loop = RandomUtils.nextInt(4) + 6;

            @Override
            public void run() {
                if (this.loop > 1) {
                    int[] i2 = HTasks.tower_rblock(type);
                    block.getWorld().getHighestBlockAt(loc).getWorld().spawnFallingBlock(loc, i2[0], (byte) i2[1]).setDropItem(false);
                    --this.loop;
                } else if (this.loop == 1) {
                    FallingBlock bb = block.getWorld().getHighestBlockAt(loc).getWorld().spawnFallingBlock(loc, Material.DIAMOND_BLOCK, (byte) 0);
                    HTasks.Tower1(bb);
                    cancel();
                }
            }
        }, 6, 6);
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
        Scheduler.later(() -> fb.getLocation().getWorld().strikeLightning(fb.getLocation()), 3);
    }

    static void STUCK(final Player player, final Location loc, final int time) {
        Scheduler.timer(new BukkitRunnable() {
            private int a = time;

            @Override
            public void run() {
                if (this.a > 0) {
                    player.teleport(loc);
                    --this.a;
                } else {
                    cancel();
                }
            }
        }, 5, 10);
    }

    static void Met(final Location loc, final int times) {
        Scheduler.timer(new BukkitRunnable() {
            private int i = times;

            @Override
            public void run() {
                if (this.i > 0) {
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc.add(0.0D, 1.0D, 0.0D), Material.COBBLESTONE, (byte) 0);
                    fb.setDropItem(false);
                    int h = HTasks.randoms.nextInt(6) - 3;
                    double g = (double) h / 5.0D;
                    int h1 = HTasks.randoms.nextInt(6) - 3;
                    double g1 = (double) h1 / 5.0D;
                    int h2 = HTasks.randoms.nextInt(15) + 14;
                    double g2 = (double) h2 / 10.0D;
                    fb.setVelocity(new Vector(g, g2, g1));
                    HTasks.Meteor(fb, 15.0F);
                    --this.i;
                } else {
                    cancel();
                }
            }
        }, 2, 2);
    }

    static void Meteor(final FallingBlock fb, final float explosionPower) {
        Scheduler.timer(new BukkitRunnable() {
            private int x = 0;

            @Override
            public void run() {
                int xx;
                if (fb.isValid()) {
                    fb.getWorld().spawnParticle(Particle.SMOKE_LARGE, fb.getLocation(), 170, 0.3D, 0.2D, 0.3D, 0.0D);

                    for (Entity e : fb.getNearbyEntities(6.0D, 6.0D, 6.0D)) {
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
                        boolean breakBlocks = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionGrief");
                        boolean setFire = LuckyBlockPlugin.instance.config.getBoolean("Allow.ExplosionFire");
                        fb.getWorld().createExplosion(xx, y, z, explosionPower, setFire, breakBlocks);
                    } catch (Exception var6) {
                        var6.printStackTrace();
                    }

                    fb.remove();
                    cancel();
                }
            }
        }, 3, 3);
    }

    private static void met3(final Item item) {
        Scheduler.later(item::remove, 300);
    }

    private static void met1(final Item item) {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (item.isValid()) {

                    for (Entity e : item.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
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
                            cancel();
                        }
                    }
                } else {
                    cancel();
                }
            }
        }, 10, 10);
    }

    private static void met2(final Block block, final Material mat, final byte data) {
        Scheduler.later(() -> {
            block.setType(mat);
            block.setData(data);
        }, 360);
    }

    static void LightningR(final Player player, final Block block, final int count) {
        Scheduler.timer(new BukkitRunnable() {
            private int x = count;

            @Override
            public void run() {
                if (this.x > 0) {
                    player.getWorld().strikeLightning(block.getLocation());
                    --this.x;
                } else {
                    cancel();
                }
            }
        }, 0, 4);
    }

    static void rain(final Location loc, final int times, final int fuse) {
        Scheduler.timer(new BukkitRunnable() {
            private int x = times;

            @Override
            public void run() {
                if (this.x > 0) {
                    TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(fuse);
                    int h = HTasks.randoms.nextInt(4) - 2;
                    double g = (double) h / 10.0D;
                    int h1 = HTasks.randoms.nextInt(4) - 2;
                    double g1 = (double) h1 / 10.0D;
                    tnt.setVelocity(new Vector(g, 1.0D, g1));
                    SoundUtils.playFixedSound(loc, SoundUtils.getSound("lb_drop_tntrain"), 1.0F, 0.0F, 50);
                    --this.x;
                } else {
                    cancel();
                }
            }
        }, 5, 5);
    }

    static void a(Player player, final Zombie zombie) {
        Scheduler.later(zombie::remove, 80);
    }

    static void itemRain(final Location loc, final int time, final Material[] mats, Short[] data) {
        int i = 0;
        short d = 0;

        int x;
        for (x = 0; x < mats.length; ++x) {
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

        short finalD = d;
        int finalX = x;
        Scheduler.timer(new BukkitRunnable() {
            private int times = time;

            @Override
            public void run() {
                if (this.times > 0) {
                    Material mat = mats[finalX];
                    Item item = loc.getWorld().dropItem(loc, new ItemStack(mat, 1, finalD));
                    item.setVelocity(new Vector(0.0D, 0.8D, 0.0D));
                    --this.times;
                    SoundUtils.playFixedSound(loc, SoundUtils.getSound("lb_drop_itemrain"), 1.0F, 0.0F, 20);
                } else {
                    cancel();
                }
            }
        }, 2, 2);
    }

    static void b(final Location loc, final int times, Material[] mats) {
        int i = 0;

        int x;
        for (x = 0; x < mats.length; ++x) {
            if (mats[x] != null) {
                ++i;
            } else {
                x = mats.length;
            }
        }

        x = randoms.nextInt(i);
        final Material m = mats[x];
        Scheduler.timer(new BukkitRunnable() {
            private int x = times;

            @Override
            public void run() {
                if (this.x > 0) {
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, m, (byte) 0);
                    fb.setDropItem(false);
                    int h = HTasks.randoms.nextInt(4) - 2;
                    double g = (double) h / 5.0D;
                    int h1 = HTasks.randoms.nextInt(4) - 2;
                    double g1 = (double) h1 / 5.0D;
                    fb.setVelocity(new Vector(g, 1.0D, g1));
                    SoundUtils.playFixedSound(loc, SoundUtils.getSound("lb_drop_blockrain_launch"), 1.0F, 1.0F, 50);
                    HTasks.b1(fb);
                    --this.x;
                } else {
                    cancel();
                }
            }
        }, 3, 3);
    }

    static void b1(final FallingBlock fb) {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (!fb.isValid()) {
                    MaterialData d = new MaterialData(fb.getMaterial(), fb.getBlockData());
                    fb.getWorld().spawnParticle(Particle.BLOCK_CRACK, fb.getLocation(), 100, 0.3D, 0.1D, 0.3D, 0.0D, d);
                    SoundUtils.playFixedSound(fb.getLocation(), SoundUtils.getSound("lb_drop_blockrain_land"), 1.0F, 1.0F, 60);
                    cancel();
                }
            }
        }, 0, 2);
    }

    static void c(final Location loc, final int times, final boolean critical, final boolean bounce) {
        Scheduler.timer(new BukkitRunnable() {

            private int x = times;

            @Override
            public void run() {
                if (this.x > 0) {
                    Arrow a = (Arrow) loc.getWorld().spawnEntity(loc, EntityType.ARROW);
                    int h = HTasks.randoms.nextInt(16) - 8;
                    double g = (double) h / 50.0D;
                    int h1 = HTasks.randoms.nextInt(16) - 8;
                    double g1 = (double) h1 / 50.0D;
                    a.setVelocity(new Vector(g, 1.2D, g1));
                    a.setCritical(critical);
                    a.setBounce(bounce);
                    SoundUtils.playFixedSound(loc, SoundUtils.getSound("lb_drop_arrowrain"), 1.0F, 1.0F, 50);
                    --this.x;
                } else {
                    cancel();
                }
            }
        }, 1, 1);
    }

    static void d(Location loc) {
        final ArmorStand s = (ArmorStand) loc.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX(), loc.getY() - 1.0D, loc.getZ()), EntityType.ARMOR_STAND);
        s.teleport(loc);
        s.setMetadata("hrocket", new FixedMetadataValue(LuckyBlockPlugin.instance, "" + s.getUniqueId()));
        s.setVisible(false);
        s.setGravity(false);
        s.setHelmet(new ItemStack(Material.IRON_BLOCK));

        Scheduler.timer(new BukkitRunnable() {

            private int times = 80;

            @Override
            public void run() {
                if (this.times > 0) {
                    Location loc = new Location(s.getWorld(), s.getLocation().getX(), s.getLocation().getY() + 1.0D, s.getLocation().getZ());
                    if (loc.getBlock() != null && loc.getBlock().getType().isSolid() || loc.getBlock().getRelative(BlockFace.UP) != null && loc.getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
                        cancel();
                        s.getWorld().createExplosion(s.getLocation(), 7.0F, true);
                        s.remove();
                    }

                    s.teleport(s.getLocation().add(0.0D, 2.2D, 0.0D));
                    s.getWorld().spawnParticle(Particle.SMOKE_LARGE, s.getLocation(), 35, 1.0D, 0.5D, 1.0D, 0.0D);

                    for (Entity e : s.getNearbyEntities(2.0D, 4.0D, 2.0D)) {
                        if (!(e instanceof Player)) {
                            e.setFireTicks(60);
                        }
                    }

                    --this.times;
                } else {
                    cancel();
                    s.getWorld().spawnParticle(Particle.FLAME, s.getLocation(), 100, 0.3D, 0.3D, 0.3D, 0.0D);
                    s.remove();
                }
            }
        }, 1, 1);
    }

    static void f(final Location loc, LuckyBlock luckyBlock) {
        int times = 30;
        if (luckyBlock.hasDropOption("Times")) {
            times = Integer.parseInt(luckyBlock.getDropOption("Times").getValues()[0].toString());
        }

        int finalTimes = times;
        Scheduler.timer(new BukkitRunnable() {
            private int x = finalTimes;

            @Override
            public void run() {
                if (this.x > 0) {
                    ThrownExpBottle xp = (ThrownExpBottle) loc.getWorld().spawnEntity(loc, EntityType.THROWN_EXP_BOTTLE);
                    int h = HTasks.randoms.nextInt(8) - 4;
                    double g = (double) h / 50.0D;
                    int h1 = HTasks.randoms.nextInt(8) - 4;
                    double g1 = (double) h1 / 60.0D;
                    xp.setVelocity(new Vector(g, 0.9D, g1));
                    xp.setBounce(true);
                    --this.x;
                } else {
                    cancel();
                }
            }
        }, 1, 2);
    }

    static void g(final Dispenser dispenser) {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (dispenser.getBlock().getType() != Material.DISPENSER) {
                    cancel();
                }

                if (dispenser.getInventory().contains(Material.ARROW)) {
                    dispenser.dispense();
                } else {
                    dispenser.getBlock().breakNaturally(null);
                    cancel();
                }
            }
        }, 3 ,1);
    }

    static void h(final Player player, final int times, int ticks) {
        Scheduler.timer(new BukkitRunnable() {
            private int t = times;

            @Override
            public void run() {
                if (this.t > 0) {
                    if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) && player.getHealth() > 0.0D) {
                        player.setHealth(Math.max(0.0D, player.getHealth() - 1.0D));
                    }

                    --this.t;
                } else {
                    cancel();
                }
            }
        }, ticks, ticks);
    }

    static void FireWorks(final Block block) {
        Scheduler.timer(new BukkitRunnable() {
            private int times = RandomUtils.nextInt(5) + 5;

            @Override
            public void run() {
                if (this.times > 0) {
                    for (int x = LuckyBlockPlugin.randoms.nextInt(18) + 8; x > 0; --x) {
                        Firework fwork = (Firework) block.getWorld().spawnEntity(block.getLocation(), EntityType.FIREWORK);
                        FireworkMeta fwm = fwork.getFireworkMeta();
                        Type type = RandomUtils.getRandomObject(Type.values());

                        FireworkEffect f = FireworkEffect.builder().flicker(RandomUtils.nextBoolean())
                                .withColor(Color.fromBGR(LuckyBlockPlugin.randoms.nextInt(255), LuckyBlockPlugin.randoms.nextInt(255), LuckyBlockPlugin.randoms.nextInt(255)))
                                .withColor(Color.fromBGR(LuckyBlockPlugin.randoms.nextInt(255), LuckyBlockPlugin.randoms.nextInt(255), LuckyBlockPlugin.randoms.nextInt(255)))
                                .withColor(Color.fromBGR(LuckyBlockPlugin.randoms.nextInt(255), LuckyBlockPlugin.randoms.nextInt(255), LuckyBlockPlugin.randoms.nextInt(255)))
                                .withFade(Color.fromBGR(LuckyBlockPlugin.randoms.nextInt(255), LuckyBlockPlugin.randoms.nextInt(255), LuckyBlockPlugin.randoms.nextInt(255)))
                                .with(type)
                                .trail(RandomUtils.nextBoolean())
                                .build();

                        fwm.clearEffects();
                        fwm.addEffect(f);
                        fwm.setPower(RandomUtils.nextInt(3) + 1);
                        fwork.setFireworkMeta(fwm);
                    }

                    --this.times;
                } else {
                    cancel();
                }
            }
        }, 5, 20);
    }

    static void Tree(final Block block, final TreeType treetype) {
        if (treetype == TreeType.CHORUS_PLANT) {
            block.getRelative(BlockFace.DOWN).setType(Material.ENDER_STONE);
        } else {
            block.getRelative(BlockFace.DOWN).setType(Material.DIRT);
        }

        Scheduler.later(() -> block.getWorld().generateTree(block.getLocation(), treetype), 1);
    }

    static void FakeDiamond(final Item item, int ticks) {
        Scheduler.later(item::remove, ticks);
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
        for (c = 5; c > 0; --c) {
            for (d = 5; d > 0; --d) {
                block.getLocation().add(a, -1.0D, b).getBlock().setType(Material.SMOOTH_BRICK);
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
        for (e = 5; e > 0; --e) {
            for (f = 5; f > 0; --f) {
                block.getLocation().add(c, 2.0D, d).getBlock().setType(Material.SMOOTH_BRICK);
                ++c;
            }

            c = -2;
            ++d;
        }

        e = -1;
        f = -1;

        for (int x = 3; x > 0; --x) {
            for (int y = 3; y > 0; --y) {
                block.getLocation().add(e, 3.0D, f).getBlock().setType(Material.SMOOTH_BRICK);
                ++e;
            }

            e = -1;
            ++f;
        }

        block.getLocation().add(0.0D, 3.0D, 0.0D).getBlock().setType(Material.LAVA);
        trap2(block, ticks);
    }

    private static void trap2(final Block block, int ticks) {
        Scheduler.later(() -> block.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().setType(Material.AIR), ticks);
    }

    static void Bedrock(final Block block) {
        Scheduler.later(() -> {
            block.setType(Material.BEDROCK);
            if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                block.getRelative(BlockFace.UP).setType(Material.SIGN_POST);
                Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
                sign.setLine(1, "Well, there's");
                sign.setLine(2, "your problem");
                sign.update(true);
            }
        }, 1);
    }

    public static void spawnLB(LuckyBlock luckyBlock, final Location bloc) {
        String s = null;
        if (luckyBlock.customDrop != null) {
            s = luckyBlock.customDrop.getName();
        } else if (luckyBlock.getDrop() != null) {
            s = luckyBlock.getDrop().name();
        }

        final ItemStack it = luckyBlock.getType().toItemStack(luckyBlock.getLuck(), null, s);
        Scheduler.later(() -> {
            bloc.getWorld().dropItem(bloc, it);
            bloc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, bloc, 150, 0.3D, 0.3D, 0.3D, 0.0D);
        }, 3);
    }

    static void i(final Location loc, int range) {
        if (range > 50) {
            range = 50;
        }

        if (range < 0) {
            range = 0;
        }

        int finalRange = range;
        Scheduler.timer(new BukkitRunnable() {
            private int taskRange = finalRange;
            private int g = 1;

            @Override
            public void run() {
                if (this.taskRange > 0) {
                    for (int x = this.g * -1; x < this.g + 1; ++x) {
                        for (int y = -4; y < 5; ++y) {
                            for (int z = this.g * -1; z < this.g + 1; ++z) {
                                Location l = new Location(loc.getWorld(), loc.getX() + (double) x, loc.getY() + (double) y, loc.getZ() + (double) z);
                                if (l.getBlock().getType().isSolid() && l.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                                    l.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
                                }
                            }
                        }
                    }

                    --this.taskRange;
                    ++this.g;
                } else {
                    cancel();
                }
            }
        }, 3, 20);
    }

    static void d_Item(final ItemStack[] items, final Location loc, final LuckyBlock luckyBlock) {
        Scheduler.timer(new BukkitRunnable() {
            private int x = 0;

            @Override
            public void run() {
                if (items != null && this.x < items.length) {
                    if (items[this.x] != null) {
                        Item item = loc.getWorld().dropItem(loc, items[this.x]);
                        int h = HTasks.randoms.nextInt(4) - 2;
                        double g = (double) h / 50.0D;
                        int h1 = HTasks.randoms.nextInt(4) - 2;
                        double g1 = (double) h1 / 50.0D;
                        item.setVelocity(new Vector(g, 0.4D, g1));
                        if (luckyBlock.hasDropOption("ShowItemName") && luckyBlock.getDropOption("ShowItemName").getValues()[0].toString().equalsIgnoreCase("true") && items[this.x].hasItemMeta() && items[this.x].getItemMeta().hasDisplayName()) {
                            item.setCustomName(items[this.x].getItemMeta().getDisplayName());
                            item.setCustomNameVisible(true);
                        }

                        SoundUtils.playFixedSound(loc, SoundUtils.getSound("lb_drop_itemrain"), 1.0F, 0.0F, 50);
                        ++this.x;
                    }
                } else {
                    cancel();
                }
            }
        }, 5, 5);
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

        int finalRange = range;
        if (mode.equalsIgnoreCase("surface")) {
            Scheduler.timer(new BukkitRunnable() {
                private int taskRange = finalRange;
                private int g = 1;

                @Override
                public void run() {
                    if (this.taskRange > 0) {
                        for (int x = this.g * -1; x < this.g + 1; ++x) {
                            for (int y = -4; y < 5; ++y) {
                                for (int z = this.g * -1; z < this.g + 1; ++z) {
                                    Location l = new Location(loc.getWorld(), loc.getX() + (double) x, loc.getY() + (double) y, loc.getZ() + (double) z);
                                    if (l.getBlock().getType().isSolid() && l.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                                        l.getBlock().setType(mat);
                                        l.getBlock().setData(data);
                                    }
                                }
                            }
                        }

                        --this.taskRange;
                        ++this.g;
                    } else {
                        cancel();
                    }
                }
            }, delay, delay);
        } else if (mode.equalsIgnoreCase("all")) {
            Scheduler.timer(new BukkitRunnable() {
                private int taskRange = finalRange;
                private int g = 1;

                @Override
                public void run() {
                    if (this.taskRange > 0) {
                        for (int x = this.g * -1; x < this.g + 1; ++x) {
                            for (int y = this.g * -1; y < this.g + 1; ++y) {
                                for (int z = this.g * -1; z < this.g + 1; ++z) {
                                    Location l = new Location(loc.getWorld(), loc.getX() + (double) x, loc.getY() + (double) y, loc.getZ() + (double) z);
                                    if (l.getBlock().getType().isSolid() && l.getBlock().getType() != Material.AIR) {
                                        l.getBlock().setType(mat);
                                        l.getBlock().setData(data);
                                    }
                                }
                            }
                        }

                        --this.taskRange;
                        ++this.g;
                    } else {
                        cancel();
                    }
                }
            }, delay, delay);
        }
    }

    static void k(final Player player) {
        Scheduler.create(() -> player.teleport(player.getLocation().add(0.0D, -1.0D, 0.0D)))
                .predicate(() -> player.getLocation().getY() > 0)
                .timer(7, 7);
    }

    static void l(final Dropper dropper) {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (dropper.getBlock().getType() != Material.DROPPER) {
                    cancel();
                }

                if (dropper.getInventory().contains(Material.DIAMOND)) {
                    dropper.drop();
                } else {
                    dropper.getBlock().breakNaturally(null);
                    cancel();
                }
            }
        }, 3, 3);
    }

    public static void m(final Location loc, int repeat, final LBType type) {
        Scheduler.timer(new BukkitRunnable() {

            private int times = repeat;

            @Override
            public void run() {
                if(times > 0) {
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, type.getType(), (byte) type.getData());
                    fb.setDropItem(false);
                    int h = HTasks.randoms.nextInt(4) - 2;
                    double g = (double) h / 5.0D;
                    int h1 = HTasks.randoms.nextInt(4) - 2;
                    double g1 = (double) h1 / 5.0D;
                    fb.setVelocity(new Vector(g, 1.0D, g1));
                    SoundUtils.playFixedSound(loc, SoundUtils.getSound("lb_drop_lbrain"), 1.0F, 1.0F, 50);
                    HTasks.m_1(fb, type.getType(), (byte) type.getData());
                    times--;
                } else {
                    cancel();
                }
            }
        }, 3, 3);
    }

    private static void m_1(final FallingBlock fallingBlock, final Material type, final byte data) {
        Scheduler.timer(new BukkitRunnable() {
            @Override
            public void run() {
                if (!fallingBlock.isValid()) {
                    Block block = fallingBlock.getLocation().getBlock();
                    if (block.getType() == type) {
                        LuckyBlock luckyBlock = new LuckyBlock(LBType.fromMaterialAndData(type, data), block, 0, null, true, true);
                        luckyBlock.playEffects();
                    }

                    cancel();
                }
            }
        }, 5, 5);
    }

    static void n(final Location loc, int ticks) {
        Scheduler.later(() -> loc.getBlock().setType(Material.LAVA), ticks);
    }

    static void rip(Player player, Block block) {
        block.setType(Material.SKULL);
        block.setData((byte) 1);
        block.getRelative(BlockFace.DOWN).setType(Material.DIRT);
        block.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).setType(Material.DIRT);
        block.getRelative(BlockFace.NORTH).setType(Material.STONE);
        block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).setType(Material.STONE);
        block.getRelative(BlockFace.UP).setType(Material.WALL_SIGN);
        block.getRelative(BlockFace.UP).setData((byte) 3);
        Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
        sign.setLine(0, "RIP");
        sign.setLine(2, player.getName());
        sign.update(true);
        Skull skull = (Skull) block.getState();
        skull.setSkullType(SkullType.PLAYER);
        skull.setRotation(BlockFace.SOUTH);
        skull.setOwningPlayer(player);
        skull.update(true);
    }
}
