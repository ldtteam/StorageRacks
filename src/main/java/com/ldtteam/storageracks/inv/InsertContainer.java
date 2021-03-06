package com.ldtteam.storageracks.inv;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

/**
 * The insert container.
 */
public class InsertContainer extends Container
{
    /**
     * The inventory.
     */
    private final IItemHandler inventory;

    /**
     * Deserialize packet buffer to container instance.
     *
     * @param windowId     the id of the window.
     * @param inv          the player inventory.
     * @param packetBuffer network buffer
     * @return new instance
     */
    public static InsertContainer fromPacketBuffer(final int windowId, final PlayerInventory inv, final PacketBuffer packetBuffer)
    {
        final BlockPos tePos = packetBuffer.readBlockPos();
        return new InsertContainer(windowId, inv, inv.player.level.getBlockEntity(tePos).getCapability(ITEM_HANDLER_CAPABILITY, null).orElse(new ItemStackHandler(0)));
    }

    /**
     * Create a new insert container.
     * @param id the id.
     * @param player the player.
     * @param handler the inv handler.
     */
    public InsertContainer(final int id, @NotNull final PlayerInventory player, @NotNull final IItemHandler handler)
    {
        super(ModContainers.insertInv, id);
        this.inventory = handler;

        for (int j = 0; j < handler.getSlots(); ++j)
        {
            this.addSlot(new SlotItemHandler(handler, j, 44 + j * 18, 20));
        }

        for (int l = 0; l < 3; ++l)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlot(new Slot(player, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlot(new Slot(player, i1, 8 + i1 * 18, 109));
        }
    }

    @Override
    public boolean stillValid(@NotNull final PlayerEntity player)
    {
        return true;
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int inputSlot)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(inputSlot);
        if (slot != null && slot.hasItem())
        {
            final ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (inputSlot < this.inventory.getSlots())
            {
                if (!this.moveItemStackTo(itemstack1, this.inventory.getSlots(), this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemstack1, 0, this.inventory.getSlots(), false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
