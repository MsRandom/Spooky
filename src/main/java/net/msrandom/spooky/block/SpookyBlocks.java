package net.msrandom.spooky.block;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.msrandom.spooky.SpookyMod.MOD_ID;

public class SpookyBlocks {
    public static final Block FOGGY_MIRROR = new FoggyMirrorBlock();

    public static void init() {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "foggy_mirror"), FOGGY_MIRROR);
    }
}
