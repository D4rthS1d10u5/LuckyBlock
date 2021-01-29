package com.mcgamer199.luckyblock.resources;

import com.mcgamer199.luckyblock.engine.LuckyBlockPlugin;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class MapEndCastle extends MapRenderer {

    public MapEndCastle() {
    }

    public void render(MapView view, MapCanvas canvas, Player player) {
        canvas.drawText(10, 50, MinecraftFont.Font, "You are ");
        canvas.drawText(10, 60, MinecraftFont.Font, "X=" + LuckyBlockPlugin.dungeon_loc[0] + ", Z=" + LuckyBlockPlugin.dungeon_loc[1]);
        canvas.drawText(10, 70, MinecraftFont.Font, "from the dung.");
    }
}
