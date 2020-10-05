package net.msrandom.spooky.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.List;
import java.util.function.Supplier;

public class SpookyBiomeSource extends BiomeSource {
    public static final Codec<SpookyBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.LONG.fieldOf("seed").orElse(0L).stable().forGetter((vanillaLayeredBiomeSource) -> vanillaLayeredBiomeSource.seed),
                    Biome.field_26750.fieldOf("biomes").stable().forGetter(source -> source.biomeList),
                    RegistryLookupCodec.of(Registry.BIOME_KEY).stable().forGetter(source -> source.biomeRegistry))
                    .apply(instance, instance.stable(SpookyBiomeSource::new)));

    private final long seed;
    private final List<Supplier<Biome>> biomeList;
    private final Registry<Biome> biomeRegistry;
    private final BiomeLayerSampler biomeSampler;

    public SpookyBiomeSource(long seed, List<Supplier<Biome>> biomes, Registry<Biome> biomeRegistry) {
        super(biomes.stream());
        this.seed = seed;
        this.biomeList = biomes;
        this.biomeRegistry = biomeRegistry;
        this.biomeSampler = BiomeLayers.build(seed, false, 6, 4);
    }

    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Environment(EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new SpookyBiomeSource(seed, biomeList, biomeRegistry);
    }

    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return biomeSampler.sample(biomeRegistry, biomeX, biomeZ);
    }
}
