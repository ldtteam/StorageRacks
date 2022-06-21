package com.ldtteam.storageracks.blocks;

import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.tileentities.TileEntityRack;
import com.ldtteam.storageracks.gui.WindowHutAllInventory;
import com.ldtteam.storageracks.utils.Constants;
import com.ldtteam.storageracks.utils.InventoryUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Block for the controller of the racks.
 */
public class ControllerBlock extends UpgradeableBlock
{
    /**
     * The direction the block is facing.
     */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * Smaller shape.
     */
    private static final VoxelShape SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    /**
     * The controller tier.
     */
    private final int tier;

    /**
     * Build material.
     */
    private final Item buildMaterial;

    /**
     * Create a new controller.
     * @param material the base material.
     * @param upgradeCost the upgrade cost.
     */
    public ControllerBlock(final Item material, final Item upgradeCost, final int tier)
    {
        super(upgradeCost);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        this.tier = tier;
        this.buildMaterial = material;
    }

    @Override
    public Block getNext()
    {
        if (tier >= FrameType.values().length)
        {
            return null;
        }
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Constants.MOD_ID,  FrameType.values()[tier + 1].getSerializedName() + "_controller"));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context)
    {
        @NotNull final Direction facing = (context.getPlayer() == null) ? Direction.NORTH : Direction.fromYRot(context.getPlayer().yRotO);
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @NotNull
    @Override
    public BlockState rotate(final BlockState state, final Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public boolean propagatesSkylightDown(final BlockState state, @NotNull final BlockGetter reader, @NotNull final BlockPos pos)
    {
        return true;
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final BlockGetter world, final List<Component> tooltip, final TooltipFlag flag)
    {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.translatable("block.storageracks.controllertoolip", tier*20));
    }

    @NotNull
    @Override
    public VoxelShape getShape(final BlockState state, final BlockGetter worldIn, final BlockPos pos, final CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void spawnAfterBreak(final BlockState state, final ServerLevel worldIn, final BlockPos pos, final ItemStack stack, final boolean check)
    {
        final BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TileEntityRack)
        {
            final IItemHandler handler = ((TileEntityRack) tileentity).getInventory();
            InventoryUtils.dropItemHandler(handler, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        super.spawnAfterBreak(state, worldIn, pos, stack, check);
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
    public InteractionResult use(
      final BlockState state,
      final Level world,
      final BlockPos pos,
      final Player player,
      final InteractionHand hand,
      final BlockHitResult ray)
    {
       /*
        If the world is client, open the gui of the building
         */
        if (world.isClientSide)
        {
            new WindowHutAllInventory((TileEntityController) world.getBlockEntity(pos)).open();
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull final BlockPos blockPos, @NotNull final BlockState blockState)
    {
        return new TileEntityController(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(final Level world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack)
    {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide && state.getBlock() instanceof ControllerBlock && placer instanceof Player)
        {
            ((TileEntityController) world.getBlockEntity(pos)).setTier(tier);
            for (final Direction direction : Direction.values())
            {
                final BlockEntity te = world.getBlockEntity(pos.relative(direction));
                if (te instanceof TileEntityRack)
                {
                    ((TileEntityRack) te).neighborChange();
                    return;
                }
            }
        }
    }

    @Override
    public boolean onDestroyedByPlayer(final BlockState state, final Level world, final BlockPos pos, final Player player, final boolean willHarvest, final FluidState fluid)
    {
        final boolean rem = super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
        if (!world.isClientSide && state.getBlock() instanceof ControllerBlock)
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

    public Item getBuildMaterial()
    {
        return this.buildMaterial;
    }

    public int getTier()
    {
        return tier;
    }
}
