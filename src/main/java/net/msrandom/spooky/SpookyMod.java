package net.msrandom.spooky;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.msrandom.spooky.block.FoggyMirrorBlock;

public class SpookyMod implements ModInitializer {
    public static final String MOD_ID = "spooky";

	@Override
	public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "foggy_mirror"), new FoggyMirrorBlock(FabricBlockSettings.of(Material.GLASS)));
	}
}
