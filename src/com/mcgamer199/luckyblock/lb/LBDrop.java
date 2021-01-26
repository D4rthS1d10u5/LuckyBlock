package com.mcgamer199.luckyblock.lb;

import com.mcgamer199.luckyblock.event.lb.block.HTasks;
import org.bukkit.entity.Player;

import java.util.List;

public enum LBDrop {
    NONE(false),
    CHEST(true),
    FALLING_BLOCK(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Height", new Double[]{10.0D})}),
    ENTITY(true),
    LAVA(true),
    VILLAGER(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Seconds", new Integer[]{4})}),
    SPLASH_POTION(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Effects", new String[]{"SPEED%200%0"})}),
    PRIMED_TNT(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("TntPower", new Float[]{3.0F}), new com.mcgamer199.luckyblock.lb.DropOption("Fuse", new Integer[]{50})}),
    LIGHTNING(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{10})}),
    FAKE_ITEM(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("ItemMaterial", new String[]{"DIAMOND"}), new com.mcgamer199.luckyblock.lb.DropOption("ItemData", new Short[]{Short.valueOf((short)0)}), new com.mcgamer199.luckyblock.lb.DropOption("ItemAmount", new Integer[]{64}), new com.mcgamer199.luckyblock.lb.DropOption("Ticks", new Integer[]{85})}),
    FIREWORK(true),
    DROPPED_ITEMS(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Effects", new Boolean[]{false}), new com.mcgamer199.luckyblock.lb.DropOption("ShowItemName", new Boolean[]{false})}),
    STUCK(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Duration", new Integer[]{15})}),
    DAMAGE(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Value", new Integer[]{5})}),
    TOWER(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Type", new String[]{"a"})}),
    F_PIGS(true),
    SLIMES(true),
    METEORS(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("ExplosionPower", new Float[]{11.0F})}),
    F_LB(true),
    SOLDIER(true),
    LB_ITEM(true),
    ELEMENTAL_CREEPER(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("BlockMaterial", new String[]{"DIRT"}), new com.mcgamer199.luckyblock.lb.DropOption("BlockData", new Byte[]{0})}),
    BEDROCK(true),
    JAIL(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Ticks", new Integer[]{70})}),
    TREE(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("TreeType", new String[]{"TREE"})}),
    SINK_IN_GROUND(true),
    WOLVES_OCELOTS(true),
    FIREWORKS(true),
    FEED(true),
    HEAL(true),
    SIGN(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Texts", new String[]{"&cHello", "&5How are you?"}), new com.mcgamer199.luckyblock.lb.DropOption("Facing", new String[]{"PLAYER"})}),
    REPAIR(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("RepairType", new String[]{"all"})}),
    ENCHANT(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Enchants", new String[]{"DURABILITY", "DAMAGE_ALL"}), new com.mcgamer199.luckyblock.lb.DropOption("Levels", new Integer[]{2, 1})}),
    ADD_ITEM(true),
    XP(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("XPAmount", new Integer[]{75})}),
    POISON_ENTITIES(true),
    CUSTOM_STRUCTURE(true),
    RUN_COMMAND(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Command", new String[]{"/say hello!"})}),
    CLEAR_EFFECTS(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Effects", new String[]{"SLOW", "SLOW_DIGGING", "HARM", "CONFUSION", "BLINDNESS", "HUNGER", "WEAKNESS", "POISON", "WITHER"})}),
    TELEPORT(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Height", new Integer[]{20})}),
    PERFORM_ACTION(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("ObjType", new String[0]), new com.mcgamer199.luckyblock.lb.DropOption("ActionName", new String[0]), new com.mcgamer199.luckyblock.lb.DropOption("ActionValue", new Object[0])}),
    RANDOM_ITEM(true),
    TNT_RAIN(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{20}), new com.mcgamer199.luckyblock.lb.DropOption("Fuse", new Integer[]{60})}),
    ZOMBIE_TRAP(true),
    ITEM_RAIN(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{20}), new com.mcgamer199.luckyblock.lb.DropOption("ItemMaterials", new String[]{"EMERALD", "DIAMOND", "IRON_INGOT", "GOLD_INGOT", "GOLD_NUGGET"})}),
    BLOCK_RAIN(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{20}), new com.mcgamer199.luckyblock.lb.DropOption("BlockMaterials", new String[]{"EMERALD_BLOCK", "GOLD_BLOCK", "LAPIS_BLOCK", "DIAMOND_BLOCK", "IRON_BLOCK"})}),
    TRADES(true),
    ARROW_RAIN(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{20}), new com.mcgamer199.luckyblock.lb.DropOption("Critical", new Boolean[]{true}), new com.mcgamer199.luckyblock.lb.DropOption("Bounce", new Boolean[]{true})}),
    ROCKET(true),
    TALKING_ZOMBIE(true),
    SET_NEARBY_BLOCKS(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("BlockMaterial", new String[]{"DIAMOND_BLOCK"}), new com.mcgamer199.luckyblock.lb.DropOption("BlockData", new Byte[]{0}), new com.mcgamer199.luckyblock.lb.DropOption("Range", new Integer[]{10}), new com.mcgamer199.luckyblock.lb.DropOption("Delay", new Integer[]{8}), new com.mcgamer199.luckyblock.lb.DropOption("Mode", new String[]{"SURFACE"})}),
    SOUND(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Listener", new String[]{"PLAYER"}), new com.mcgamer199.luckyblock.lb.DropOption("SoundName", new String[]{"UI_BUTTON_CLICK"})}),
    XP_RAIN(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{32})}),
    SET_BLOCK(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("BlockMaterial", new String[]{"DIAMOND_BLOCK"})}),
    FALLING_ANVILS(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Height", new Integer[]{20}), new com.mcgamer199.luckyblock.lb.DropOption("AnvilData", new Byte[]{8}), new com.mcgamer199.luckyblock.lb.DropOption("LocationType", new String[]{"PLAYER"})}),
    DISPENSER(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{64})}),
    POTION_EFFECT(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Effects", new String[]{"SPEED%200%0"})}),
    DAMAGE_1(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{30}), new com.mcgamer199.luckyblock.lb.DropOption("Ticks", new Integer[]{11})}),
    FIRE(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Range", new Integer[]{6})}),
    EXPLOSION(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("ExplosionPower", new Float[]{4.0F})}),
    BOB(true),
    PETER(true),
    KILL(true),
    LAVA_HOLE(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Radius", new Byte[]{2}), new com.mcgamer199.luckyblock.lb.DropOption("WithWebs", new Boolean[]{true}), new com.mcgamer199.luckyblock.lb.DropOption("BordersMaterial", new String[]{"STONE"}), new com.mcgamer199.luckyblock.lb.DropOption("BordersData", new Byte[]{0}), new com.mcgamer199.luckyblock.lb.DropOption("Texts", new String[]{"Say goodbye :)"})}),
    VOID_HOLE(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Radius", new Byte[]{2}), new com.mcgamer199.luckyblock.lb.DropOption("BordersMaterial", new String[]{"AIR"}), new com.mcgamer199.luckyblock.lb.DropOption("BordersData", new Byte[]{0})}),
    WATER_TRAP(true),
    LB_STRUCTURE(true),
    LAVA_POOL(true),
    DROPPER(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{64})}),
    EXPLOSIVE_CHEST(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Ticks", new Integer[]{50}), new com.mcgamer199.luckyblock.lb.DropOption("ClearInventory", new Boolean[]{true})}),
    ADD_LEVEL(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Amount", new Integer[]{10})}),
    ADD_XP(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Amount", new Integer[]{550})}),
    LUCKY_WELL(true),
    LB_RAIN(true, new LBDrop.LBFunction() {
        public void function(LB lb, Player player) {
            HTasks.m(lb.getBlock().getLocation(), 10, lb.getType());
        }
    }, new com.mcgamer199.luckyblock.lb.DropOption[0]),
    KARL(true),
    HELL_HOUND(true),
    METEORS_1(true),
    ILLUMINATI(false),
    SCHEMATIC_STRUCTURE(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("LocationType", new String[]{"PLAYER"}), new com.mcgamer199.luckyblock.lb.DropOption("Loc", new Integer[]{0, 0, 0})}),
    TNT_IN_THE_MIDDLE(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("BlocksMaterial", new String[]{"DIAMOND_BLOCK"}), new com.mcgamer199.luckyblock.lb.DropOption("BlocksData", new Byte[]{0}), new com.mcgamer199.luckyblock.lb.DropOption("Fuse", new Integer[]{65}), new com.mcgamer199.luckyblock.lb.DropOption("ExplosionPower", new Float[]{5.0F})}),
    FLYING_TNTS(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Times", new Integer[]{8}), new com.mcgamer199.luckyblock.lb.DropOption("Fuse", new Integer[]{80})}),
    ANVIL_JAIL(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Height", new Double[]{35.0D})}),
    LAVA_JAIL(true, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Ticks", new Integer[]{55})}),
    ANVIL_RAIN(false, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("Duration", new Integer[]{200}), new com.mcgamer199.luckyblock.lb.DropOption("BorderMaterial", new String[]{"OBSIDIAN"}), new com.mcgamer199.luckyblock.lb.DropOption("BorderData", new Byte[]{0})}),
    RIP(false, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("ClearInventory", new Boolean[]{true})}),
    DONT_MINE(false, new com.mcgamer199.luckyblock.lb.DropOption[]{new com.mcgamer199.luckyblock.lb.DropOption("BlocksMaterial", new String[]{"DIAMOND_ORE"}), new com.mcgamer199.luckyblock.lb.DropOption("BlocksData", new Byte[]{0}), new com.mcgamer199.luckyblock.lb.DropOption("ExplosionPower", new Float[]{5.5F})}),
    EQUIP_ITEM(false);

    private com.mcgamer199.luckyblock.lb.DropOption[] defaultOptions = new com.mcgamer199.luckyblock.lb.DropOption[64];
    private boolean visible;
    private LBDrop.LBFunction function;

    private LBDrop(boolean visible, LBDrop.LBFunction function, com.mcgamer199.luckyblock.lb.DropOption... defaultOptions) {
        this.defaultOptions = defaultOptions;
        this.visible = visible;
        this.function = function;
    }

    private LBDrop(boolean visible, com.mcgamer199.luckyblock.lb.DropOption... defaultOptions) {
        this.defaultOptions = defaultOptions;
        this.visible = visible;
    }

    private LBDrop(boolean visible) {
        this.visible = visible;
    }

    private LBDrop(int id, List<com.mcgamer199.luckyblock.lb.DropOption> defaultOptions) {
        for(int x = 0; x < defaultOptions.size(); ++x) {
            this.defaultOptions[x] = (com.mcgamer199.luckyblock.lb.DropOption)defaultOptions.get(x);
        }

    }

    public DropOption[] getDefaultOptions() {
        return this.defaultOptions;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public LBDrop.LBFunction getFunction() {
        return this.function;
    }

    public static boolean isValid(String name) {
        LBDrop[] var4;
        int var3 = (var4 = values()).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            LBDrop drop = var4[var2];
            if (drop.name().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public static LBDrop getByName(String name) {
        LBDrop[] var4;
        int var3 = (var4 = values()).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            LBDrop drop = var4[var2];
            if (drop.name().equalsIgnoreCase(name)) {
                return drop;
            }
        }

        return null;
    }

    public interface LBFunction {
        void function(LB var1, Player var2);
    }
}
