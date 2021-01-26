package com.mcgamer199.luckyblock.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SoundFile {
    private Sound sound;
    private float pitch = 1.0F;
    private float volume = 1.0F;

    public SoundFile(Sound sound) {
        this.sound = sound;
    }

    public void play(Player player) {
        player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
    }

    public void play(Location loc) {
        World world = loc.getWorld();
        world.playSound(loc, this.sound, this.volume, this.pitch);
    }

    public String getString() {
        return this.sound.toString();
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getVolume() {
        return this.volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
