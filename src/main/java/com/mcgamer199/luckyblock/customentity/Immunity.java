package com.mcgamer199.luckyblock.customentity;

public enum Immunity {
    FIRE("fire"),
    LAVA("lava"),
    ENTITY_ATTACK("entity_attack"),
    CONTACT("contact"),
    BLOCK_EXPLOSION("block_explosion"),
    CUSTOM("custom"),
    DRAGON_BREATH("dragon_breath"),
    DRAWNING("drawning"),
    ENTITY_EXPLOSION("entity_explosion"),
    FALL("fall"),
    FALLING_BLOCK("falling_block"),
    FIRE_TICK("fire_tick"),
    LIGHTNING("lightning"),
    MAGIC("magic"),
    MELTING("melting"),
    POISON("poison"),
    PROJECTILE("projectile"),
    STARVATION("starvation"),
    SUFFOCATION("suffocation"),
    SUICIDE("suicide"),
    THORNS("thorns"),
    VOID("void"),
    WITHER("wither");

    private final String name;

    Immunity(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
