package com.ldtteam.storageracks.blocks;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;

public abstract class UpgradeableBlock extends Block implements EntityBlock
{
    /**
     * The hardness this block has.
     */
    private static final float BLOCK_HARDNESS = 10.0F;

    /**
     * The resistance this block has.
     */
    private static final float RESISTANCE = Float.POSITIVE_INFINITY;

    /**
     * Necessary material for upgrade.
     */
    private final Item upgradeMaterial;

    public UpgradeableBlock(final Item upgradeMaterial)
    {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).noOcclusion().strength(BLOCK_HARDNESS, RESISTANCE));
        this.upgradeMaterial = upgradeMaterial;
    }

    public Item getUpgradeMaterial()
    {
        return upgradeMaterial;
    }

    /**
     * Calculates the next block in upgrade line.
     * @return the next block in line.
     */
    public abstract Block getNext();

    /**
     * Check if this is an upgrade attempt.
     *
     * @param pos    the pos of the rack.
     * @param player the player pos.
     */
    public void checkUpgrade(final BlockPos pos, final Player player)
    {
        final BlockState state = player.level().getBlockState(pos);
        final UpgradeableBlock block = (UpgradeableBlock) state.getBlock();

        if (player.getMainHandItem().getItem() == block.upgradeMaterial)
        {
            player.getInventory().removeItem(player.getInventory().selected, 1);
            upgrade(pos, state, getNext(), player.level());
        }
    }

    private void upgrade(final BlockPos pos, final BlockState current, final Block next, final Level world)
    {
        final BlockState newState = next.defaultBlockState();

        final BlockEntity te = world.getBlockEntity(pos);
        final CompoundTag save = te.saveWithFullMetadata();
        te.load(new CompoundTag());

        world.setBlock(pos, newState, 0x03);
        world.getBlockEntity(pos).load(save);
    }
}
