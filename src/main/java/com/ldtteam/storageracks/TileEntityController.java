package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.ControllerBlock;
import com.ldtteam.storageracks.utils.InventoryUtils;
import com.ldtteam.storageracks.utils.ItemStackUtils;
import com.ldtteam.storageracks.utils.WorldUtil;
import com.ldtteam.structurize.api.util.BlockPosUtil;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.ldtteam.storageracks.utils.NbtTagConstants.*;
import static com.ldtteam.storageracks.utils.NbtTagConstants.TAG_POS;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

/**
 * Class which handles the tileEntity of our colony warehouse.
 */
public class TileEntityController extends TileEntity
{
    /**
     * List of racks.
     */
    public Set<BlockPos> racks = new HashSet<>();

    /**
     * The tier of the controller.
     */
    private int tier;

    public TileEntityController()
    {
        super(ModTileEntities.CONTROLLER);
    }

    public boolean hasMatchingItemStackInWarehouse(@NotNull final Predicate<ItemStack> itemStackSelectionPredicate, int count)
    {
        final List<Tuple<ItemStack, BlockPos>> targetStacks = getMatchingItemStacksInWarehouse(itemStackSelectionPredicate);
        return targetStacks.stream().mapToInt(tuple -> ItemStackUtils.getSize(tuple.getA())).sum() >= count;
    }

    public boolean hasMatchingItemStackInWarehouse(@NotNull final ItemStack itemStack, final int count, final boolean ignoreNBT)
    {
        int totalCountFound = 0;
        for (@NotNull final BlockPos pos : racks)
        {
            if (WorldUtil.isBlockLoaded(level, pos))
            {
                final TileEntity entity = getLevel().getBlockEntity(pos);
                if (entity instanceof TileEntityRack && !((AbstractTileEntityRack) entity).isEmpty())
                {
                    totalCountFound += ((AbstractTileEntityRack) entity).getCount(itemStack);
                    if (totalCountFound >= count)
                    {
                        return true;
                    }
                }

                if (entity instanceof ChestTileEntity)
                {
                    totalCountFound += InventoryUtils.getItemCountInItemHandler(entity.getCapability(ITEM_HANDLER_CAPABILITY, null).orElseGet(null),
                      item -> item.sameItemStackIgnoreDurability(itemStack) && item.getCount() >= itemStack.getCount());
                    if (totalCountFound >= count)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    public List<Tuple<ItemStack, BlockPos>> getMatchingItemStacksInWarehouse(@NotNull final Predicate<ItemStack> itemStackSelectionPredicate)
    {
        List<Tuple<ItemStack, BlockPos>> found = new ArrayList<>();
        for (@NotNull final BlockPos pos : racks)
        {
            final TileEntity entity = getLevel().getBlockEntity(pos);
            if (entity instanceof TileEntityRack && !((AbstractTileEntityRack) entity).isEmpty() && ((AbstractTileEntityRack) entity).getItemCount(itemStackSelectionPredicate) > 0)
            {
                final TileEntityRack rack = (TileEntityRack) entity;
                for (final ItemStack stack : (InventoryUtils.filterItemHandler(rack.getInventory(), itemStackSelectionPredicate)))
                {
                    found.add(new Tuple<>(stack, pos));
                }
            }

            if (entity instanceof ChestTileEntity && InventoryUtils.hasItemInItemHandler(entity.getCapability(ITEM_HANDLER_CAPABILITY, null).orElseGet(null), itemStackSelectionPredicate))
            {
                for (final ItemStack stack : InventoryUtils.filterItemHandler(entity.getCapability(ITEM_HANDLER_CAPABILITY, null).orElseGet(null), itemStackSelectionPredicate))
                {
                    found.add(new Tuple<>(stack, pos));
                }
            }
        }

        return found;
    }

    /**
     * Check for a certain item and return the position of the chest containing it.
     *
     * @param itemStackSelectionPredicate the stack to search for.
     * @return the position or null.
     */
    @Nullable
    public BlockPos getPositionOfChestWithItemStack(@NotNull final Predicate<ItemStack> itemStackSelectionPredicate)
    {
        final Predicate<ItemStack> notEmptyPredicate = itemStackSelectionPredicate.and(ItemStackUtils.NOT_EMPTY_PREDICATE);
        for (final BlockPos pos : racks)
        {
            if (WorldUtil.isBlockLoaded(level, pos))
            {
                final TileEntity entity = getLevel().getBlockEntity(pos);
                if (entity instanceof AbstractTileEntityRack)
                {
                    if (((AbstractTileEntityRack) entity).hasItemStack(notEmptyPredicate))
                    {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Search the right chest for an itemStack.
     *
     * @param stack the stack to dump.
     * @return the tile entity of the chest
     */
    @Nullable
    private TileEntity searchRightChestForStack(@NotNull final ItemStack stack)
    {
        for (@NotNull final BlockPos pos : racks)
        {
            final TileEntity entity = getLevel().getBlockEntity(pos);
            if (isInRack(stack, entity, false) || isInChest(stack, entity, false))
            {
                return entity;
            }
        }

        @Nullable final TileEntity chest = searchChestWithSimilarItem(stack);
        return chest == null ? searchMostEmptySlot() : chest;
    }

    /**
     * Check if a similar item is in the rack.
     *
     * @param stack                 the stack to check.
     * @param entity                the entity.
     * @param includeSimilarMatches if similar matches should be included or only exact matches.
     * @return true if so.
     */
    private static boolean isInRack(final ItemStack stack, final TileEntity entity, final boolean includeSimilarMatches)
    {
        return entity instanceof TileEntityRack
                 && !((AbstractTileEntityRack) entity).isEmpty()
                 && (includeSimilarMatches ? ((TileEntityRack) entity).hasSimilarStack(stack) : ((AbstractTileEntityRack) entity).hasItemStack(stack, 1))
                 && ((TileEntityRack) entity).getFreeSlots() > 0;
    }

    /**
     * Check if a similar item is in the chest.
     *
     * @param stack             the stack to check.
     * @param entity            the entity.
     * @param ignoreDamageValue should the damage value be ignored.
     * @return true if so.
     */
    private static boolean isInChest(final ItemStack stack, final TileEntity entity, final boolean ignoreDamageValue)
    {
        return entity instanceof ChestTileEntity
                 && InventoryUtils.findSlotInItemHandlerNotFullWithItem(entity.getCapability(ITEM_HANDLER_CAPABILITY, null).orElseGet(null), stack);
    }

    /**
     * Searches a chest with a similar item as the incoming stack.
     *
     * @param stack the stack.
     * @return the entity of the chest.
     */
    @Nullable
    private TileEntity searchChestWithSimilarItem(final ItemStack stack)
    {
        for (@NotNull final BlockPos pos : racks)
        {
            final TileEntity entity = getLevel().getBlockEntity(pos);
            if (isInRack(stack, entity, true) || isInChest(stack, entity, true))
            {
                return entity;
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
    private TileEntity searchMostEmptySlot()
    {
        int freeSlots = 0;
        TileEntity emptiestChest = null;
        for (@NotNull final BlockPos pos : racks)
        {
            final TileEntity entity = getLevel().getBlockEntity(pos);
            if (entity == null)
            {
                racks.remove(pos);
                continue;
            }
            final int tempFreeSlots;
            if (entity instanceof TileEntityRack)
            {
                if (((AbstractTileEntityRack) entity).isEmpty())
                {
                    return entity;
                }

                tempFreeSlots = ((AbstractTileEntityRack) entity).getFreeSlots();
                if (freeSlots < tempFreeSlots)
                {
                    freeSlots = tempFreeSlots;
                    emptiestChest = entity;
                }
            }
        }

        return emptiestChest;
    }

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

    public void removeAll(final Set<BlockPos> visitedPositions)
    {
        racks.removeAll(visitedPositions);
        this.setChanged();
    }

    @Override
    public void load(final BlockState state, final CompoundNBT compound)
    {
        super.load(state, compound);
        racks.clear();
        setTier(((ControllerBlock) state.getBlock()).getTier());
        final ListNBT racksNBT = compound.getList(TAG_INVENTORY, TAG_COMPOUND);
        for (int i = 0; i < racksNBT.size(); i++)
        {
            final CompoundNBT posCompound = racksNBT.getCompound(i);
            racks.add(BlockPosUtil.readFromNBT(posCompound, TAG_POS));
        }
    }

    @NotNull
    @Override
    public CompoundNBT save(final CompoundNBT compound)
    {
        super.save(compound);
        @NotNull final ListNBT racksNBT = new ListNBT();
        for (final BlockPos pos : racks)
        {
            final CompoundNBT newCompound = new CompoundNBT();
            BlockPosUtil.writeToNBT(newCompound, TAG_POS, pos);
            racksNBT.add(newCompound);
        }
        compound.put(TAG_INVENTORY, racksNBT);
        return compound;
    }

    @Override
    public void setChanged()
    {
        WorldUtil.markChunkDirty(level, getBlockPos());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        final CompoundNBT compound = new CompoundNBT();
        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, this.save(compound));
    }

    @NotNull
    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket packet)
    {
        this.load(getBlockState(), packet.getTag());
    }

    @Override
    public void handleUpdateTag(final BlockState state, final CompoundNBT tag)
    {
        this.load(state, tag);
    }

    public void setTier(final int tier)
    {
        this.tier = tier;
    }
}
