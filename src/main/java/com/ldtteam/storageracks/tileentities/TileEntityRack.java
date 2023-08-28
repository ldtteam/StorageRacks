package com.ldtteam.storageracks.tileentities;

import com.google.common.collect.ImmutableList;
import com.ldtteam.domumornamentum.client.model.data.MaterialTextureData;
import com.ldtteam.domumornamentum.client.model.properties.ModProperties;
import com.ldtteam.domumornamentum.entity.block.IMateriallyTexturedBlockEntity;
import com.ldtteam.storageracks.ItemStorage;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.RackType;
import com.ldtteam.storageracks.inv.ContainerRack;
import com.ldtteam.storageracks.utils.BlockPosUtil;
import com.ldtteam.storageracks.utils.ItemStackUtils;
import com.ldtteam.storageracks.utils.WorldUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static com.ldtteam.storageracks.utils.Constants.*;
import static com.ldtteam.storageracks.utils.NbtTagConstants.*;

/**
 * Tile entity for the warehouse shelves.
 */
public class TileEntityRack extends AbstractTileEntityRack implements IMateriallyTexturedBlockEntity
{
    /**
     * Static texture mappings
     */
    private static final List<ResourceLocation> textureMapping = ImmutableList.<ResourceLocation>builder()
                                                                   .add(new ResourceLocation("block/bricks"))
                                                                   .add(new ResourceLocation("block/sand"))
                                                                   .add(new ResourceLocation("block/orange_wool"))
                                                                   .add(new ResourceLocation("block/dirt")).build();

    private static final List<ResourceLocation> secondarytextureMapping = ImmutableList.<ResourceLocation>builder()
                                                                            .add(new ResourceLocation("block/oak_log"))
                                                                            .add(new ResourceLocation("block/spruce_log"))
                                                                            .add(new ResourceLocation("block/birch_log"))
                                                                            .add(new ResourceLocation("block/jungle_log"))
                                                                            .build();

    /**
     * Cached resmap.
     */
    private MaterialTextureData textureDataCache = new MaterialTextureData();

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
     * Last optional we created.
     */
    private LazyOptional<IItemHandler> lastOptional;

    /**
     * New TileEntity.
     * @param pos position.
     * @param state initial state.
     */
    public TileEntityRack(final BlockPos pos, final BlockState state)
    {
        super(ModTileEntities.RACK.get(), pos, state);
    }

    /**
     * Selfmade rotate method, called from block.
     * @param rotationIn the rotation.
     */
    public void rotate(final Rotation rotationIn)
    {
        if (controller != null)
        {
            controller = controller.rotate(rotationIn);
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
           if (checkItem.getPrimaryCreativeTabIndex() == storage.getPrimaryCreativeTabIndex())
           {
               return true;
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
    public void load(@NotNull final CompoundTag compound)
    {
        super.load(compound);

        int oldSize = compound.getInt(TAG_SIZE);
        checkForUpgrade(getBlockState(), oldSize);

        inventory = createInventory(DEFAULT_SIZE + size * SLOT_PER_LINE);

        final ListTag inventoryTagList = compound.getList(TAG_INVENTORY, Tag.TAG_COMPOUND);
        for (int i = 0; i < inventoryTagList.size(); i++)
        {
            final CompoundTag inventoryCompound = inventoryTagList.getCompound(i);
            if (!inventoryCompound.contains(TAG_EMPTY))
            {
                final ItemStack stack = ItemStack.of(inventoryCompound);
                inventory.setStackInSlot(i, stack);
            }
        }

        updateContent();

        this.controllerPos = BlockPosUtil.readFromNBT(compound, TAG_POS);
        invalidateCap();

        if (level != null && level.isClientSide)
        {
            refreshTextureCache();
        }
    }

    //Make a dag between rack -> controller

    public void checkForUpgrade(final BlockState state, final int oldSize)
    {
        final RackBlock block = (RackBlock) state.getBlock();
        if (block.frameType.getSerializedName().contains("stone"))
        {
            size = 1;
        }
        else if (block.frameType.getSerializedName().contains("iron"))
        {
            size = 2;
        }
        else if (block.frameType.getSerializedName().contains("gold"))
        {
            size = 3;
        }
        else if (block.frameType.getSerializedName().contains("emerald"))
        {
            size = 4;
        }
        else if (block.frameType.getSerializedName().contains("diamond"))
        {
            size = 5;
        }

        if (oldSize != size)
        {
            upgradeItemStorage();
        }
    }

    @Override
    public void saveAdditional(@NotNull final CompoundTag compound)
    {
        super.saveAdditional(compound);
        compound.putInt(TAG_SIZE, size);

        @NotNull final ListTag inventoryTagList = new ListTag();
        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            @NotNull final CompoundTag inventoryCompound = new CompoundTag();
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
    public AbstractContainerMenu createMenu(final int id, @NotNull final Inventory inv, @NotNull final Player player)
    {
        return new ContainerRack(id, inv, getBlockPos());
    }

    @NotNull
    @Override
    public Component getDisplayName()
    {
        return Component.translatable("container.title.rack");
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
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

    /**
     * Return false if not successful.
     */
    public void neighborChange()
    {
        final Set<BlockPos> visitedPositions = new HashSet<>();
        final BlockPos controller = visitPositions(level, visitedPositions, this.getBlockPos());
        visitedPositions.removeIf(pos -> level.getBlockState(pos).getBlock() instanceof CornerBlock);

        if (controller != BlockPos.ZERO && controller != null)
        {
            this.controller = getBlockPos().subtract(controller);
            ((TileEntityController) level.getBlockEntity(controller)).addAll(visitedPositions);
            for (final BlockPos pos : visitedPositions)
            {
                if (!pos.equals(controller))
                {
                    ((TileEntityRack) level.getBlockEntity(pos)).controller = pos.subtract(controller);
                }
            }
        }
        else if (controller == null)
        {
            BlockPos oldController = null;
            for (final BlockPos pos : visitedPositions)
            {
                final TileEntityRack rack = ((TileEntityRack) level.getBlockEntity(pos));
                if (rack.controller != null)
                {
                    oldController = rack.getBlockPos().subtract(rack.controller);
                    rack.controller = null;
                }
            }

            if (oldController != null)
            {
                final TileEntityController te = ((TileEntityController) level.getBlockEntity(oldController));
                if (te != null)
                {
                    te.removeAll(visitedPositions);
                }
            }
        }
    }

    @Override
    public void setBlockState(@NotNull final BlockState state)
    {
        super.setBlockState(state);
        invalidateCap();
    }

    public static BlockPos visitPositions(final Level level, final Set<BlockPos> visitedPositions, final BlockPos current)
    {
        BlockPos controller = null;
        if (level.getBlockEntity(current) instanceof TileEntityController)
        {
            controller = current;
        }

        visitedPositions.add(current);

        for (final Direction dir : Direction.values())
        {
            final BlockPos next = current.relative(dir);
            if (!visitedPositions.contains(next))
            {
                final BlockEntity te = level.getBlockEntity(next);
                if (te instanceof TileEntityRack || te instanceof TileEntityController || level.getBlockState(next).getBlock() instanceof CornerBlock)
                {
                    final BlockPos cont = visitPositions(level, visitedPositions, next);
                    if (cont != null)
                    {
                        if (cont.equals(BlockPos.ZERO) || (controller != null && !cont.equals(controller)))
                        {
                            return BlockPos.ZERO;
                        }
                        controller = cont;
                    }
                }
            }
        }
        return controller;
    }

    @Override
    public void updateTextureDataWith(final MaterialTextureData materialTextureData)
    {
        // noop
    }

    /**
     * Refresh the texture mapping.
     */
    private void refreshTextureCache()
    {
        final Map<ResourceLocation, Block> resMap = new HashMap<>();
        final int displayPerSlots = this.getInventory().getSlots() / 4;
        int index = 0;
        boolean update = false;

        final List<Map.Entry<ItemStorage, Integer>> list = content.entrySet().stream().sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())).toList();
        final Queue<Block> extraBlockQueue = new ArrayDeque<>();
        for (final Map.Entry<ItemStorage, Integer> entry : list)
        {
            // Need more solid checks!
            if (index < textureMapping.size())
            {
                Block block = Blocks.BARREL;
                if (entry.getKey().getItemStack().getItem() instanceof BlockItem blockitem)
                {
                    block = blockitem.getBlock();
                }

                int displayRows = (int) Math.ceil((Math.max(1.0, (double) entry.getValue() / entry.getKey().getItemStack().getMaxStackSize())) / displayPerSlots);
                if (displayRows > 1)
                {
                    for (int i = 0; i < displayRows - 1; i++)
                    {
                        extraBlockQueue.add(block);
                    }
                }

                if (entry.getValue() < 16 && !extraBlockQueue.isEmpty())
                {
                    block = extraBlockQueue.poll();
                }

                final ResourceLocation secondaryResLoc = secondarytextureMapping.get(index);
                if (!block.defaultBlockState().isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))
                {
                    resMap.put(secondaryResLoc, block);
                    block = Blocks.BARREL;
                }
                else
                {
                    resMap.put(secondaryResLoc, Blocks.AIR);
                }

                final ResourceLocation resLoc = textureMapping.get(index);
                resMap.put(resLoc, block);

                if (this.textureDataCache == null
                      || !this.textureDataCache.getTexturedComponents().getOrDefault(resLoc, Blocks.BEDROCK).equals(resMap.get(resLoc))
                      || !this.textureDataCache.getTexturedComponents().getOrDefault(secondaryResLoc, Blocks.BEDROCK).equals(resMap.get(secondaryResLoc)))
                {
                    update = true;
                }
                index++;
            }
        }

        for (int i = index; i < textureMapping.size(); i++)
        {
            Block block = Blocks.AIR;
            if (!extraBlockQueue.isEmpty())
            {
                block = extraBlockQueue.poll();
            }

            final ResourceLocation secondaryResLoc = secondarytextureMapping.get(i);
            if (block != Blocks.AIR && !block.defaultBlockState().isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))
            {
                resMap.put(secondaryResLoc, block);
                block = Blocks.BARREL;
            }
            else
            {
                resMap.put(secondaryResLoc, Blocks.AIR);
            }

            final ResourceLocation resLoc = textureMapping.get(i);
            resMap.put(resLoc, block);

            if (this.textureDataCache == null
                  || !this.textureDataCache.getTexturedComponents().getOrDefault(resLoc, Blocks.BEDROCK).equals(resMap.get(resLoc))
                  || !this.textureDataCache.getTexturedComponents().getOrDefault(secondaryResLoc, Blocks.BEDROCK).equals(resMap.get(secondaryResLoc)))
            {
                update = true;
            }
        }

        if (update)
        {
            this.textureDataCache = new MaterialTextureData(resMap);
            this.requestModelDataUpdate();
            if (level != null)
            {
                level.sendBlockUpdated(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public @NotNull ModelData getModelData()
    {
        return ModelData.builder()
                 .with(ModProperties.MATERIAL_TEXTURE_PROPERTY, textureDataCache)
                 .build();
    }

    @Override
    public @NotNull MaterialTextureData getTextureData()
    {
        return textureDataCache;
    }
}
