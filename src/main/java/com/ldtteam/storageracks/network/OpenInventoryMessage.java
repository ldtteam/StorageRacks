package com.ldtteam.storageracks.network;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Message sent to open an inventory.
 */
public class OpenInventoryMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "open_inventory", OpenInventoryMessage::new);

    /**
     * The position of the inventory block/entity.
     */
    private BlockPos pos;

    /**
     * Empty public constructor.
     */
    public OpenInventoryMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.pos = buf.readBlockPos();
    }

    /**
     * Constructor to open an inv.
     * @param pos the pos of the inv.
     */
    public OpenInventoryMessage(final BlockPos pos)
    {
        super(TYPE);
        this.pos = pos;
    }

    @Override
    public void toBytes(final RegistryFriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void onExecute(final IPayloadContext context, final ServerPlayer playerEntity)
    {
        if (playerEntity == null)
        {
            return;
        }
        final BlockEntity tileEntity = playerEntity.level().getBlockEntity(pos);
        playerEntity.openMenu((MenuProvider) tileEntity, pos);
    }
}
