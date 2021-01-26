package com.mcgamer199.luckyblock.logic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ActionPerformer {
    public ActionPerformer() {
    }

    public static boolean perform(ColorsClass.ObjectType objectType, String name, Object value) {
        if (value == null) {
            value = "";
        }

        String val = value.toString();
        Object obj = objectType.getObj();
        if (obj instanceof Entity) {
            Entity entity = (Entity)obj;
            if (name.equalsIgnoreCase("set_custom_name")) {
                entity.setCustomName(val);
            }

            if (name.equalsIgnoreCase("set_custom_name_visible")) {
                entity.setCustomNameVisible(Boolean.parseBoolean(val));
            }

            if (name.equalsIgnoreCase("set_fall_distance")) {
                entity.setFallDistance(Float.parseFloat(val));
            }

            if (name.equalsIgnoreCase("set_fire_ticks")) {
                entity.setFireTicks(Integer.parseInt(val));
            }

            if (name.equalsIgnoreCase("set_glowing")) {
                entity.setGlowing(Boolean.parseBoolean(val));
            }

            name.equalsIgnoreCase("set_gravity");
            name.equalsIgnoreCase("set_invulnerable");
            name.equalsIgnoreCase("set_silent");
            if (obj instanceof LivingEntity) {
                LivingEntity living = (LivingEntity)entity;
                if (name.equalsIgnoreCase("clear_inventory")) {
                    living.getEquipment().clear();
                }

                if (name.equalsIgnoreCase("set_health")) {
                    living.setHealth((double)Integer.parseInt(val));
                }

                if (name.equalsIgnoreCase("set_max_health")) {
                    living.setMaxHealth((double)Integer.parseInt(val));
                }

                name.equalsIgnoreCase("set_collidable");
                name.equalsIgnoreCase("set_ai");
                if (obj instanceof Player) {
                    Player player = (Player)obj;
                    if (name.equalsIgnoreCase("set_allow_flight")) {
                        player.setAllowFlight(Boolean.parseBoolean(val));
                    }

                    if (name.equalsIgnoreCase("set_exp")) {
                        player.setExp(Float.parseFloat(val));
                    }

                    if (name.equalsIgnoreCase("set_level")) {
                        player.setLevel(Integer.parseInt(val));
                    }

                    if (name.equalsIgnoreCase("close_inventory")) {
                        player.closeInventory();
                    }

                    if (name.equalsIgnoreCase("chat")) {
                        player.chat(val);
                    }

                    if (name.equalsIgnoreCase("give_xp")) {
                        player.giveExp(Integer.parseInt(val));
                    }

                    if (name.equalsIgnoreCase("give_xp_levels")) {
                        player.giveExpLevels(Integer.parseInt(val));
                    }

                    if (name.equalsIgnoreCase("perform_command")) {
                        player.performCommand(val);
                    }

                    if (name.equalsIgnoreCase("open_chest")) {
                        boolean var8 = true;

                        try {
                            int size = Integer.parseInt(val);
                            if (size % 9 != 0) {
                                return false;
                            }

                            if (size <= 0 || size >= 55) {
                                return false;
                            }

                            player.openInventory(Bukkit.createInventory(player, size));
                        } catch (Exception var10) {
                            return false;
                        }
                    }

                    if (name.equalsIgnoreCase("open_crafting")) {
                        player.openWorkbench((Location)null, true);
                    }

                    if (name.equalsIgnoreCase("open_enchant_table")) {
                        player.openEnchanting((Location)null, true);
                    }
                }
            }
        }

        return true;
    }
}
