package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.api.LuckyBlockAPI;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LuckyBlock;
import com.mcgamer199.luckyblock.util.LocationUtils;
import com.mcgamer199.luckyblock.util.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Detector {
    private final int id;
    private String name;
    private final String[] blocks = new String[64];
    private Vector range;
    private Location loc;
    private final UUID uuid;
    private boolean running;

    public Detector(int id) {
        this.id = id;
        this.uuid = UUID.randomUUID();
        this.range = new Vector(7, 7, 7);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getBlocks() {
        return this.blocks;
    }

    public void expand(int i) {
        this.range.setX(this.range.getX() + 1);
        this.range.setY(this.range.getY() + 1);
        this.range.setZ(this.range.getZ() + 1);
    }

    public Vector getRange() {
        return this.range;
    }

    public void setRange(Vector range) {
        this.range = range;
    }

    public Location getLoc() {
        return this.loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void addBlock(Block block) {
        for (int x = 0; x < this.blocks.length; ++x) {
            if (this.blocks[x] == null) {
                this.blocks[x] = LocationUtils.asString(block.getLocation());
                x = this.blocks.length;
            }
        }

    }

    public void addBlock(String dim) {
        for (int x = 0; x < this.blocks.length; ++x) {
            if (this.blocks[x] == null) {
                this.blocks[x] = dim;
                x = this.blocks.length;
            }
        }

    }

    public void dispose() {
        if (LuckyBlockAPI.detectors.contains(this)) {
            LuckyBlockPlugin.instance.detectors.set(LuckyBlockAPI.getDetector(this.id), null);
            LuckyBlockAPI.detectors.remove(this);
        }

        String[] var4;
        int var3 = (var4 = this.blocks).length;

        for (int var2 = 0; var2 < var3; ++var2) {
            String s = var4[var2];
            if (s != null && LocationUtils.blockFromString(s) != null) {
                Block block = LocationUtils.blockFromString(s);
                block.setType(Material.AIR);
            }
        }

        LuckyBlockAPI.saveDetFile();
    }

    public void save() {
        for (int x = 0; x < LuckyBlockAPI.detectors.size(); ++x) {
            Detector d = LuckyBlockAPI.detectors.get(x);
            if (d.getId() == this.id) {
                LuckyBlockAPI.detectors.remove(d);
            }
        }

        LuckyBlockPlugin.instance.detectors.set("Detectors.Detector" + this.uuid + ".ID", this.id);
        if (this.name != null) {
            LuckyBlockPlugin.instance.detectors.set("Detectors.Detector" + this.uuid + ".Name", this.name);
        }

        LuckyBlockPlugin.instance.detectors.set("Detectors.Detector" + this.uuid + ".Range.x", this.range.getX());
        LuckyBlockPlugin.instance.detectors.set("Detectors.Detector" + this.uuid + ".Range.y", this.range.getY());
        LuckyBlockPlugin.instance.detectors.set("Detectors.Detector" + this.uuid + ".Range.z", this.range.getZ());
        List<String> list = new ArrayList();

        for (int i = 0; i < this.blocks.length; ++i) {
            if (this.blocks[i] != null) {
                list.add(this.blocks[i]);
            }
        }

        LuckyBlockPlugin.instance.detectors.set("Detectors.Detector" + this.uuid + ".Blocks", list);
        LuckyBlockAPI.saveDetFile();
        LuckyBlockAPI.detectors.add(this);
    }

    private Block getMainBlock() {
        return LocationUtils.blockFromString(this.blocks[0]);
    }

    private boolean checkBlock(int x, int y, int z) {
        return LuckyBlock.isLuckyBlock(this.getMainBlock().getLocation().add(x, y, z).getBlock());
    }

    private LuckyBlock getLB(int x, int y, int z) {
        return LuckyBlock.getByBlock(this.getMainBlock().getLocation().add(x, y, z).getBlock());
    }

    public void searchForBlocks(Player searcher) {
        if (this.running) {
            searcher.sendMessage(ChatColor.GREEN + "Already running!");
        } else {
            this.running = true;
            Scheduler.timer(new BukkitRunnable() {
                private final int x = (int) Detector.this.range.getX() * -1;
                private final int y = (int) Detector.this.range.getY() * -1;
                private final int z = (int) Detector.this.range.getZ() * -1;

                @Override
                public void run() {
                    if (this.x == (int) Detector.this.range.getX() && this.y == (int) Detector.this.range.getY() && this.z == (int) Detector.this.range.getZ()) {
                        Scheduler.cancelTask(this);
                    } else {
                        Detector.this.checkBlock(0, 0, 0);
                        Detector.this.getLB(0, 0, 0);
                    }
                }
            }, 20, 20);
        }
    }

    void action1(final Player searcher) {
        if (this.running) {
            searcher.sendMessage(ChatColor.GREEN + "Already running!");
        } else {
            Scheduler.timer(new BukkitRunnable() {
                int x = (int) Detector.this.range.getX() * -1;
                int y = (int) Detector.this.range.getY() * -1;
                int z = (int) Detector.this.range.getZ() * -1;
                final int x1 = (int) Detector.this.range.getX() * -1;
                final int z1 = (int) Detector.this.range.getZ() * -1;
                final int x2 = (int) Detector.this.range.getX();
                final int y2 = (int) Detector.this.range.getY();
                final int z2 = (int) Detector.this.range.getZ();
                int total = 0;
                boolean finish = false;

                @Override
                public void run() {
                    boolean changeX = true;
                    boolean changeZ = true;
                    if (Detector.this.checkBlock(this.x, this.y, this.z)) {
                        ++this.total;
                    }

                    if (this.y == this.y2 && this.x == this.x2 && this.z == this.z2) {
                        this.finish = true;
                    }

                    if (this.z == this.z2 && this.x == this.x2) {
                        ++this.y;
                        this.z = this.z1;
                        changeZ = false;
                    }

                    if (this.x == this.x2) {
                        if (changeZ) {
                            ++this.z;
                        }

                        this.x = this.x1;
                        changeX = false;
                    }

                    if (changeX) {
                        ++this.x;
                    }

                    if (this.finish) {
                        searcher.sendMessage("Total: " + this.total);
                        Scheduler.cancelTask(this);
                    }
                }
            }, 3, 5);
        }
    }
}
