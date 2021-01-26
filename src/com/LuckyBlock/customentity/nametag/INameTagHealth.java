package com.LuckyBlock.customentity.nametag;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

public class INameTagHealth extends EntityNameTag {
    public INameTagHealth.NameTagMode mode_base;
    public int mode;
    public String[] strings;
    private static final ChatColor a;
    private static final ChatColor b;
    private LivingEntity l;
    public int heartsAmount;

    static {
        a = ChatColor.GREEN;
        b = ChatColor.GRAY;
    }

    public INameTagHealth() {
        this.mode_base = INameTagHealth.NameTagMode.DEFAULT;
        this.mode = 0;
        this.strings = new String[]{"█", "♥"};
        this.heartsAmount = 1;
    }

    protected void func_load() {
        this.l = (LivingEntity)this.getAttachedEntity();
    }

    protected int getTickTime() {
        return 5;
    }

    protected void tick() {
        String s;
        double p;
        int i;
        if (this.mode_base == INameTagHealth.NameTagMode.DEFAULT) {
            s = this.strings[this.mode];
            if (this.l != null) {
                p = this.l.getHealth() / this.l.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                p *= 10.0D;
                i = (int)p;
                if (i > 8) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + s + s + s + s + s + s + s + s);
                } else if (i == 8) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + s + s + s + s + s + s + s + b + s);
                } else if (i == 7) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + s + s + s + s + s + s + b + s + s);
                } else if (i == 6) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + s + s + s + s + s + b + s + s + s);
                } else if (i == 5) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + s + s + s + s + b + s + s + s + s);
                } else if (i == 4) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + s + s + s + b + s + s + s + s + s);
                } else if (i == 3) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + s + s + b + s + s + s + s + s + s);
                } else if (i == 2) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + s + b + s + s + s + s + s + s + s);
                } else if (i == 1) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + s + b + s + s + s + s + s + s + s);
                } else if (i == 0) {
                    this.armorStand.setCustomName(INameTagHealth.a + s + b + s + s + s + s + s + s + s + s);
                }
            }
        } else if (this.mode_base == INameTagHealth.NameTagMode.THREE_HEARTS) {
            s = this.strings[1];
            p = this.l.getHealth() / this.l.getMaxHealth();
            p *= 100.0D;
            i = (int)p;
            if (i > 66) {
                this.armorStand.setCustomName(INameTagHealth.a + s + s + s);
            } else if (i < 67 && i > 33) {
                this.armorStand.setCustomName(INameTagHealth.a + s + s + b + s);
            } else if (i < 33) {
                this.armorStand.setCustomName(INameTagHealth.a + s + b + s + s);
            }
        } else if (this.mode_base == INameTagHealth.NameTagMode.PERCENT) {
            double pizdec = this.l.getHealth() / this.l.getMaxHealth();
            pizdec *= 100.0D;
            int a = (int)pizdec;
            this.armorStand.setCustomName(ChatColor.DARK_PURPLE + "%" + ChatColor.GREEN + a);
        } else if (this.mode_base == INameTagHealth.NameTagMode.CUSTOM_HEARTS) {
            if (this.heartsAmount > 20) {
                this.heartsAmount = 20;
            }

            s = this.strings[1];
            p = this.l.getHealth() / this.l.getMaxHealth();
            p *= 100.0D;
            i = (int)p;
            int part = 100 / this.heartsAmount;
            int b1 = 0;
            String c = "" + INameTagHealth.a;

            for(i += part; i >= part; i -= part) {
                if (b1 < this.heartsAmount) {
                    c = c + s;
                }

                ++b1;
            }

            if (b1 < this.heartsAmount) {
                for(c = c + b; b1 < this.heartsAmount; ++b1) {
                    c = c + s;
                }
            }

            this.armorStand.setCustomName(c);
        }

    }

    protected void onsave(ConfigurationSection c) {
        c.set("Mode", this.mode);
        c.set("ModeBase", this.mode_base.name());
        c.set("Hearts", this.heartsAmount);
    }

    protected void onload(ConfigurationSection c) {
        this.mode = c.getInt("Mode");
        this.mode_base = INameTagHealth.NameTagMode.valueOf(c.getString("ModeBase"));
        this.heartsAmount = c.getInt("Hearts");
    }

    public static enum NameTagMode {
        DEFAULT,
        THREE_HEARTS,
        PERCENT,
        CUSTOM_HEARTS;

        private NameTagMode() {
        }
    }
}
