package com.ldtteam.storageracks.blocks;

import com.ldtteam.storageracks.tileentities.TileEntityRack;
import com.ldtteam.storageracks.utils.Constants;
import com.ldtteam.storageracks.utils.InventoryUtils;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

/**
 * Block for the shelves of the warehouse.
 */
public class RackBlock extends UpgradeableBlock
{
    /**
     * Variant of rack (full, empty).
     */
    public static final EnumProperty<RackType> VARIANT = EnumProperty.create("variant", RackType.class);

    /**
     * This blocks name.
     */
    private static final String BLOCK_NAME = "rack";

    /**
     * Smaller shape.
     */
    private static final VoxelShape SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    /**
     * The two types.
     */
    private final FrameType frameType;
    private final WoodType woodType;

    public RackBlock(final WoodType wood, final FrameType frame, final Item upgradeMaterial)
    {
        super(upgradeMaterial);
        this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, RackType.DEFAULT));
        this.woodType = wood;
        this.frameType = frame;

        setRegistryName(Constants.MOD_ID.toLowerCase() + ":" + wood.getSerializedName() + "_" + frame.getSerializedName() + "_" + BLOCK_NAME);
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
    public void spawnAfterBreak(final BlockState state, final ServerLevel worldIn, final BlockPos pos, final ItemStack stack)
    {
        final BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TileEntityRack)
        {
            final IItemHandler handler = ((TileEntityRack) tileentity).getInventory();
            InventoryUtils.dropItemHandler(handler, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        super.spawnAfterBreak(state, worldIn, pos, stack);
    }

    @NotNull
    @Override
    public InteractionResult use(
      final BlockState state,
      final Level world,
      final BlockPos pos,
      final Player player,
      final InteractionHand hand,
      final BlockHitResult ray)
    {
        final BlockEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof TileEntityRack)
        {
            final TileEntityRack rack = (TileEntityRack) tileEntity;
            if (!world.isClientSide)
            {
                rack.checkForUpgrade(state, rack.getSize());

                NetworkHooks.openGui((ServerPlayer) player,
                  rack,
                  buf -> buf.writeBlockPos(rack.getBlockPos()));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(VARIANT);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull final BlockPos blockPos, @NotNull final BlockState blockState)
    {
        return new TileEntityRack(blockPos, blockState);
    }

    @Override
    public BlockState rotate(final BlockState state, final LevelAccessor world, final BlockPos pos, final Rotation direction)
    {
        ((TileEntityRack) world.getBlockEntity(pos)).rotate(direction);
        return super.rotate(state, world, pos, direction);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof TileEntityRack)
            {
                TileEntityRack tileEntityRack = (TileEntityRack) tileEntity;
                InventoryUtils.dropItemHandler(tileEntityRack.getInventory(),
                  worldIn,
                  tileEntityRack.getBlockPos().getX(),
                  tileEntityRack.getBlockPos().getY(),
                  tileEntityRack.getBlockPos().getZ());
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }

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
        if (!world.isClientSide && state.getBlock() instanceof RackBlock)
        {
            ((TileEntityRack) world.getBlockEntity(pos)).neighborChange();
        }
    }

    @Override
    public boolean onDestroyedByPlayer(final BlockState state, final Level world, final BlockPos pos, final Player player, final boolean willHarvest, final FluidState fluid)
    {
        final boolean rem = super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
        if (!world.isClientSide && state.getBlock() instanceof RackBlock)
        {
            for (final Direction direction : Direction.values())
            {
                final BlockEntity te = world.getBlockEntity(pos.relative(direction));
                if (te instanceof TileEntityRack)
                {
                    ((TileEntityRack) te).neighborChange();
                }
            }
        }
        return rem;
    }
}
