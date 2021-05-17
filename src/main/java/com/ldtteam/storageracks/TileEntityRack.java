package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.RackType;
import com.ldtteam.storageracks.utils.ItemStackUtils;
import com.ldtteam.storageracks.utils.WorldUtil;
import com.ldtteam.structurize.api.util.BlockPosUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.ldtteam.storageracks.utils.Constants.*;
import static com.ldtteam.storageracks.utils.NbtTagConstants.*;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

/**
 * Tile entity for the warehouse shelves.
 */
public class TileEntityRack extends AbstractTileEntityRack
{
    /**
     * The content of the chest.
     */
    private final Map<ItemStorage, Integer> content = new HashMap<>();

    /**
     * Size multiplier of the inventory. 0 = default value. 1 = 1*9 additional slots, and so on.
     */
    private int size = 0;

    /**
     * Amount of free slots
     */
    private int freeSlots = 0;

    /**
     * Offset to the controller.
     */
    private BlockPos controller;

    /**
     * Offset to the parent rack.
     */
    private BlockPos parent;

    /**
     * Last optional we created.
     */
    private LazyOptional<IItemHandler> lastOptional;

    /**
     * New TileEntity.
     */
    public TileEntityRack()
    {
        super(ModTileEntities.RACK);
    }

    @Override
    public void rotate(final Rotation rotationIn)
    {
        super.rotate(rotationIn);
        if (controller != null)
        {
            controller = controller.rotate(rotationIn);
        }

        if (parent != null)
        {
            parent = parent.rotate(rotationIn);
        }
    }

    @Override
    public int getFreeSlots()
    {
        return freeSlots;
    }

    @Override
    public boolean hasItemStack(final ItemStack stack, final int count)
    {
        final ItemStorage checkItem = new ItemStorage(stack);

        return content.getOrDefault(checkItem, 0) >= count;
    }

    @Override
    public int getCount(final ItemStack stack)
    {
        final ItemStorage checkItem = new ItemStorage(stack);
        return content.getOrDefault(checkItem, 0);
    }

    @Override
    public boolean hasItemStack(@NotNull final Predicate<ItemStack> itemStackSelectionPredicate)
    {
        for (final Map.Entry<ItemStorage, Integer> entry : content.entrySet())
        {
            if (itemStackSelectionPredicate.test(entry.getKey().getItemStack()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasSimilarStack(@NotNull final ItemStack stack)
    {
        final ItemStorage checkItem = new ItemStorage(stack);
        if (content.containsKey(checkItem))
        {
            return true;
        }

        for (final ItemStorage storage : content.keySet())
        {
            for (final ResourceLocation tag : stack.getItem().getTags())
            {
                if (StorageRacks.config.getServer().enabledModTags.get().contains(tag.toString())
                      && storage.getItemStack().getItem().getTags().contains(tag))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the content of the Rack
     *
     * @return the map of content.
     */
    public Map<ItemStorage, Integer> getAllContent()
    {
        return content;
    }

    @Override
    public void upgradeItemStorage()
    {
        final RackInventory tempInventory = new RackInventory(DEFAULT_SIZE + size * SLOT_PER_LINE);
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            tempInventory.setStackInSlot(slot, inventory.getStackInSlot(slot));
        }

        inventory = tempInventory;
        final BlockState state = level.getBlockState(getBlockPos());
        level.sendBlockUpdated(getBlockPos(), state, state, 0x03);
        invalidateCap();
    }

    @Override
    public int getItemCount(final Predicate<ItemStack> predicate)
    {
        for (final Map.Entry<ItemStorage, Integer> entry : content.entrySet())
        {
            if (predicate.test(entry.getKey().getItemStack()))
            {
                return entry.getValue();
            }
        }
        return 0;
    }

    @Override
    public void updateItemStorage()
    {
        if (level != null && !level.isClientSide)
        {
            final boolean empty = content.isEmpty();
            updateContent();

            if ((empty && !content.isEmpty()) || !empty && content.isEmpty())
            {
                updateBlockState();
            }
            setChanged();
        }
    }

    /**
     * Just do the content update.
     */
    private void updateContent()
    {
        content.clear();
        freeSlots = 0;
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            final ItemStack stack = inventory.getStackInSlot(slot);

            if (ItemStackUtils.isEmpty(stack))
            {
                freeSlots++;
                continue;
            }

            final ItemStorage storage = new ItemStorage(stack.copy());
            int amount = ItemStackUtils.getSize(stack);
            if (content.containsKey(storage))
            {
                amount += content.remove(storage);
            }
            content.put(storage, amount);
        }
    }

    @Override
    public void updateBlockState()
    {
        if (level != null && level.getBlockState(getBlockPos()).getBlock() instanceof RackBlock)
        {
            if (content.isEmpty())
            {
                level.setBlock(this.getBlockPos(), level.getBlockState(this.getBlockPos()).setValue(RackBlock.VARIANT, RackType.DEFAULT), 0x03);
            }
            else
            {
                level.setBlock(this.getBlockPos(), level.getBlockState(this.getBlockPos()).setValue(RackBlock.VARIANT, RackType.FULL), 0x03);
            }
        }
    }

    @Override
    public ItemStackHandler createInventory(final int slots)
    {
        return new RackInventory(slots);
    }

    @Override
    public boolean isEmpty()
    {
        return content.isEmpty();
    }


    @Override
    public void load(final BlockState state, final CompoundNBT compound)
    {
        super.load(state, compound);

        int oldSize = compound.getInt(TAG_SIZE);
        checkForUpgrade(state, oldSize);

        inventory = createInventory(DEFAULT_SIZE + size * SLOT_PER_LINE);

        final ListNBT inventoryTagList = compound.getList(TAG_INVENTORY, TAG_COMPOUND);
        for (int i = 0; i < inventoryTagList.size(); i++)
        {
            final CompoundNBT inventoryCompound = inventoryTagList.getCompound(i);
            if (!inventoryCompound.contains(TAG_EMPTY))
            {
                final ItemStack stack = ItemStack.of(inventoryCompound);
                inventory.setStackInSlot(i, stack);
            }
        }

        updateContent();

        this.controllerPos = BlockPosUtil.readFromNBT(compound, TAG_POS);
        invalidateCap();
    }

    //Make a dag between rack -> controller

    public void checkForUpgrade(final BlockState state, final int oldSize)
    {
        if (state.getBlock().getRegistryName().getPath().contains("stone"))
        {
            size = 1;
        }
        else if (state.getBlock().getRegistryName().getPath().contains("iron"))
        {
            size = 2;
        }
        else if (state.getBlock().getRegistryName().getPath().contains("gold"))
        {
            size = 3;
        }
        else if (state.getBlock().getRegistryName().getPath().contains("emerald"))
        {
            size = 4;
        }
        else if (state.getBlock().getRegistryName().getPath().contains("diamond"))
        {
            size = 5;
        }

        if (oldSize != size)
        {
            upgradeItemStorage();
        }
    }

    @NotNull
    @Override
    public CompoundNBT save(final CompoundNBT compound)
    {
        super.save(compound);
        compound.putInt(TAG_SIZE, size);

        @NotNull final ListNBT inventoryTagList = new ListNBT();
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            @NotNull final CompoundNBT inventoryCompound = new CompoundNBT();
            final ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty())
            {
                inventoryCompound.putBoolean(TAG_EMPTY, true);
            }
            else
            {
                stack.save(inventoryCompound);
            }
            inventoryTagList.add(inventoryCompound);
        }
        compound.put(TAG_INVENTORY, inventoryTagList);
        BlockPosUtil.writeToNBT(compound, TAG_POS, controllerPos);
        return compound;
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final Direction dir)
    {
        if (!remove && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (lastOptional != null && lastOptional.isPresent())
            {
                return lastOptional.cast();
            }

            lastOptional = LazyOptional.of(() ->
            {
                if (this.isRemoved())
                {
                    return new RackInventory(0);
                }

                return getInventory();
            });
            return lastOptional.cast();
        }
        return super.getCapability(capability, dir);
    }

    @Override
    public void setChanged()
    {
        WorldUtil.markChunkDirty(level, getBlockPos());
    }

    @Nullable
    @Override
    public Container createMenu(final int id, @NotNull final PlayerInventory inv, @NotNull final PlayerEntity player)
    {
        return new ContainerRack(id, inv, getBlockPos());
    }

    @NotNull
    @Override
    public ITextComponent getDisplayName()
    {
        return new StringTextComponent("Rack");
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        invalidateCap();
    }

    @Override
    public void clearCache()
    {
        super.clearCache();
        invalidateCap();
    }

    /**
     * Invalidates the cap
     */
    private void invalidateCap()
    {
        if (lastOptional != null && lastOptional.isPresent())
        {
            lastOptional.invalidate();
        }

        lastOptional = null;
    }

    public int getSize()
    {
        return size;
    }

    public void newNeighborRack(final BlockPos newPos)
    {
        final BlockPos relativePos = getBlockPos().subtract(newPos);
        if (parent == null)
        {
            tryReCalculateGraph();
            parent = relativePos;
            controller = ((TileEntityRack)level.getBlockEntity(newPos)).controller;
        }
    }

    public void potentialRackRemoval(final BlockPos newPos)
    {
        final BlockPos relativePos = getBlockPos().subtract(newPos);
        if (relativePos.equals(parent))
        {
            tryReCalculateGraph();
        }
    }
}
