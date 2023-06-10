package com.ldtteam.storageracks.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * Block for the shelves of the warehouse.
 */
public class CornerBlock extends Block
{
    /**
     * The hardness this block has.
     */
    private static final float BLOCK_HARDNESS = 5.0F;

    /**
     * The resistance this block has.
     */
    private static final float RESISTANCE = Float.POSITIVE_INFINITY;

    /**
     * Smaller shape.
     */
    private static final VoxelShape SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    /**
     * The two types.
     */
    public final  FrameType frameType;
    private final WoodType  woodType;

    public CornerBlock(final WoodType wood, final FrameType frame)
    {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion().strength(BLOCK_HARDNESS, RESISTANCE));
        this.woodType = wood;
        this.frameType = frame;
    }

    @Override
    public boolean propagatesSkylightDown(final BlockState state, @NotNull final BlockGetter reader, @NotNull final BlockPos pos)
    {
        return true;
    }

    @NotNull
    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            worldIn.updateNeighbourForOutputSignal(pos, this);
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    /**
     * Get the associated wood, used for data gen
     *
     * @return the wood type
     */
    public WoodType getWoodType()
    {
        return this.woodType;
    }

    /**
     * Get the registered TimberFrameCentreType, used by the Data Generators
     *
     * @return the registered TimberFrameCentreType
     */
    public FrameType getFrameType()
    {
        return this.frameType;
    }

    @Override
    public void onPlace(final BlockState state, final Level world, final BlockPos pos, final BlockState newState, final boolean bool)
    {
        super.onPlace(state, world, pos, newState, bool);
        if (!world.isClientSide && state.getBlock() instanceof CornerBlock)
        {
            world.updateNeighbourForOutputSignal(pos, this);
        }
    }
}
