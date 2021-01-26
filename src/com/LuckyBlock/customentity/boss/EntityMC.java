package com.LuckyBlock.customentity.boss;

import com.LuckyBlock.Engine.LuckyBlock;
import com.LuckyBlock.customentity.boss.main.EntityHealer;
import com.LuckyBlock.customentity.boss.main.EntityIPart;
import com.LuckyBlock.customentity.nametag.INameTagHealth;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.core.entity.CustomEntity;
import org.core.entity.Immunity;
import org.core.inventory.ItemMaker;
import org.core.logic.ITask;
import org.bukkit.util.Vector;

import java.util.*;


public class EntityMC extends CustomEntity implements EntityLBBoss {
    private boolean damageable = true;
    private Wither e;
    private static final ItemStack[] ARMOR_HELMETS;
    private static final ItemStack[] ARMOR_CHESTPLATES;
    private static final ItemStack[] ARMOR_LEGGINGS;
    private static final ItemStack[] ARMOR_BOOTS;
    private static final ItemStack ARMY_SWORD;
    private boolean damaged = false;
    private List<EntityIPart> parts = new ArrayList();

    static {
        ARMOR_HELMETS = new ItemStack[]{new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.IRON_HELMET), new ItemStack(Material.GOLD_HELMET), new ItemStack(Material.DIAMOND_HELMET)};
        ARMOR_CHESTPLATES = new ItemStack[]{new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE), new ItemStack(Material.DIAMOND_CHESTPLATE)};
        ARMOR_LEGGINGS = new ItemStack[]{new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.DIAMOND_LEGGINGS)};
        ARMOR_BOOTS = new ItemStack[]{new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.GOLD_BOOTS), new ItemStack(Material.DIAMOND_BOOTS)};
        ARMY_SWORD = ItemMaker.addEnchant(new ItemStack(Material.WOOD_SWORD), Enchantment.DAMAGE_ALL, 10);
    }

    public EntityMC() {
    }

    protected Entity spawnFunction(Location loc) {
        Wither w = (Wither)loc.getWorld().spawnEntity(loc, EntityType.WITHER);
        w.setSilent(true);
        this.e = w;
        this.spawn_parts(loc);
        this.func_tick();
        return w;
    }

    private void spawn_parts(Location loc) {
        Wither w = this.e;
        INameTagHealth n = new INameTagHealth();
        n.mode = 1;
        n.spawn(this.e, new double[]{0.0D, 4.0D, 0.0D});
        EntityIPart p = new EntityIPart(w);
        EntityIPart p1 = new EntityIPart(w);
        EntityIPart p2 = new EntityIPart(w);
        p.item = new ItemStack(Material.DIAMOND_BLOCK);
        p1.item = new ItemStack(Material.DIAMOND_BLOCK);
        p2.item = new ItemStack(Material.DIAMOND_BLOCK);
        p.amountDegree = 0.01D;
        p1.amountDegree = 0.01D;
        p2.amountDegree = 0.01D;
        p.offset = new double[]{0.0D, 3.2D, 0.0D};
        p1.offset = new double[]{1.0D, 3.2D, 0.0D};
        p2.offset = new double[]{-1.0D, 3.2D, 0.0D};
        p.spawn(loc);
        p1.spawn(loc);
        p2.spawn(loc);
        p.update_item();
        p1.update_item();
        p2.update_item();
        p.run_rotate();
        p1.run_rotate();
        p2.run_rotate();
        this.parts.add(p);
        this.parts.add(p1);
        this.parts.add(p2);
        EntityHealer eh = new EntityHealer(w);
        EntityHealer eh1 = new EntityHealer(w);
        eh.delay = 40;
        eh1.delay = 40;
        eh.healValue = 5.0D;
        eh1.healValue = 5.0D;
        eh.spawn(new Location(loc.getWorld(), loc.getX() + 5.0D, loc.getY() + 5.0D, loc.getZ()));
        eh1.spawn(new Location(loc.getWorld(), loc.getX() - 5.0D, loc.getY() + 5.0D, loc.getZ()));
        eh.func_run();
        eh1.func_run();
    }

    private void func_tick() {
        this.func_tick1();
        this.func_tick2();
        this.func_tick3();
    }

    private void func_tick1() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (!EntityMC.this.e.isDead()) {
                    if (EntityMC.this.e.getTarget() != null && EntityMC.this.e.getTarget() != EntityMC.this.e.getTarget() && EntityMC.this.getNearbyMonsters(7, 7, 7) < 15) {
                        for(int x = EntityMC.this.random.nextInt(5) + 4; x > 0; --x) {
                            WitherSkeleton s = (WitherSkeleton)EntityMC.this.e.getWorld().spawnEntity(EntityMC.this.e.getLocation(), EntityType.WITHER_SKELETON);
                            int i = EntityMC.this.random.nextInt(EntityMC.ARMOR_HELMETS.length);
                            s.getEquipment().setHelmet(EntityMC.ARMOR_HELMETS[i]);
                            s.getEquipment().setChestplate(EntityMC.ARMOR_CHESTPLATES[i]);
                            s.getEquipment().setLeggings(EntityMC.ARMOR_LEGGINGS[i]);
                            s.getEquipment().setBoots(EntityMC.ARMOR_BOOTS[i]);
                            s.getEquipment().setHelmetDropChance(0.0F);
                            s.getEquipment().setChestplateDropChance(0.0F);
                            s.getEquipment().setLeggingsDropChance(0.0F);
                            s.getEquipment().setBootsDropChance(0.0F);
                            s.getEquipment().setItemInMainHand(EntityMC.ARMY_SWORD);
                            s.getEquipment().setItemInMainHandDropChance(0.0F);
                            s.setTarget(EntityMC.this.e.getTarget());
                        }
                    }
                } else {
                    task.run();
                }

            }
        }, 20L, 20L));
    }

    private void func_tick2() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (!EntityMC.this.e.isDead()) {
                    List<LivingEntity> l = EntityMC.this.getNearbyUnArmored(10, 10, 10, new EntityType[]{EntityType.SHEEP, EntityType.WITHER_SKELETON});
                    Iterator var3 = l.iterator();

                    while(var3.hasNext()) {
                        LivingEntity a = (LivingEntity)var3.next();
                        a.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 2));
                        a.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                        a.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200, 0));
                        a.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
                        a.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 1));
                    }
                } else {
                    task.run();
                }

            }
        }, 100L, 100L));
    }

    private void func_tick3() {
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlock.instance, new Runnable() {
            public void run() {
                if (!EntityMC.this.e.isDead()) {
                    Item item = EntityMC.this.e.getWorld().dropItem(EntityMC.this.e.getLocation().add(0.0D, 2.0D, 0.0D), ItemMaker.createItem(Material.WOOL, 1, 11, ChatColor.GOLD + "LBWither's Bomb"));
                    double i1 = (double)(EntityMC.this.random.nextInt(80) - 40);
                    double i2 = (double)(EntityMC.this.random.nextInt(80) - 40);
                    double d1 = i1 / 100.0D;
                    double d2 = i2 / 100.0D;
                    item.setVelocity(new Vector(d1, 0.5D, d2));
                    EntityMC.this.func_item_remove(item);
                } else {
                    task.run();
                }

            }
        }, 20L, 40L));
    }

    private void func_item_remove(final Item item) {
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
            public void run() {
                item.remove();
            }
        }, 50L));
    }

    public ItemStack[] getDrops() {
        return new ItemStack[0];
    }

    protected int[] getPercents() {
        return new int[0];
    }

    public String getSpawnEggEntity() {
        return "zombie";
    }

    protected boolean isAnimated() {
        return true;
    }

    protected List<String> getNames() {
        return Arrays.asList("" + ChatColor.YELLOW + ChatColor.BOLD + "MCGamer199", "" + ChatColor.GOLD + ChatColor.BOLD + "MCGamer199");
    }

    public double getDefense() {
        return 3.0D;
    }

    protected void onDamage(EntityDamageEvent event) {
        if (!this.damageable) {
            event.setCancelled(true);
        }

        if (!this.damaged) {
            this.damaged = true;
            ItemStack item = ((EntityIPart)this.parts.get(0)).item;
            int i = this.random.nextInt(100) + 1;
            if (i > 90 && event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent ed = (EntityDamageByEntityEvent)event;
                if (ed.getDamager() instanceof LivingEntity && !this.func_cA2((LivingEntity)ed.getDamager())) {
                    event.setCancelled(true);
                    ((LivingEntity)ed.getDamager()).damage(event.getDamage(), this.e);
                    Iterator var6 = this.parts.iterator();

                    while(var6.hasNext()) {
                        EntityIPart part = (EntityIPart)var6.next();
                        part.item = new ItemStack(Material.EMERALD_BLOCK);
                        part.update_item();
                    }

                    this.func_part_change(25L, item);
                    return;
                }
            }

            Iterator var5 = this.parts.iterator();

            while(var5.hasNext()) {
                EntityIPart part = (EntityIPart)var5.next();
                part.item = new ItemStack(Material.WOOL, 1, (short)14);
                part.update_item();
            }

            this.func_part_change(20L, item);
        } else {
            event.setCancelled(true);
        }

    }

    protected int getNamesDelay() {
        return 10;
    }

    private List<LivingEntity> getNearbyUnArmored(int x, int y, int z, EntityType[] ignoreE) {
        List<LivingEntity> list = new ArrayList();
        Iterator var7 = this.e.getNearbyEntities((double)x, (double)y, (double)z).iterator();

        while(true) {
            Entity e;
            do {
                if (!var7.hasNext()) {
                    return list;
                }

                e = (Entity)var7.next();
            } while(!(e instanceof LivingEntity));

            LivingEntity l = (LivingEntity)e;
            boolean c = true;
            if (ignoreE != null) {
                EntityType[] var13 = ignoreE;
                int var12 = ignoreE.length;

                for(int var11 = 0; var11 < var12; ++var11) {
                    EntityType t = var13[var11];
                    if (l.getType() == t) {
                        c = false;
                        break;
                    }
                }
            }

            if (c && this.func_cA1(l)) {
                list.add(l);
            }
        }
    }

    private boolean func_cA1(LivingEntity e) {
        if (e.getEquipment() != null) {
            ItemStack[] var5;
            int var4 = (var5 = e.getEquipment().getArmorContents()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                ItemStack i = var5[var3];
                if (i != null && (i.getType() == Material.CHAINMAIL_HELMET || i.getType() == Material.CHAINMAIL_CHESTPLATE || i.getType() == Material.CHAINMAIL_LEGGINGS || i.getType() == Material.CHAINMAIL_BOOTS)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean func_cA2(LivingEntity e) {
        if (e.getEquipment() != null && e.getEquipment().getChestplate() != null) {
            ItemStack i = e.getEquipment().getChestplate();
            if (i.hasItemMeta() && i.getItemMeta().hasEnchant(LuckyBlock.enchantment_reflect_prot) && i.getItemMeta().getEnchantLevel(LuckyBlock.enchantment_reflect_prot) > 0) {
                return true;
            }
        }

        return false;
    }

    private void func_part_change(long delay, final ItemStack regular) {
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
            public void run() {
                Iterator var2 = EntityMC.this.parts.iterator();

                while(var2.hasNext()) {
                    EntityIPart part = (EntityIPart)var2.next();
                    part.item = regular;
                    part.update_item();
                }

                EntityMC.this.damaged = false;
            }
        }, delay));
    }

    protected void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {
        event.getEntity().setFallDistance(50.0F);
    }

    public Immunity[] getImmuneTo() {
        return new Immunity[]{Immunity.BLOCK_EXPLOSION, Immunity.ENTITY_EXPLOSION};
    }

    void damageIgnoreArmor(LivingEntity e, int damage) {
        if (e.getHealth() > (double)damage) {
            e.setHealth(e.getHealth() - (double)damage);
        } else {
            e.setHealth(0.0D);
        }

        e.damage(0.0D);
    }

    private int getNearbyMonsters(int x, int y, int z) {
        int a = 0;
        Iterator var6 = this.e.getNearbyEntities((double)x, (double)y, (double)z).iterator();

        while(var6.hasNext()) {
            Entity e = (Entity)var6.next();
            if (e instanceof Monster) {
                ++a;
            }
        }

        return a;
    }

    protected void onSave(ConfigurationSection c) {
        List<String> uuids = new ArrayList();
        Iterator var4 = this.parts.iterator();

        while(var4.hasNext()) {
            EntityIPart part = (EntityIPart)var4.next();
            uuids.add(part.getEntity().getUniqueId().toString());
        }

        c.set("Parts", uuids);
    }

    protected void onLoad(final ConfigurationSection c) {
        this.e = (Wither)this.entity;
        ITask task = new ITask();
        task.setId(ITask.getNewDelayed(LuckyBlock.instance, new Runnable() {
            public void run() {
                List<String> uuids = c.getStringList("Parts");
                if (uuids != null && uuids.size() > 0) {
                    for(int x = 0; x < uuids.size(); ++x) {
                        UUID uuid = UUID.fromString((String)uuids.get(x));
                        CustomEntity ce = CustomEntity.getByUUID(uuid);
                        if (ce != null) {
                            EntityMC.this.parts.add((EntityIPart)ce);
                        }
                    }
                }

                EntityMC.this.func_tick();
            }
        }, 15L));
    }

    public void onHitEntityWithBeam(LivingEntity entity, String tag) {
    }
}
