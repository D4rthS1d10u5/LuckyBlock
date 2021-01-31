package com.mcgamer199.luckyblock.customentity;

import com.mcgamer199.luckyblock.util.ItemStackUtils;
import com.mcgamer199.luckyblock.api.nbt.NBTCompoundWrapper;
import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.logic.ITask;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class CustomEntity {

    public static final List<CustomEntity> entities = new ArrayList();
    public static final List<CustomEntity> classes = new ArrayList();
    protected Entity entity;
    protected Random random = new Random();
    private UUID uuid;
    private BossBar bossBar;

    public CustomEntity() {
    }

    public static boolean isClassValid(String name) {
        name = name.replace("-", ".");
        Iterator var2 = classes.iterator();

        CustomEntity c;
        while (var2.hasNext()) {
            c = (CustomEntity) var2.next();
            if (c.getClass().getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        if (name.startsWith("LB_")) {
            try {
                c = null;
                String[] d = name.split("LB_");
                Class clazz = Class.forName("com.LuckyBlock.customentity." + d[1]);
                if (CustomEntity.class.isAssignableFrom(clazz)) {
                    return true;
                }
            } catch (Exception var3) {
            }
        }

        return false;
    }

    public static CustomEntity getByReplacedEntity(EntityType entitytype) {
        for (int x = 0; x < classes.size(); ++x) {
            if (classes.get(x).replaceEntity() == entitytype) {
                return classes.get(x);
            }
        }

        return null;
    }

    public static CustomEntity getClassByName(String name) {
        name = name.replace("-", ".");
        name = name.replace("LB_", "com.LuckyBlock.customentity.");
        Iterator var2 = classes.iterator();

        while (var2.hasNext()) {
            CustomEntity e = (CustomEntity) var2.next();
            if (e.getClass().getName().equalsIgnoreCase(name)) {
                return e;
            }
        }

        return null;
    }

    public static CustomEntity getByUUID(UUID uuid) {
        for (int x = 0; x < entities.size(); ++x) {
            if (entities.get(x).getUuid().toString().equalsIgnoreCase(uuid.toString())) {
                return entities.get(x);
            }
        }

        return null;
    }

    void add() {
        entities.add(this);
    }

    void b() {
        if (this.entity != null) {
            this.uuid = this.entity.getUniqueId();
        }

        if (this.targetsNearbyEntities() && this.getTargets() != null && this.getTargets().length > 0) {
            this.c();
        }

        this.d();
        final ITask task = new ITask();
        task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
            int x = 0;

            public void run() {
                if (!CustomEntity.this.entity.isDead()) {
                    if (CustomEntity.this.isAnimated() && CustomEntity.this.getNames() != null && CustomEntity.this.getNames().size() > 0) {
                        CustomEntity.this.entity.setCustomName(CustomEntity.this.getNames().get(this.x));
                        if (this.x < CustomEntity.this.getNames().size() - 1) {
                            ++this.x;
                        } else {
                            this.x = 0;
                        }
                    }
                } else {
                    task.run();
                    CustomEntity.this.remove();
                }

            }
        }, 1L, this.getNamesDelay()));
    }

    private void c() {
        if (this.entity != null && this.entity instanceof Creature) {
            final Creature cr = (Creature) this.entity;
            final ITask task = new ITask();
            task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
                public void run() {
                    if (!CustomEntity.this.entity.isDead()) {
                        if (cr.getTarget() == null) {
                            List<Entity> entities = new ArrayList();
                            Iterator var3 = CustomEntity.this.entity.getNearbyEntities(15.0D, 10.0D, 15.0D).iterator();

                            while (true) {
                                while (true) {
                                    Entity ex;
                                    do {
                                        LivingEntity l;
                                        do {
                                            do {
                                                do {
                                                    if (!var3.hasNext()) {
                                                        if (entities.size() > 0) {
                                                            UUID uuid = null;
                                                            if (CustomEntity.this.getPriorities() != null) {
                                                                List<Entity> entitiesx = CustomEntity.this.getEntitiesWithHighestPriorities(entities);
                                                                if (CustomEntity.this.getNearestEntity(entitiesx) != null) {
                                                                    uuid = CustomEntity.this.getNearestEntity(entitiesx).getUniqueId();
                                                                }
                                                            } else if (!CustomEntity.this.useOldSystem()) {
                                                                uuid = CustomEntity.this.getNearestEntity(entities).getUniqueId();
                                                            } else {
                                                                uuid = entities.get(CustomEntity.this.random.nextInt(entities.size())).getUniqueId();
                                                            }

                                                            Iterator var8 = CustomEntity.this.entity.getNearbyEntities(25.0D, 20.0D, 25.0D).iterator();

                                                            while (var8.hasNext()) {
                                                                Entity e = (Entity) var8.next();
                                                                if (e.getUniqueId() == uuid) {
                                                                    cr.setTarget((LivingEntity) e);
                                                                }
                                                            }
                                                        }

                                                        return;
                                                    }

                                                    ex = (Entity) var3.next();
                                                } while (!(ex instanceof LivingEntity));

                                                l = (LivingEntity) ex;
                                            } while (!l.hasAI());
                                        } while (ex == CustomEntity.this.entity);
                                    } while (!CustomEntity.this.containsTarget(ex.getType()));

                                    if (!(ex instanceof Player)) {
                                        entities.add(ex);
                                    } else if (((Player) ex).getGameMode() == GameMode.SURVIVAL || ((Player) ex).getGameMode() == GameMode.ADVENTURE) {
                                        entities.add(ex);
                                    }
                                }
                            }
                        } else if (CustomEntity.this.entity.getLocation().distance(cr.getTarget().getLocation()) > 20.0D || !cr.getTarget().isValid()) {
                            cr.setTarget(null);
                        }
                    } else {
                        CustomEntity.this.remove();
                        task.run();
                    }

                }
            }, 30L, 30L));
        }

    }

    private void d() {
        if (this.hasBossBar() && this.getBossBar() != null && this.getBossBarRange() > 0 && this.getBossBarRange() < 225) {
            final ITask task = new ITask();
            task.setId(ITask.getNewRepeating(LuckyBlockPlugin.instance, new Runnable() {
                public void run() {
                    if (!CustomEntity.this.entity.isDead()) {
                        if (CustomEntity.this.hasBossBar() && CustomEntity.this.getBossBar() != null && CustomEntity.this.getBossBarRange() > 0 && CustomEntity.this.getBossBarRange() < 225) {
                            Iterator var2 = CustomEntity.this.entity.getWorld().getPlayers().iterator();

                            while (var2.hasNext()) {
                                Player p = (Player) var2.next();
                                int d = (int) CustomEntity.this.entity.getLocation().distance(p.getLocation());
                                if (d <= CustomEntity.this.getBossBarRange()) {
                                    if (CustomEntity.this.getBossBar().getPlayers() != null && !CustomEntity.this.getBossBar().getPlayers().contains(p)) {
                                        CustomEntity.this.getBossBar().addPlayer(p);
                                    }
                                } else if (CustomEntity.this.getBossBar().getPlayers() != null && CustomEntity.this.getBossBar().getPlayers().contains(p)) {
                                    CustomEntity.this.getBossBar().removePlayer(p);
                                }
                            }
                        } else {
                            task.run();
                        }
                    } else {
                        task.run();
                    }

                }
            }, 40L, 40L));
        }

    }

    private boolean containsTarget(EntityType entitytype) {
        EntityType[] var5;
        int var4 = (var5 = this.getTargets()).length;

        for (int var3 = 0; var3 < var4; ++var3) {
            EntityType t = var5[var3];
            if (entitytype.name().equalsIgnoreCase(t.name())) {
                return true;
            }
        }

        return false;
    }

    protected EntityType[] getTargets() {
        return null;
    }

    protected boolean defaultDrops() {
        return false;
    }

    protected final void spawn_1(Location loc, Entity e) {
        this.entity = e;
        if (this.entity != null) {
            this.entity.setMetadata("CustomEntity", new FixedMetadataValue(LuckyBlockPlugin.instance, this.getClass().getName()));
            entities.add(this);
            this.b();
            this.save_def();
            CustomEntityEvents.onSpawn(this.entity);
            if (this.hasBossBar() && this.getBossBar() != null) {
                this.getBossBar().setVisible(true);
            }
        }

    }

    public final void spawn(Location loc) {
        if (this.entity == null) {
            this.spawn_1(loc, this.spawnFunction(loc));
        }

    }

    protected Entity spawnFunction(Location loc) {
        return null;
    }

    public final void remove() {
        if (this.getEntity() != null) {
            this.getEntity().remove();
        }

        for (int x = 0; x < entities.size(); ++x) {
            if (entities.get(x).getUuid() == this.getUuid()) {
                entities.remove(x);
            }
        }

        CustomEntityLoader.removeEntity(this);
    }

    public Entity getEntity() {
        return this.entity;
    }

    public ItemStack[] getDrops() {
        return null;
    }

    protected int[] getPercents() {
        return null;
    }

    protected List<String> getNames() {
        return null;
    }

    protected int getNamesDelay() {
        return 0;
    }

    final ItemStack[] getRandomItems() {
        ItemStack[] its = new ItemStack[64];
        int p = 0;
        if (this.getDrops() != null && this.getPercents() != null) {
            for (int x = 0; x < this.getDrops().length; ++x) {
                if (this.getDrops()[x] != null && this.getPercents()[x] > 0) {
                    int r = this.random.nextInt(100) + 1;
                    if (r <= this.getPercents()[x]) {
                        its[p] = this.getDrops()[x];
                        ++p;
                    }
                }
            }
        }

        return its;
    }

    public int getXp() {
        return 0;
    }

    protected void onDeath(EntityDeathEvent event) {
    }

    protected void onDamage(EntityDamageEvent event) {
    }

    protected void onTarget(EntityTargetLivingEntityEvent event) {
    }

    protected void onInteract(PlayerInteractEntityEvent event) {
    }

    protected void onRegainHealth(EntityRegainHealthEvent event) {
    }

    protected void onShootBow(EntityShootBowEvent event) {
    }

    protected void onRegrowWool(SheepRegrowWoolEvent event) {
    }

    protected void onEntityChangeBlock(EntityChangeBlockEvent event) {
    }

    protected void onDamagePlayer(EntityDamageByEntityEvent event) {
    }

    protected void onDamageEntity(EntityDamageByEntityEvent event) {
    }

    protected void onKillPlayer(EntityDamageByEntityEvent event) {
    }

    protected void onKillEntity(EntityDamageByEntityEvent event) {
    }

    protected void onDyeWoolSheep(SheepDyeWoolEvent event) {
    }

    protected void onDamageEntityWithProjectile(EntityDamageByEntityEvent event) {
    }

    protected void onDamagePlayerWithProjectile(EntityDamageByEntityEvent event) {
    }

    protected void onKillEntityWithProjectile(EntityDamageByEntityEvent event) {
    }

    protected void onExplode(EntityExplodeEvent event) {
    }

    protected void onTick() {
    }

    protected void onSpawnNaturally(CreatureSpawnEvent event) {
    }

    protected void onSlimeSplit(SlimeSplitEvent event) {
    }

    protected void onCreeperPower(CreeperPowerEvent event) {
    }

    protected void onCombust(EntityCombustEvent event) {
    }

    protected void onCombustByEntity(EntityCombustByEntityEvent event) {
    }

    protected void onCombustByBlock(EntityCombustByBlockEvent event) {
    }

    protected void onInterPortal(EntityPortalEnterEvent event) {
    }

    protected void onExitPortal(EntityPortalExitEvent event) {
    }

    protected void onEntityBreakDoor(EntityBreakDoorEvent event) {
    }

    protected void onEntityCreatePortal(EntityCreatePortalEvent event) {
    }

    protected void onTame(EntityTameEvent event) {
    }

    protected void onTeleport(EntityTeleportEvent event) {
    }

    protected void onHorseJump(HorseJumpEvent event) {
    }

    protected void onAquireTrade(VillagerAcquireTradeEvent event) {
    }

    protected void onReplenishTrade(VillagerReplenishTradeEvent event) {
    }

    protected void onDamageByEntity(EntityDamageByEntityEvent event) {
    }

    protected void onDamageByPlayer(EntityDamageByEntityEvent event) {
    }

    protected void onKilledByPlayer(EntityDamageByEntityEvent event, Player player) {
    }

    protected void onKilledByEntity(EntityDamageByEntityEvent event) {
    }

    protected void onShootProjectile(ProjectileLaunchEvent event) {
    }

    protected void onChunkLoad() {
    }

    protected int getTickTime() {
        return -1;
    }

    public double getDefense() {
        return 0.0D;
    }

    public final UUID getUuid() {
        return this.uuid;
    }

    public Immunity[] getImmuneTo() {
        return null;
    }

    protected boolean isAnimated() {
        return false;
    }

    public int getAttackDamage() {
        return 0;
    }

    protected final void save_def() {
        CustomEntityLoader.save(this);
    }

    public final boolean isImmuneTo(EntityDamageEvent.DamageCause cause) {
        if (this.getImmuneTo() != null) {
            for (int x = 0; x < this.getImmuneTo().length; ++x) {
                if (this.getImmuneTo()[x].getName().equalsIgnoreCase(cause.name())) {
                    return true;
                }
            }
        }

        return false;
    }

    protected int xpsize() {
        return 100;
    }

    protected boolean targetsNearbyEntities() {
        return false;
    }

    public Particle getDeathParticles() {
        return null;
    }

    public boolean spawnNaturally() {
        return false;
    }

    public Biome[] getSpawnBiomes() {
        return null;
    }

    boolean canSpawn(Biome biome) {
        if (this.getSpawnBiomes() != null && this.getSpawnBiomes().length > 0) {
            Biome[] var5;
            int var4 = (var5 = this.getSpawnBiomes()).length;

            for (int var3 = 0; var3 < var4; ++var3) {
                Biome b = var5[var3];
                if (biome == b) {
                    return true;
                }
            }
        }

        return false;
    }

    protected EntityType replaceEntity() {
        return null;
    }

    protected int getSpawnChance() {
        return 0;
    }

    protected int[] getPriorities() {
        return null;
    }

    int getHighestPriority() {
        if (this.getPriorities() != null) {
            int h = 0;
            int[] var5;
            int var4 = (var5 = this.getPriorities()).length;

            for (int var3 = 0; var3 < var4; ++var3) {
                int i = var5[var3];
                if (i > h) {
                    h = i;
                }
            }

            return h;
        } else {
            return 0;
        }
    }

    private int getHighestValidPriority(List<Entity> entities) {
        if (this.getPriorities() != null) {
            int h = 0;
            Iterator var4 = entities.iterator();

            while (var4.hasNext()) {
                Entity e = (Entity) var4.next();
                if (this.getPriorityByTarget(e.getType()) > h) {
                    h = this.getPriorityByTarget(e.getType());
                }
            }

            return h;
        } else {
            return 0;
        }
    }

    private int getPriorityByTarget(EntityType entitytype) {
        if (this.getTargets() != null && this.getPriorities() != null && this.getTargets().length == this.getPriorities().length) {
            for (int x = 0; x < this.getTargets().length; ++x) {
                if (this.getTargets()[x] == entitytype) {
                    return this.getPriorities()[x];
                }
            }
        }

        return 0;
    }

    private EntityType getTargetByPriority(int priority) {
        if (this.getTargets() != null && this.getPriorities() != null && this.getTargets().length == this.getPriorities().length) {
            for (int x = 0; x < this.getPriorities().length; ++x) {
                if (priority == this.getPriorities()[x]) {
                    return this.getTargets()[x];
                }
            }
        }

        return null;
    }

    private List<Entity> getEntitiesWithHighestPriorities(List<Entity> entities) {
        List<Entity> list = new ArrayList();
        int highest = this.getHighestValidPriority(entities);
        EntityType target = this.getTargetByPriority(highest);
        Iterator var6 = entities.iterator();

        while (var6.hasNext()) {
            Entity e = (Entity) var6.next();
            if (e.getType() == target) {
                list.add(e);
            }
        }

        return list;
    }

    private Entity getNearestEntity(List<Entity> entities) {
        if (entities != null && entities.size() > 0) {
            double distance = 30.0D;
            Entity choosen = null;
            Iterator var6 = entities.iterator();

            while (var6.hasNext()) {
                Entity e = (Entity) var6.next();
                if (this.entity.getLocation().distance(e.getLocation()) < distance) {
                    distance = this.entity.getLocation().distance(e.getLocation());
                    choosen = e;
                }
            }

            return choosen;
        } else {
            return null;
        }
    }

    protected boolean hasBossBar() {
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected boolean useOldSystem() {
        return false;
    }

    public String getSpawnEggEntity() {
        return null;
    }

    public BossBar getBossBar() {
        return this.bossBar;
    }

    public int getBossBarRange() {
        return 32;
    }

    protected void itemsToDrop(Item[] items) {
    }

    protected void xpToDrop(ExperienceOrb[] xp) {
    }

    public boolean itemsEdited() {
        return false;
    }

    public final ItemStack getSpawnEgg() {
        String[] d = this.getClass().getName().replace(".", "_").split("_");
        String a = d[d.length - 1];
        ItemStack item = ItemStackUtils.createItem(Material.MONSTER_EGG, 1, 0, ChatColor.GREEN + "Spawn Entity", Arrays.asList("", ChatColor.GOLD + "Entity: " + ChatColor.GRAY + a, ChatColor.GRAY + "Right click to spawn"));

        NBTCompoundWrapper<?> itemTag = ItemStackUtils.getItemTag(item);

        itemTag.setString("EntityClass", getClass().getName());
        String entitySpawnEgg = getSpawnEggEntity();
        if(entitySpawnEgg != null) {
            NBTCompoundWrapper<?> entityTag = itemTag.newCompound();
            entityTag.setString("id", entitySpawnEgg);
            itemTag.setCompound("EntityTag", entityTag);
        }

        return ItemStackUtils.setItemTag(item, itemTag);
    }

    public void interact(Player player) {
        PlayerInteractEntityEvent e = new PlayerInteractEntityEvent(player, this.entity);
        this.onInteract(e);
    }

    protected double[] particleOptions() {
        return null;
    }

    public double getProjectileDamage() {
        return 0.0D;
    }

    public boolean isAffectedByLooting() {
        return false;
    }

    public final void hideBar() {
        this.hideBar(true);
    }

    public final void hideBar(boolean pl) {
        if (this.getBossBar() != null && this.getBossBar().getPlayers() != null) {
            if (pl) {
                Iterator var3 = this.getBossBar().getPlayers().iterator();

                while (var3.hasNext()) {
                    Player p = (Player) var3.next();
                    this.getBossBar().removePlayer(p);
                }
            }

            this.getBossBar().setVisible(false);
        }

    }

    protected void onSave(ConfigurationSection c) {
    }

    protected void onLoad(ConfigurationSection c) {
    }
}
