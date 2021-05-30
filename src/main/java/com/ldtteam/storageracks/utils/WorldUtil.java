package com.ldtteam.storageracks.utils;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.chunk.ChunkStatus;

/**
 * Class which has world related util functions like chunk load checks
 */
public class WorldUtil
{
    /**
     * Checks if the block is loaded for block access
     *
     * @param world world to use
     * @param pos   position to check
     * @return true if block is accessible/loaded
     */
    public static boolean isBlockLoaded(final IWorld world, final BlockPos pos)
    {
        return isChunkLoaded(world, pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Returns whether a chunk is fully loaded
     *
     * @param world world to check on
     * @param x     chunk position
     * @param z     chunk position
     * @return true if loaded
     */
    public static boolean isChunkLoaded(final IWorld world, final int x, final int z)
    {
        return world.getChunk(x, z, ChunkStatus.FULL, false) != null;
    }

    /**
     * Mark a chunk at a position dirty if loaded.
     *
     * @param world the world to mark it dirty in.
     * @param pos   the position within the chunk.
     */
    public static void markChunkDirty(final World world, final BlockPos pos)
    {
        if (WorldUtil.isBlockLoaded(world, pos))
        {
            world.getChunk(pos.getX() >> 4, pos.getZ() >> 4).markUnsaved();
            final BlockState state = world.getBlockState(pos);
            world.sendBlockUpdated(pos, state, state, 3);
        }
    }
}
