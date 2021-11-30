package com.ldtteam.storageracks.network;

import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.utils.InventoryUtils;
import com.ldtteam.storageracks.utils.SoundUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkEvent;

/**
 * Unlock sorting feature at controller.
 */
public class UnlockSortMessage implements IMessage
{
    /**
     * Pos of the controller.
     */
    private BlockPos pos;

    /**
     * Empty constructor used when registering the
     */
    public UnlockSortMessage()
    {
        super();
    }

    /**
     * Create a new message.
     * @param pos the pos of the controller.
     */
    public UnlockSortMessage(final BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void toBytes(final FriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }

    @Override
    public void fromBytes(final FriendlyByteBuf buf)
    {
        this.pos = buf.readBlockPos();
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        final ServerPlayer playerEntity = ctxIn.getSender();
        final BlockEntity te = playerEntity.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof TileEntityController))
        {
            return;
        }

        if (!playerEntity.isCreative())
        {
            final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(playerEntity.getInventory()), Items.REDSTONE_BLOCK);
            if (slot < 0)
            {
                SoundUtils.playErrorSound(playerEntity, pos);
                playerEntity.sendMessage(new TranslatableComponent("com.storageracks.sort.unlock.failed"), playerEntity.getUUID());
                return;
            }
            playerEntity.getInventory().getItem(slot).shrink(1);
        }

        SoundUtils.playSuccessSound(playerEntity, pos);
        playerEntity.sendMessage(new TranslatableComponent("com.storageracks.sort.unlock.succeeded"), playerEntity.getUUID());
        ((TileEntityController) te).unlockSort();
    }
}
