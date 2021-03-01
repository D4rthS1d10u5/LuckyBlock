package com.mcgamer199.luckyblock.listeners;

import com.mcgamer199.luckyblock.api.enums.ItemProperty;
import com.mcgamer199.luckyblock.api.IObjects;
import com.mcgamer199.luckyblock.LuckyBlockPlugin;
import com.mcgamer199.luckyblock.lb.LBType;
import com.mcgamer199.luckyblock.api.ColorsClass;
import com.mcgamer199.luckyblock.tags.EntityTags;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class EntityEvents extends ColorsClass implements Listener {

    public EntityEvents() {
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (EntityTags.entityDrops.containsKey(entity.getUniqueId())) {
            event.getDrops().clear();
            ItemStack[] items = EntityTags.entityDrops.get(entity.getUniqueId());

            for (int x = 0; x < items.length; ++x) {
                if (items[x] != null) {
                    event.getDrops().add(items[x]);
                }
            }

            EntityTags.entityDrops.remove(entity.getUniqueId());
            EntityTags.save();
        }

    }

    @EventHandler
    private void onHurtZombie(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Zombie && event.getDamager() instanceof Player) {
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.getCustomName() != null && zombie.getCustomName().equalsIgnoreCase("" + ChatColor.BLUE + ChatColor.BOLD + "[Illuminati]")) {
                event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 20.0F, true);
            }
        }

    }

    @EventHandler
    public void onHurtEntity(EntityDamageEvent event) {
        int m2;
        if (event.getEntity() instanceof Squid) {
            Squid squid = (Squid) event.getEntity();
            if (squid.getCustomName() != null && squid.getCustomName().startsWith(yellow + "Lucky Squid")) {
                double f = event.getFinalDamage();
                m2 = (int) (squid.getHealth() - f);
                if (m2 < 0) {
                    m2 = 0;
                }

                squid.setCustomName(yellow + "Lucky Squid " + red + m2 + white + "/" + red + squid.getMaxHealth());
            }
        } else if (event.getEntity() instanceof Bat) {
            Bat bat = (Bat) event.getEntity();
            if (bat.hasMetadata("luckybat") && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                event.setCancelled(true);
            }

            if (bat.hasMetadata("flyinglb")) {
                if (bat.getPassenger() != null) {
                    bat.getPassenger().remove();
                }

                bat.remove();
            }
        } else if (event.getEntity() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getEntity();
            if (wolf.getCustomName() != null && wolf.getCustomName().startsWith("" + yellow + bold + "Wolf")) {
                int h = (int) event.getFinalDamage();
                int m1 = (int) (wolf.getHealth() - (double) h);
                m2 = (int) wolf.getMaxHealth();
                if (m1 < 0) {
                    m1 = 0;
                }

                wolf.setCustomName("" + yellow + bold + "Wolf " + green + m1 + white + "/" + green + m2);
            }
        }

    }

    @EventHandler
    public void mobsRegenHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getEntity();
            if (wolf.getCustomName() != null && wolf.getCustomName().startsWith("" + yellow + bold + "Wolf")) {
                int h = (int) event.getAmount();
                int m1 = (int) (wolf.getHealth() + (double) h);
                int m2 = (int) wolf.getMaxHealth();
                wolf.setCustomName("" + yellow + bold + "Wolf " + green + m1 + white + "/" + green + m2);
            }
        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Pig) {
            Player player = null;
            Pig pig = (Pig) event.getEntity();
            if (pig.getCustomName() != null && pig.getCustomName().startsWith(yellow + "Lucky Pig") && pig.getHealth() - event.getFinalDamage() <= 0.0D) {
                String[] s = pig.getCustomName().split(yellow + "Lucky Pig ");
                if (event.getDamager() instanceof Player) {
                    player = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Projectile) {
                    Projectile p = (Projectile) event.getDamager();
                    if (p.getShooter() instanceof Player) {
                        player = (Player) p.getShooter();
                    }
                }

                if (player != null) {
                    if (s.length == 2) {
                        String[] g = s[1].split(green + "+");
                        String[] h = g[1].split(" Health");
                        Damageable d = player;
                        int plus = Integer.parseInt(h[0]);

                        try {
                            player.setHealth(d.getHealth() + (double) plus);
                        } catch (Exception var11) {
                            player.setHealth(player.getMaxHealth());
                        }
                    }

                    if (pig.getVehicle() != null) {
                        pig.getVehicle().remove();
                    }
                }
            }
        }

    }

    @EventHandler
    private void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) && event.getDamager() instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) event.getDamager();
            if (l.getEquipment().getItemInMainHand() != null && l.getEquipment().getItemInMainHand().getType() != Material.AIR && l.getEquipment().getItemInMainHand().hasItemMeta() && l.getEquipment().getItemInMainHand().getItemMeta().hasEnchant(LuckyBlockPlugin.enchantment_lightning)) {
                event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
            }
        }

    }

    @EventHandler
    private void onStrike(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) event.getEntity();
            if (l.getEquipment().getItemInMainHand() != null && l.getEquipment().getItemInMainHand().getType() != Material.AIR && l.getEquipment().getItemInMainHand().hasItemMeta() && l.getEquipment().getItemInMainHand().getItemMeta().hasEnchant(LuckyBlockPlugin.enchantment_lightning) && event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    private void onLBItemDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item) {
            Item itemI = (Item) event.getEntity();
            if (itemI.getItemStack() != null && LBType.isLB(itemI.getItemStack()) && LBType.fromItem(itemI.getItemStack()).hasItemProperty(ItemProperty.INVINCIBLE)) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    private void onLBItemDespawn(ItemDespawnEvent event) {
        if (LBType.isLB(event.getEntity().getItemStack()) && LBType.fromItem(event.getEntity().getItemStack()).hasItemProperty(ItemProperty.HOPPER)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Villager && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && IObjects.getValue("lv_spawn").equals(true)) {
            random.nextInt(100);
            Villager villager = (Villager) event.getEntity();
            villager.setProfession(Villager.Profession.BLACKSMITH);
            villager.setCustomName(ChatColor.YELLOW + "Lucky Villager");
            List<MerchantRecipe> recipes = new ArrayList();
            int r = random.nextInt(8) + 5;
            List<LBType> types = new ArrayList();

            int x;
            for (x = 0; x < LBType.getTypes().size(); ++x) {
                if (!LBType.getTypes().get(x).disabled) {
                    types.add(LBType.getTypes().get(x));
                }
            }

            for (x = 0; x < r; ++x) {
                LBType type = types.get(random.nextInt(types.size()));
                int luck = type.getRandomP();
                MerchantRecipe recipe = new MerchantRecipe(type.toItemStack(luck), 16);
                int p = random.nextInt(48) + 5;
                int a = random.nextInt(3) + 1;
                if (a == 1) {
                    recipe.addIngredient(new ItemStack(Material.DIAMOND, p));
                } else if (a == 2) {
                    recipe.addIngredient(new ItemStack(Material.EMERALD, p));
                } else if (a == 3) {
                    recipe.addIngredient(new ItemStack(Material.GOLD_INGOT, p));
                }

                recipes.add(recipe);
            }

            villager.setRecipes(recipes);
        }

    }
}

