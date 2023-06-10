package com.ldtteam.storageracks;

import com.ldtteam.storageracks.utils.ItemStackUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Used to store an stack with various informations to compare items later on.
 */
public class ItemStorage
{
    /**
     * The stack to store.
     */
    private final ItemStack stack;

    /**
     * The creative tab index of the storage.
     */
    private final List<Integer> creativeTabIndex;

    /**
     * Amount of the storage.
     */
    private int amount;

    /**
     * Creates an instance of the storage.
     *
     * @param stack the stack.
     */
    public ItemStorage(@NotNull final ItemStack stack)
    {
        this.stack = stack;
        this.amount = ItemStackUtils.getSize(stack);
        this.creativeTabIndex = new ArrayList<>(); //stack.getItem().getCreativeTabs().stream().filter(Objects::nonNull).map(g -> g.id).collect(Collectors.toList());
    }

    /**
     * Get the itemStack from this itemStorage.
     *
     * @return the stack.
     */
    public ItemStack getItemStack()
    {
        return stack;
    }

    /**
     * Getter for the quantity.
     *
     * @return the amount.
     */
    public int getAmount()
    {
        return this.amount;
    }

    /**
     * Setter for the quantity.
     *
     * @param amount the amount.
     */
    public void setAmount(final int amount)
    {
        this.amount = amount;
    }

    /**
     * Getter for the primary creativeTab index of the storage.
     *
     * @return the index.
     */
    public int getPrimaryCreativeTabIndex()
    {
        return creativeTabIndex.isEmpty() ? 0 : creativeTabIndex.get(0);
    }

    @Override
    public int hashCode()
    {
        //Only use the stack itself for the has, equals will handle the broader attributes
        return Objects.hash(stack.getItem());
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ItemStorage))
        {
            return false;
        }

        final ItemStorage that = (ItemStorage) o;


        return ItemStack.isSameItem(stack, that.getItemStack())
                 && that.getDamageValueValue() == this.getDamageValueValue()
                 && ((that.getItemStack().getTag() == null && this.getItemStack().getTag() == null)
                       || (that.getItemStack().getTag() != null && that.getItemStack().getTag().equals(this.getItemStack().getTag())));
    }

    /**
     * Getter for the stack.
     *
     * @return the stack.
     */
    @NotNull
    public Item getItem()
    {
        return stack.getItem();
    }

    /**
     * Getter for the damage value.
     *
     * @return the damage value.
     */
    public int getDamageValueValue()
    {
        return stack.getDamageValue();
    }

    /**
     * Is this an empty ItemStorage
     * 
     * @return true if empty
     */
    public boolean isEmpty()
    {
        return ItemStackUtils.isEmpty(stack) || amount <= 0;
    }
}
