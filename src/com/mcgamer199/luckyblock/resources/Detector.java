package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.engine.LuckyBlock;
import com.mcgamer199.luckyblock.engine.LuckyBlockAPI;
import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.logic.IRange;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.mcgamer199.luckyblock.logic.ITask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Detector {
    private int id;
    private String name;
    private String[] blocks = new String[64];
    private IRange range;
    private Location loc;
    private UUID uuid;
    private boolean running;

    public Detector(int id) {
        this.id = id;
        this.uuid = UUID.randomUUID();
        this.range = new IRange(7, 7, 7);
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String[] getBlocks() {
        return this.blocks;
    }

    public void setRange(IRange range) {
        this.range = range;
    }

    public void expand(int i) {
        this.range.setX(this.range.getX() + 1);
        this.range.setY(this.range.getY() + 1);
        this.range.setZ(this.range.getZ() + 1);
    }

    public IRange getRange() {
        return this.range;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public Location getLoc() {
        return this.loc;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void addBlock(Block block) {
        for(int x = 0; x < this.blocks.length; ++x) {
            if (this.blocks[x] == null) {
                this.blocks[x] = LB.blockToString(block);
                x = this.blocks.length;
            }
        }

    }

    public void addBlock(String dim) {
        for(int x = 0; x < this.blocks.length; ++x) {
            if (this.blocks[x] == null) {
                this.blocks[x] = dim;
                x = this.blocks.length;
            }
        }

    }

    public void dispose() {
        if (LuckyBlockAPI.detectors.contains(this)) {
            LuckyBlock.instance.detectors.set(LuckyBlockAPI.getDet(this.id), (Object)null);
            LuckyBlockAPI.detectors.remove(this);
        }

        String[] var4;
        int var3 = (var4 = this.blocks).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            String s = var4[var2];
            if (s != null && MyTasks.stringToBlock(s) != null) {
                Block block = MyTasks.stringToBlock(s);
                block.setType(Material.AIR);
            }
        }

        LuckyBlockAPI.saveDetFile();
    }

    public void save() {
        for(int x = 0; x < LuckyBlockAPI.detectors.size(); ++x) {
            Detector d = (Detector)LuckyBlockAPI.detectors.get(x);
            if (d.getId() == this.id) {
                LuckyBlockAPI.detectors.remove(d);
            }
        }

        LuckyBlock.instance.detectors.set("Detectors.Detector" + this.uuid + ".ID", this.id);
        if (this.name != null) {
            LuckyBlock.instance.detectors.set("Detectors.Detector" + this.uuid + ".Name", this.name);
        }

        LuckyBlock.instance.detectors.set("Detectors.Detector" + this.uuid + ".Range.x", this.range.getX());
        LuckyBlock.instance.detectors.set("Detectors.Detector" + this.uuid + ".Range.y", this.range.getY());
        LuckyBlock.instance.detectors.set("Detectors.Detector" + this.uuid + ".Range.z", this.range.getZ());
        List<String> list = new ArrayList();

        for(int i = 0; i < this.blocks.length; ++i) {
            if (this.blocks[i] != null) {
                list.add(this.blocks[i]);
            }
        }

        LuckyBlock.instance.detectors.set("Detectors.Detector" + this.uuid + ".Blocks", list);
        LuckyBlockAPI.saveDetFile();
        LuckyBlockAPI.detectors.add(this);
    }

    private Block getMainBlock() {
        return MyTasks.stringToBlock(this.blocks[0]);
    }

    private boolean checkBlock(int x, int y, int z) {
        return LB.isLuckyBlock(this.getMainBlock().getLocation().add((double)x, (double)y, (double)z).getBlock());
    }

    private LB getLB(int x, int y, int z) {
        return LB.getFromBlock(this.getMainBlock().getLocation().add((double)x, (double)y, (double)z).getBlock());
    }

    public void searchForBlocks(Player searcher) {
        if (this.running) {
            searcher.sendMessage(ChatColor.GREEN + "Already running!");
        } else {
            this.running = true;
            final ITask task = new ITask();
            task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
                int x;
                int y;
                int z;
                int total;

                {
                    this.x = Detector.this.range.getX() * -1;
                    this.y = Detector.this.range.getY() * -1;
                    this.z = Detector.this.range.getZ() * -1;
                    this.total = 0;
                }

                public void run() {
                    if (this.x == Detector.this.range.getX() && this.y == Detector.this.range.getY() && this.z == Detector.this.range.getZ()) {
                        task.run();
                    } else {
                        Detector.this.checkBlock(0, 0, 0);
                        Detector.this.getLB(0, 0, 0);
                        this.total = this.total;
                    }

                }
            }, 20L, 20L));
        }
    }

    void action1(final Player searcher) {
        if (this.running) {
            searcher.sendMessage(ChatColor.GREEN + "Already running!");
        } else {
            final ITask task = new ITask();
            task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
                int x;
                int y;
                int z;
                int x1;
                int z1;
                int x2;
                int y2;
                int z2;
                int total;
                boolean finish;

                {
                    this.x = Detector.this.range.getX() * -1;
                    this.y = Detector.this.range.getY() * -1;
                    this.z = Detector.this.range.getZ() * -1;
                    this.x1 = Detector.this.range.getX() * -1;
                    this.z1 = Detector.this.range.getZ() * -1;
                    this.x2 = Detector.this.range.getX();
                    this.y2 = Detector.this.range.getY();
                    this.z2 = Detector.this.range.getZ();
                    this.total = 0;
                    this.finish = false;
                }

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
                        task.run();
                    }

                }
            }, 3L, 5L));
        }
    }
}
