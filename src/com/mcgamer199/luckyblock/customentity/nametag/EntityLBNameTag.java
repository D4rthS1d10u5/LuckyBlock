package com.mcgamer199.luckyblock.customentity.nametag;

import com.mcgamer199.luckyblock.lb.LB;
import com.mcgamer199.luckyblock.logic.MyTasks;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import com.mcgamer199.luckyblock.entity.CustomEntity;
import com.mcgamer199.luckyblock.entity.Immunity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EntityLBNameTag extends CustomEntity {
    private LB lb;
    public String text = "none";
    public double[] a_ = new double[]{0.0D, 0.0D, 0.0D};
    private HashMap<String, Object> tags = new HashMap();
    private ArmorStand armorS;

    public EntityLBNameTag() {
    }

    public void spawn(LB lb, int i) {
        this.lb = lb;
        Location l = lb.getBlock().getLocation();
        if (this.a_[0] == 0.0D && this.a_[1] == 0.0D && this.a_[2] == 0.0D) {
            double[] a = new double[]{0.0D, 0.0D, 0.0D};
            if (i == 0) {
                a = new double[]{0.5D, 1.2D, 0.5D};
            } else if (i == 1) {
                a = new double[]{0.5D, 1.5D, 0.5D};
            } else if (i == 2) {
                a = new double[]{0.5D, 1.8D, 0.5D};
            }

            this.a_ = a;
        }

        l.add(this.a_[0], this.a_[1], this.a_[2]);
        if (l != null) {
            ArmorStand as = (ArmorStand)lb.getBlock().getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
            as.setCustomName(this.text);
            as.setCustomNameVisible(true);
            as.setMarker(true);
            as.setGravity(false);
            as.setVisible(false);
            this.armorS = as;
            super.spawn_1(as.getLocation(), as);
        }

    }

    public int getTickTime() {
        return 1;
    }

    protected void onTick() {
        this.armorS.setCustomName(this.text);
        if (this.lb != null) {
            this.entity.teleport(this.lb.getBlock().getLocation().add(this.a_[0], this.a_[1], this.a_[2]));
            if (!this.lb.isValid()) {
                this.remove();
            }
        }

    }

    public LB getLb() {
        return this.lb;
    }

    public Object getTag(String key) {
        return this.tags.get(key);
    }

    public boolean hasTag(String key) {
        return this.tags.containsKey(key);
    }

    public void setValue(String key, Object value) {
        this.tags.put(key, value);
    }

    public void reload(boolean save) {
        if (this.hasTag("IType")) {
            String i = this.getTag("IType").toString();
            if (i.equalsIgnoreCase("Drop")) {
                if (this.lb.hasDropOption("Title")) {
                    this.text = ChatColor.translateAlternateColorCodes('&', this.lb.getDropOption("Title").getValues()[0].toString());
                } else if (this.lb.customDrop != null) {
                    this.text = ChatColor.RED + this.lb.customDrop.getName();
                } else {
                    this.text = ChatColor.RED + this.lb.getDrop().name();
                }
            } else if (i.equalsIgnoreCase("LBType")) {
                this.text = this.lb.getType().getName();
            } else if (i.equalsIgnoreCase("Luck")) {
                this.text = this.lb.getType().getLuckString(this.lb.getLuck());
            }
        }

        if (save) {
            this.save_def();
        }

    }

    protected void onSave(ConfigurationSection c) {
        c.set("LB_Block", LB.blockToString(this.lb.getBlock()));
        c.set("Loc1", this.a_[0]);
        c.set("Loc2", this.a_[1]);
        c.set("Loc3", this.a_[2]);
        Iterator var3 = this.tags.keySet().iterator();

        while(var3.hasNext()) {
            String s = (String)var3.next();
            c.set("Tags." + s, this.tags.get(s));
        }

    }

    protected void onLoad(ConfigurationSection c) {
        String b = c.getString("LB_Block");
        this.a_[0] = c.getDouble("Loc1");
        this.a_[1] = c.getDouble("Loc2");
        this.a_[2] = c.getDouble("Loc3");
        this.armorS = (ArmorStand)this.entity;
        if (c.getConfigurationSection("Tags") != null) {
            Iterator var4 = c.getConfigurationSection("Tags").getKeys(false).iterator();

            while(var4.hasNext()) {
                String s = (String)var4.next();
                this.setValue(s, c.getConfigurationSection("Tags").get(s));
            }
        }

        if (b != null && LB.getFromBlock(MyTasks.stringToBlock(b)) != null) {
            this.lb = LB.getFromBlock(MyTasks.stringToBlock(b));
        }

        this.reload(false);
    }

    public Immunity[] getImmuneTo() {
        return Immunity.values();
    }

    public static List<EntityLBNameTag> getByLB(LB lb) {
        List<EntityLBNameTag> list = new ArrayList();

        for(int x = 0; x < entities.size(); ++x) {
            CustomEntity c = (CustomEntity)entities.get(x);
            if (c instanceof EntityLBNameTag) {
                EntityLBNameTag e = (EntityLBNameTag)c;
                if (e.lb.equals(lb)) {
                    list.add(e);
                }
            }
        }

        return list;
    }
}
