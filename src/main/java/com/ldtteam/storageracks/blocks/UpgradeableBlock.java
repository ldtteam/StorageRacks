package com.ldtteam.storageracks.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UpgradeableBlock extends Block
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

    /**
     * Next block in upgrade order.
     */
    private Block next;

    public UpgradeableBlock(final Item upgradeMaterial)
    {
        super(AbstractBlock.Properties.of(Material.STONE).noOcclusion().strength(BLOCK_HARDNESS, RESISTANCE));
        this.upgradeMaterial = upgradeMaterial;
    }

    public void setNext(final Block next)
    {
        this.next = next;
    }

    /**
     * Check if this is an upgrade attempt.
     *
     * @param pos    the pos of the rack.
     * @param player the player pos.
     */
    public void checkUpgrade(final BlockPos pos, final PlayerEntity player)
    {
        final BlockState state = player.level.getBlockState(pos);
        final UpgradeableBlock block = (RackBlock) state.getBlock();

        if (player.getMainHandItem().getItem() == block.upgradeMaterial)
        {
            player.inventory.removeItem(player.inventory.selected, 1);
            upgrade(pos, state, block.next, player.level);
        }
    }

    private void upgrade(final BlockPos pos, final BlockState current, final Block next, final World world)
    {
        final BlockState newState = next.defaultBlockState();

        final TileEntity te = world.getBlockEntity(pos);
        final CompoundNBT save = te.save(new CompoundNBT());
        te.load(current, new CompoundNBT());

        world.setBlock(pos, newState, 0x03);
        world.getBlockEntity(pos).load(newState, save);
    }
}
