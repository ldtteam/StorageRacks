package com.ldtteam.storageracks.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

/**
 * Message sent to open an inventory.
 */
public class OpenInventoryMessage implements IMessage
{
    /**
     * The position of the inventory block/entity.
     */
    private BlockPos pos;

    /**
     * Empty public constructor.
     */
    public OpenInventoryMessage()
    {
        super();
    }

    /**
     * Constructor to open an inv.
     * @param pos the pos of the inv.
     */
    public OpenInventoryMessage(final BlockPos pos)
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
        final ServerPlayer player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }
        final BlockEntity tileEntity = player.level.getBlockEntity(pos);
        NetworkHooks.openGui(player, (MenuProvider) tileEntity, packetBuffer -> packetBuffer.writeBlockPos(pos));
    }
}
