package com.ldtteam.storageracks.utils;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * Utility methods for the inventories.
 */
public class InventoryUtils
{
    /**
     * Several values to spawn items in the world.
     */
    private static final double SPAWN_MODIFIER    = 0.8D;
    private static final double SPAWN_ADDITION    = 0.1D;
    private static final int    MAX_RANDOM_SPAWN  = 21;
    private static final int    MIN_RANDOM_SPAWN  = 10;
    private static final double MOTION_MULTIPLIER = 0.05000000074505806D;
    private static final double MOTION_Y_MIN      = 0.20000000298023224D;

    /**
     * Private constructor to hide the implicit one.
     */
    private InventoryUtils()
    {
        /*
         * Intentionally left empty.
         */
    }

    /**
     * Compares whether or not the item in an itemstack is equal to a given item.
     *
     * @param itemStack  ItemStack to check.
     * @param targetItem Item to check.
     * @return True when item in item stack is equal to target item.
     */
    private static boolean compareItems(@Nullable final ItemStack itemStack, final Item targetItem)
    {
        return !ItemStackUtils.isEmpty(itemStack) && itemStack.getItem() == targetItem;
    }

    /**
     * Returns the index of the first occurrence of the Item with the given ItemDamage in the {@link IItemHandler}.
     *
     * @param itemHandler {@link IItemHandler} to check
     * @param targetItem  Item to find.
     * @return Index of the first occurrence
     */
    public static int findFirstSlotInItemHandlerWith(@NotNull final IItemHandler itemHandler, @NotNull final Item targetItem)
    {
        return findFirstSlotInItemHandlerWith(itemHandler, (ItemStack stack) -> compareItems(stack, targetItem));
    }

    /**
     * Returns the index of the first occurrence of an ItemStack that matches the given predicate in the {@link IItemHandler}.
     *
     * @param itemHandler                 ItemHandler to check
     * @param itemStackSelectionPredicate The predicate to match.
     * @return Index of the first occurrence
     */
    public static int findFirstSlotInItemHandlerWith(@NotNull final IItemHandler itemHandler, @NotNull final Predicate<ItemStack> itemStackSelectionPredicate)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            if (itemStackSelectionPredicate.test(itemHandler.getStackInSlot(slot)))
            {
                return slot;
            }
        }

        return -1;
    }

    /**
     * Method to get all the IItemHandlers from a given Provider.
     *
     * @param provider The provider to get the IItemHandlers from.
     * @return A list with all the unique IItemHandlers a provider has.
     */
    @NotNull
    public static Set<IItemHandler> getItemHandlersFromProvider(@NotNull final ICapabilityProvider provider)
    {
        final Set<IItemHandler> handlerList = new HashSet<>();
        for (final Direction side : Direction.values())
        {
            provider.getCapability(ForgeCapabilities.ITEM_HANDLER, side).ifPresent(handlerList::add);
        }
        provider.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(handlerList::add);
        return handlerList;
    }

    /**
     * Method to put a given Itemstack in a given target {@link IItemHandler}. Trying to merge existing itemStacks if possible.
     *
     * @param stack         the itemStack to transfer.
     * @param targetHandler The {@link IItemHandler} that works as Target.
     * @return True when the swap was successful, false when not.
     */
    public static boolean transferItemStackIntoNextBestSlotInItemHandler(final ItemStack stack, @NotNull final IItemHandler targetHandler)
    {
        return transferItemStackIntoNextBestSlotInItemHandlerWithResult(stack, targetHandler).isEmpty();
    }

    /**
     * Method to put a given Itemstack in a given target {@link IItemHandler}. Trying to merge existing itemStacks if possible.
     *
     * @param stack         the itemStack to transfer.
     * @param targetHandler The {@link IItemHandler} that works as Target.
     * @return the rest of the stack.
     */
    public static ItemStack transferItemStackIntoNextBestSlotInItemHandlerWithResult(final ItemStack stack, @NotNull final IItemHandler targetHandler)
    {
        ItemStack sourceStack = stack.copy();

        if (ItemStackUtils.isEmpty(sourceStack))
        {
            return sourceStack;
        }

        sourceStack = mergeItemStackIntoNextBestSlotInItemHandlers(sourceStack, targetHandler);

        if (ItemStackUtils.isEmpty(sourceStack))
        {
            return sourceStack;
        }

        for (int i = 0; i < targetHandler.getSlots(); i++)
        {
            sourceStack = targetHandler.insertItem(i, sourceStack, false);
            if (ItemStackUtils.isEmpty(sourceStack))
            {
                return sourceStack;
            }
        }

        return sourceStack;
    }

    /**
     * Method to merge the ItemStacks from the given source {@link IItemHandler} to the given target {@link IItemHandler}. Trying to merge itemStacks or returning stack if not
     * possible.
     *
     * @param stack         the stack to add.
     * @param targetHandler The {@link IItemHandler} that works as Target.
     * @return True when the swap was successful, false when not.
     */
    public static ItemStack mergeItemStackIntoNextBestSlotInItemHandlers(
      final ItemStack stack,
      @NotNull final IItemHandler targetHandler)
    {
        if (ItemStackUtils.isEmpty(stack))
        {
            return stack;
        }
        ItemStack sourceStack = stack.copy();

        for (int i = 0; i < targetHandler.getSlots(); i++)
        {
            if (!ItemStackUtils.isEmpty(targetHandler.getStackInSlot(i)) && ItemStack.isSameItem(targetHandler.getStackInSlot(i), sourceStack))
            {
                sourceStack = targetHandler.insertItem(i, sourceStack, false);
                if (ItemStackUtils.isEmpty(sourceStack))
                {
                    return sourceStack;
                }
            }
        }
        return sourceStack;
    }

    /**
     * Drop an actual itemHandler in the world.
     *
     * @param handler the handler.
     * @param world   the world.
     * @param x       the x pos.
     * @param y       the y pos.
     * @param z       the z pos.
     */
    public static void dropItemHandler(final IItemHandler handler, final Level world, final int x, final int y, final int z)
    {
        for (int i = 0; i < handler.getSlots(); ++i)
        {
            final ItemStack itemstack = handler.getStackInSlot(i);

            if (!ItemStackUtils.isEmpty(itemstack))
            {
                spawnItemStack(world, x, y, z, itemstack);
            }
        }
    }

    /**
     * Spawn an itemStack in the world.
     *
     * @param world the world.
     * @param x       the x pos.
     * @param y       the y pos.
     * @param z       the z pos.
     * @param stack   the stack to drop.
     */
    public static void spawnItemStack(final Level world, final double x, final double y, final double z, final ItemStack stack)
    {
        final Random random = new Random();
        final double spawnX = random.nextDouble() * SPAWN_MODIFIER + SPAWN_ADDITION;
        final double spawnY = random.nextDouble() * SPAWN_MODIFIER + SPAWN_ADDITION;
        final double spawnZ = random.nextDouble() * SPAWN_MODIFIER + SPAWN_ADDITION;

        while (stack.getCount() > 0)
        {
            final int randomSplitStackSize = random.nextInt(MAX_RANDOM_SPAWN) + MIN_RANDOM_SPAWN;
            final ItemEntity ItemEntity = new ItemEntity(world, x + spawnX, y + spawnY, z + spawnZ, stack.split(randomSplitStackSize));

            ItemEntity.setDeltaMovement(random.nextGaussian() * MOTION_MULTIPLIER, random.nextGaussian() * MOTION_MULTIPLIER + MOTION_Y_MIN, random.nextGaussian() * MOTION_MULTIPLIER);
            world.addFreshEntity(ItemEntity);
        }
    }
}
