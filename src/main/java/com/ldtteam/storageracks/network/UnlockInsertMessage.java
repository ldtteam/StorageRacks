package com.ldtteam.storageracks.network;

import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.utils.InventoryUtils;
import com.ldtteam.storageracks.utils.SoundUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.wrapper.InvWrapper;

/**
 * Unlock insert feature at controller.
 */
public class UnlockInsertMessage implements IMessage
{
    /**
     * Pos of the controller.
     */
    private BlockPos pos;

    /**
     * Empty constructor used when registering the
     */
    public UnlockInsertMessage()
    {
        super();
    }

    /**
     * Create a new message.
     * @param pos the pos of the controller.
     */
    public UnlockInsertMessage(final BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void toBytes(final PacketBuffer buf)
    {
        buf.writeBlockPos(pos);
    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {
        this.pos = buf.readBlockPos();
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        final ServerPlayerEntity playerEntity = ctxIn.getSender();
        final TileEntity te = playerEntity.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof TileEntityController))
        {
            return;
        }

        if (!playerEntity.isCreative())
        {
            final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(playerEntity.inventory), Items.HOPPER);
            if (slot < 0)
            {
                SoundUtils.playErrorSound(playerEntity, pos);
                playerEntity.sendMessage(new TranslationTextComponent("com.storageracks.insert.unlock.failed"), playerEntity.getUUID());
                return;
            }
            playerEntity.inventory.getItem(slot).shrink(1);
        }

        playerEntity.sendMessage(new TranslationTextComponent("com.storageracks.insert.unlock.succeeded"), playerEntity.getUUID());
        SoundUtils.playSuccessSound(playerEntity, pos);
        ((TileEntityController) te).unlockInsert();
    }
}
