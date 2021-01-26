package org.core.entity;

import com.LuckyBlock.Engine.LuckyBlock;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

public class CustomEntityLoader {
    public static File fileF;
    public static FileConfiguration file;

    static {
        fileF = new File(LuckyBlock.instance.getDataFolder() + File.separator + "CustomEntities.yml");
        file = YamlConfiguration.loadConfiguration(fileF);
    }

    public CustomEntityLoader() {
    }

    public static void load() {
        if (file.getConfigurationSection("CustomEntities") != null) {
            ConfigurationSection c = file.getConfigurationSection("CustomEntities");
            Iterator var2 = c.getKeys(false).iterator();

            while(true) {
                while(var2.hasNext()) {
                    String s = (String)var2.next();
                    ConfigurationSection f = c.getConfigurationSection(s);
                    String en = null;
                    UUID uuid = null;
                    Entity entity = null;
                    if (f.getString("Class") != null) {
                        en = f.getString("Class");
                    }

                    if (en != null) {
                        if (f.getString("UUID") != null) {
                            uuid = UUID.fromString(f.getString("UUID"));
                        }

                        for(int x = 0; x < Bukkit.getWorlds().size(); ++x) {
                            World world = (World)Bukkit.getWorlds().get(x);

                            for(int i = 0; i < world.getEntities().size(); ++i) {
                                Entity e = (Entity)world.getEntities().get(i);
                                if (e.getUniqueId().toString().equalsIgnoreCase(uuid.toString())) {
                                    entity = e;
                                    i = world.getEntities().size();
                                    x = Bukkit.getWorlds().size();
                                }
                            }
                        }

                        Object o = getCustomEntity(en);
                        CustomEntity cu = (CustomEntity)o;
                        cu.entity = entity;
                        if (entity != null) {
                            cu.onLoad(f);
                            cu.b();
                            cu.add();
                            CustomEntityEvents.onSpawn(entity);
                        } else {
                            file.set("CustomEntities." + s, (Object)null);

                            try {
                                file.save(fileF);
                            } catch (IOException var12) {
                                var12.printStackTrace();
                            }
                        }
                    } else {
                        c.set(s, (Object)null);

                        try {
                            file.save(fileF);
                        } catch (IOException var11) {
                            var11.printStackTrace();
                        }
                    }
                }

                return;
            }
        }
    }

    public static void save(CustomEntity entity) {
        ConfigurationSection c = file.createSection("CustomEntities.Entity" + entity.getUuid());
        c.set("Class", entity.getClass().getName());
        c.set("UUID", entity.entity.getUniqueId().toString());
        entity.onSave(c);

        try {
            file.save(fileF);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static void removeEntity(CustomEntity entity) {
        if (file.getConfigurationSection("CustomEntities.Entity" + entity.getUuid()) != null) {
            file.set("CustomEntities.Entity" + entity.getUuid(), (Object)null);

            try {
                file.save(fileF);
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static Object getCustomEntity(String clss) {
        try {
            Class c = null;
            if (clss.startsWith("LB_")) {
                String[] d = clss.split("LB_");
                c = Class.forName("com.LuckyBlock.customentities." + d[1]);
            } else {
                c = Class.forName(clss);
            }

            if (CustomEntity.class.isAssignableFrom(c)) {
                return c.newInstance();
            }
        } catch (Exception var3) {
        }

        return null;
    }
}

