package net.msrandom.spooky.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.msrandom.spooky.SpookyMod;

public class FoggyMirrorBlock extends Block {
    public static final RegistryKey<World> SPOOKY = RegistryKey.of(Registry.DIMENSION, new Identifier(SpookyMod.MOD_ID, "spooky"));

    public FoggyMirrorBlock() {
        super(FabricBlockSettings.of(Material.GLASS));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world instanceof ServerWorld && entity instanceof ServerPlayerEntity && !entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals() && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset((double) (-pos.getX()), (double) (-pos.getY()), (double) (-pos.getZ()))), state.getOutlineShape(world, pos), BooleanBiFunction.AND)) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            RegistryKey<World> from = world.getRegistryKey();
            RegistryKey<World> dimension = from == SPOOKY ? World.OVERWORLD : SPOOKY;
            ServerWorld destination = ((ServerWorld) world).getServer().getWorld(dimension);
            if (destination == null) {
                return;
            }

            ServerWorld serverWorld = player.getServerWorld();
            WorldProperties worldProperties = destination.getLevelProperties();
            player.networkHandler.sendPacket(new PlayerRespawnS2CPacket(destination.getDimension(), destination.getRegistryKey(), BiomeAccess.hashSeed(destination.getSeed()), player.interactionManager.getGameMode(), player.interactionManager.getPreviousGameMode(), destination.isDebugWorld(), destination.isFlat(), true));
            player.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
            PlayerManager playerManager = player.server.getPlayerManager();
            playerManager.sendCommandTree(player);
            serverWorld.removePlayer(player);
            player.removed = false;
            serverWorld.getProfiler().push("placing");
            player.setWorld(destination);
            destination.onPlayerChangeDimension(player);
            player.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(1), player.getPitch(1));
            serverWorld.getProfiler().pop();
            Criteria.CHANGED_DIMENSION.trigger(player, from, dimension);
            player.interactionManager.setWorld(destination);
            player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
            playerManager.sendWorldInfo(player, destination);
            playerManager.sendPlayerStatus(player);

            for (StatusEffectInstance statusEffect : player.getStatusEffects()) {
                player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getEntityId(), statusEffect));
            }

            player.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
        }
    }
}
