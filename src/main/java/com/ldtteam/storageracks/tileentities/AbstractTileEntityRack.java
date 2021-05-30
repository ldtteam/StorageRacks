package com.ldtteam.storageracks.tileentities;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

import static com.ldtteam.storageracks.utils.Constants.DEFAULT_SIZE;

public abstract class AbstractTileEntityRack extends TileEntity implements INamedContainerProvider
{
    /**
     * Pos of the owning building.
     */
    protected BlockPos controllerPos = BlockPos.ZERO;

    /**
     * The inventory of the tileEntity.
     */
    protected ItemStackHandler inventory;

    public AbstractTileEntityRack(final TileEntityType<?> tileEntityTypeIn)
    {
        super(tileEntityTypeIn);
        inventory = createInventory(DEFAULT_SIZE);
    }

    /**
     * Rack inventory type.
     */
    public class RackInventory extends ItemStackHandler
    {
        public RackInventory(final int defaultSize)
        {
            super(defaultSize);
        }

        @Override
        protected void onContentsChanged(final int slot)
        {
            updateItemStorage();
            super.onContentsChanged(slot);
        }

        @Override
        public void setStackInSlot(final int slot, final @Nonnull ItemStack stack)
        {
            validateSlotIndex(slot);
            final boolean changed = !ItemStack.isSame(stack, this.stacks.get(slot));
            this.stacks.set(slot, stack);
            if (changed)
            {
                onContentsChanged(slot);
            }
        }
    }

    /**
     * Create the inventory that belongs to the rack.
     * @param slots the number of slots.
     * @return the created inventory,
     */
    public abstract ItemStackHandler createInventory(final int slots);

    /**
     * Get the amount of free slots in the inventory. This method checks the content list, it is therefore extremely fast.
     *
     * @return the amount of free slots (an integer).
     */
    public abstract int getFreeSlots();

    /**
     * Check if a similar/same item as the stack is in the inventory. This method checks the content list, it is therefore extremely fast.
     *
     * @param stack             the stack to check.
     * @param count             the min count it should have.
     * @return true if so.
     */
    public abstract boolean hasItemStack(ItemStack stack, final int count);

    /**
     * Check if a similar/same item as the stack is in the inventory. And return the count if so.
     *
     * @param stack             the stack to check.
     * @return the quantity or 0.
     */
    public abstract int getCount(ItemStack stack);

    /**
     * Check if a similar/same item as the stack is in the inventory. This method checks the content list, it is therefore extremely fast.
     *
     * @param itemStackSelectionPredicate the predicate to test the stack against.
     * @return true if so.
     */
    public abstract boolean hasItemStack(@NotNull Predicate<ItemStack> itemStackSelectionPredicate);

    /**
     * Check if a similar stack is in the rack.
     *
     * @param stack stack to check.
     * @return a set of different results depending on the similarity metric.
     */
    public abstract boolean hasSimilarStack(@NotNull ItemStack stack);

    /**
     * Upgrade the rack by 1. This adds 9 more slots and copies the inventory to the new one.
     */
    public abstract void upgradeItemStorage();

    /* Get the amount of items matching a predicate in the inventory.
     * @param predicate the predicate.
     * @return the total count.
     */
    public abstract int getItemCount(Predicate<ItemStack> predicate);

    /**
     * Scans through the whole storage and updates it.
     */
    public abstract void updateItemStorage();

    /**
     * Update the blockState of the rack. Switch between connected, single, full and empty texture.
     */
    protected abstract void updateBlockState();

    /**
     * Checks if the chest is empty. This method checks the content list, it is therefore extremely fast.
     *
     * @return true if so.
     */
    public abstract boolean isEmpty();

    public IItemHandlerModifiable getInventory()
    {
        return inventory;
    }
}
