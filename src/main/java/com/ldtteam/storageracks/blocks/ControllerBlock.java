package com.ldtteam.storageracks.blocks;

import com.ldtteam.storageracks.TileEntityController;
import com.ldtteam.storageracks.TileEntityRack;
import com.ldtteam.storageracks.WindowHutAllInventory;
import com.ldtteam.storageracks.utils.Constants;
import com.ldtteam.storageracks.utils.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block for the controller of the racks.
 */
public class ControllerBlock extends UpgradeableBlock
{
    /**
     * This blocks name.
     */
    private static final String BLOCK_NAME = "controller";

    /**
     * The direction the block is facing.
     */
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    /**
     * Smaller shape.
     */
    private static final VoxelShape SHAPE = VoxelShapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    /**
     * The controller tier.
     */
    private final int tier;

    /**
     * Create a new controller.
     * @param material the base material.
     * @param upgradeCost the upgrade cost.
     */
    public ControllerBlock(final String material, final Item upgradeCost, final int tier)
    {
        super(upgradeCost);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        setRegistryName(Constants.MOD_ID.toLowerCase() + ":" + material + "_" + BLOCK_NAME);
        this.tier = tier;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockItemUseContext context)
    {
        @NotNull final Direction facing = (context.getPlayer() == null) ? Direction.NORTH : Direction.fromYRot(context.getPlayer().yRot);
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @NotNull
    @Override
    public BlockState rotate(final BlockState state, final Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public boolean propagatesSkylightDown(final BlockState state, @NotNull final IBlockReader reader, @NotNull final BlockPos pos)
    {
        return true;
    }

    @NotNull
    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public void spawnAfterBreak(final BlockState state, final ServerWorld worldIn, final BlockPos pos, final ItemStack stack)
    {
        final TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TileEntityRack)
        {
            final IItemHandler handler = ((TileEntityRack) tileentity).getInventory();
            InventoryUtils.dropItemHandler(handler, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        super.spawnAfterBreak(state, worldIn, pos, stack);
    }

    /**
     * Choose a different gui when no colony view, for colony overview and creation/deletion
     *
     * @param state   the blockstate.
     * @param world the world.
     * @param pos     the position.
     * @param player  the player.
     * @param hand    the hand.
     * @param ray     the raytraceresult.
     * @return the result type.
     */
    @NotNull
    @Override
    public ActionResultType use(
      final BlockState state,
      final World world,
      final BlockPos pos,
      final PlayerEntity player,
      final Hand hand,
      final BlockRayTraceResult ray)
    {
       /*
        If the world is client, open the gui of the building
         */
        if (world.isClientSide)
        {
            new WindowHutAllInventory((TileEntityController) world.getBlockEntity(pos)).open();
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(final BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world)
    {
        return new TileEntityController();
    }

    @Override
    public void setPlacedBy(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack)
    {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide && state.getBlock() instanceof ControllerBlock && placer instanceof PlayerEntity)
        {
            ((TileEntityController) world.getBlockEntity(pos)).setTier(tier);
            for (final Direction direction : Direction.values())
            {
                final TileEntity te = world.getBlockEntity(pos.relative(direction));
                if (te instanceof TileEntityRack)
                {
                    ((TileEntityRack) te).neighborChange((PlayerEntity) placer);
                    return;
                }
            }
        }
    }

    @Override
    public boolean removedByPlayer(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final boolean willHarvest, final FluidState fluid)
    {
        final boolean rem = super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
        if (!world.isClientSide && state.getBlock() instanceof ControllerBlock)
        {
            for (final Direction direction : Direction.values())
            {
                final TileEntity te = world.getBlockEntity(pos.relative(direction));
                if (te instanceof TileEntityRack)
                {
                    ((TileEntityRack) te).neighborChange(player);
                }
            }
        }
        return rem;
    }

    public int getTier()
    {
        return tier;
    }
}
