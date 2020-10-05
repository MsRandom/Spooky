package net.msrandom.spooky;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.msrandom.spooky.block.SpookyBlocks;
import net.msrandom.spooky.world.biome.source.SpookyBiomeSource;

public class SpookyMod implements ModInitializer {
    public static final String MOD_ID = "spooky";

	@Override
	public void onInitialize() {
        SpookyBlocks.init();
        Registry.register(Registry.BIOME_SOURCE, new Identifier(MOD_ID, "spooky"), SpookyBiomeSource.CODEC);
	}
}
