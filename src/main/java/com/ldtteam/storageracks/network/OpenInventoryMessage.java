package com.ldtteam.storageracks.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

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
        final ServerPlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }
        final TileEntity tileEntity = player.level.getBlockEntity(pos);
        NetworkHooks.openGui(player, (INamedContainerProvider) tileEntity, packetBuffer -> packetBuffer.writeBlockPos(pos));
    }
}
