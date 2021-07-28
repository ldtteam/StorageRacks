package com.ldtteam.storageracks.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for playing sounds.
 */
public final class SoundUtils
{
    /**
     * Volume to play at.
     */
    public static final double VOLUME = 0.5D;

    /**
     * Private constructor to hide the implicit public one.
     */
    private SoundUtils()
    {
        /*
         * Intentionally left empty.
         */
    }

    /**
     * Play a success sound.
     * @param player the player to play it for.
     * @param position the position it is played at.
     */
    public static void playSuccessSound(@NotNull final Player player, @NotNull final BlockPos position)
    {
        if (player instanceof ServerPlayer)
        {
            ((ServerPlayer) player).connection.send(new ClientboundSoundPacket(SoundEvents.NOTE_BLOCK_BELL,
              SoundSource.NEUTRAL,
              position.getX(),
              position.getY(),
              position.getZ(),
              (float) VOLUME * 2,
              (float) 1.0));
        }
    }

    /**
     * Play an error sound.
     * @param player the player to play it for.
     * @param position the position it is played at.
     */
    public static void playErrorSound(@NotNull final Player player, @NotNull final BlockPos position)
    {
        if (player instanceof ServerPlayer)
        {
            ((ServerPlayer) player).connection.send(new ClientboundSoundPacket(SoundEvents.NOTE_BLOCK_DIDGERIDOO,
              SoundSource.NEUTRAL,
              position.getX(),
              position.getY(),
              position.getZ(),
              (float) VOLUME * 2,
              (float) 0.3));
        }
    }
}
