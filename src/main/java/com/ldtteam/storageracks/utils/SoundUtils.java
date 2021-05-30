package com.ldtteam.storageracks.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
    public static void playSuccessSound(@NotNull final PlayerEntity player, @NotNull final BlockPos position)
    {
        if (player instanceof ServerPlayerEntity)
        {
            ((ServerPlayerEntity) player).connection.send(new SPlaySoundEffectPacket(SoundEvents.NOTE_BLOCK_BELL,
              SoundCategory.NEUTRAL,
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
    public static void playErrorSound(@NotNull final PlayerEntity player, @NotNull final BlockPos position)
    {
        if (player instanceof ServerPlayerEntity)
        {
            ((ServerPlayerEntity) player).connection.send(new SPlaySoundEffectPacket(SoundEvents.NOTE_BLOCK_DIDGERIDOO,
              SoundCategory.NEUTRAL,
              position.getX(),
              position.getY(),
              position.getZ(),
              (float) VOLUME * 2,
              (float) 0.3));
        }
    }
}
