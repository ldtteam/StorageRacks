package com.ldtteam.storageracks.tileentities;

import com.ldtteam.storageracks.blocks.ControllerBlock;
import com.ldtteam.storageracks.inv.InsertContainer;
import com.ldtteam.storageracks.utils.BlockPosUtil;
import com.ldtteam.storageracks.utils.InventoryUtils;
import com.ldtteam.storageracks.utils.WorldUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static com.ldtteam.storageracks.utils.NbtTagConstants.*;
import static com.ldtteam.storageracks.utils.NbtTagConstants.TAG_POS;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

/**
 * Class which handles the tileEntity of our colony warehouse.
 */
public class TileEntityController extends BlockEntity implements MenuProvider
{
    /**
     * List of racks.
     */
    public Set<BlockPos> racks = new HashSet<>();

    /**
     * The tier of the controller.
     */
    private int tier;

    /**
     * If sort is unlocked.
     */
    private boolean unlockedSort;

    /**
     * If insert is unlocked
     */
    private boolean unlockedInsert;

    /**
     * Controller inventory type.
     */
    public class ControllerInventory extends ItemStackHandler
    {
        /**
         * Create a new controller inventory.
         */
        public ControllerInventory()
        {
            super(5);
        }

        @Override
        public void setStackInSlot(final int slot, final @Nonnull ItemStack stack)
        {
            if (level.isClientSide)
            {
                return;
            }
            insertItemStack(stack);
        }

        @Override
        public boolean isItemValid(final int slot, @Nonnull final ItemStack stack)
        {
            return getRackForStack(stack) != null;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(final int slot, final int amount, final boolean simulate)
        {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(final int slot, @Nonnull final ItemStack stack, final boolean simulate)
        {
            if (level.isClientSide)
            {
                return ItemStack.EMPTY;
            }

            if (simulate)
            {
                if (getRackForStack(stack) == null)
                {
                    return stack;
                }
                return ItemStack.EMPTY;
            }

            if (!insertItemStack(stack))
            {
                return stack;
            }
            return ItemStack.EMPTY;
        }
    }

    /**
     * Get a rack for a stack.
     * @param stack the stack to insert.
     * @return the matching rack.
     */
    public TileEntityRack getRackForStack(final ItemStack stack)
    {
        TileEntityRack rack = getPositionOfChestWithItemStack(stack);
        if (rack == null)
        {
            rack = getPositionOfChestWithSimilarItemStack(stack);
            if (rack == null)
            {
                rack = searchMostEmptyRack();
            }
        }
        return rack;
    }

    /**
     * Insert a stack into a rack the controller is connected to.
     * @param stack the stack to insert.
     * @return true if successful.
     */
    public boolean insertItemStack(final ItemStack stack)
    {
        final TileEntityRack rack = getRackForStack(stack);
        if (rack == null)
        {
            return false;
        }

        return InventoryUtils.transferItemStackIntoNextBestSlotInItemHandler(stack, rack.getCapability(ITEM_HANDLER_CAPABILITY, null).orElse(new ItemStackHandler(0)));
    }

    /**
     * Create a new controller.
     * @param pos position.
     * @param state initial state.
     */
    public TileEntityController(final BlockPos pos, final BlockState state)
    {
        super(ModTileEntities.CONTROLLER.get(), pos, state);
    }

    @Override
    public void load(final CompoundTag compound)
    {
        super.load(compound);
        racks.clear();
        setTier(((ControllerBlock) getBlockState().getBlock()).getTier());
        final ListTag racksNBT = compound.getList(TAG_INVENTORY, Tag.TAG_COMPOUND);
        for (int i = 0; i < racksNBT.size(); i++)
        {
            final CompoundTag posCompound = racksNBT.getCompound(i);
            racks.add(BlockPosUtil.readFromNBT(posCompound, TAG_POS));
        }
        this.unlockedSort = compound.getBoolean(TAG_SORT);
        this.unlockedInsert = compound.getBoolean(TAG_INSERT);
    }

    @Override
    public void saveAdditional(final @NotNull CompoundTag compound)
    {
        super.saveAdditional(compound);
        @NotNull final ListTag racksNBT = new ListTag();
        for (final BlockPos pos : racks)
        {
            final CompoundTag newCompound = new CompoundTag();
            BlockPosUtil.writeToNBT(newCompound, TAG_POS, pos);
            racksNBT.add(newCompound);
        }
        compound.put(TAG_INVENTORY, racksNBT);
        compound.putBoolean(TAG_SORT, unlockedSort);
        compound.putBoolean(TAG_INSERT, unlockedInsert);
    }

    @Override
    public void setChanged()
    {
        WorldUtil.markChunkDirty(level, getBlockPos());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag()
    {
        final CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket packet)
    {
        this.load(packet.getTag());
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag)
    {
        this.load(tag);
    }

    /**
     * Set the tier of the controller.
     * @param tier the tier.
     */
    public void setTier(final int tier)
    {
        this.tier = tier;
    }

    /**
     * Unlock the sort.
     */
    public void unlockSort()
    {
        this.unlockedSort = true;
        setChanged();
    }

    /**
     * Unlock the insert.
     */
    public void unlockInsert()
    {
        this.unlockedInsert = true;
        setChanged();
    }

    /**
     * Check if sort is unlocked.
     * @return true if so.
     */
    public boolean isSortUnlocked()
    {
        return unlockedSort;
    }

    /**
     * Check if insert is unlocked.
     * @return true if so.
     */
    public boolean isInsertUnlocked()
    {
        return unlockedInsert;
    }

    /**
     * Find the position of the rack with a specific stack.
     * @param stack the stack to look for.
     * @return the pos, or null if not existent.
     */
    @Nullable
    public TileEntityRack getPositionOfChestWithItemStack(@NotNull final ItemStack stack)
    {
        for (final BlockPos pos : racks)
        {
            if (WorldUtil.isBlockLoaded(level, pos))
            {
                final BlockEntity entity = getLevel().getBlockEntity(pos);
                if (entity instanceof AbstractTileEntityRack)
                {
                    if (((AbstractTileEntityRack) entity).getFreeSlots() > 0 && ((AbstractTileEntityRack) entity).hasItemStack(stack, 1))
                    {
                        return (TileEntityRack) entity;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Find the position of the rack with a similar stack.
     * @param stack the stack to look for.
     * @return the pos, or null if not existent.
     */
    @Nullable
    public TileEntityRack getPositionOfChestWithSimilarItemStack(@NotNull final ItemStack stack)
    {
        for (final BlockPos pos : racks)
        {
            if (WorldUtil.isBlockLoaded(level, pos))
            {
                final BlockEntity entity = getLevel().getBlockEntity(pos);
                if (entity instanceof AbstractTileEntityRack)
                {
                    if (((AbstractTileEntityRack) entity).getFreeSlots() > 0 && ((AbstractTileEntityRack) entity).hasSimilarStack(stack))
                    {
                        return (TileEntityRack) entity;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Search for the chest with the least items in it.
     *
     * @return the tileEntity of this chest.
     */
    @Nullable
    private TileEntityRack searchMostEmptyRack()
    {
        int freeSlots = 0;
        TileEntityRack emptiestChest = null;
        for (@NotNull final BlockPos pos : racks)
        {
            final BlockEntity entity = getLevel().getBlockEntity(pos);
            if (entity instanceof TileEntityRack)
            {
                if (((AbstractTileEntityRack) entity).isEmpty())
                {
                    return (TileEntityRack) entity;
                }

                final int tempFreeSlots = ((AbstractTileEntityRack) entity).getFreeSlots();
                if (tempFreeSlots > freeSlots)
                {
                    freeSlots = tempFreeSlots;
                    emptiestChest = (TileEntityRack) entity;
                }
            }
        }

        return emptiestChest;
    }

    /**
     * Add all new rack positions.
     * @param visitedPositions the connected racks.
     */
    public void addAll(final Set<BlockPos> visitedPositions)
    {
        if (visitedPositions.size() > tier * 20)
        {
            return;
        }
        racks.clear();
        racks.addAll(visitedPositions);
        this.setChanged();
    }

    /**
     * Clear the list of racks.
     * @param visitedPositions the list of pos.
     */
    public void removeAll(final Set<BlockPos> visitedPositions)
    {
        racks.removeAll(visitedPositions);
        this.setChanged();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final Direction dir)
    {
        if (!remove && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return LazyOptional.of(() -> (T) new ControllerInventory());
        }
        return super.getCapability(capability, dir);
    }

    @NotNull
    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container.title.insertion");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int id, @NotNull final Inventory inv, @NotNull final Player player)
    {
        return new InsertContainer(id, inv, getCapability(ITEM_HANDLER_CAPABILITY, null).orElse(new ItemStackHandler(0)));
    }
}
