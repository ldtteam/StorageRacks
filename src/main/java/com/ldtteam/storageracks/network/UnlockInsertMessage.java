package com.ldtteam.storageracks.network;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.utils.Constants;
import com.ldtteam.storageracks.utils.InventoryUtils;
import com.ldtteam.storageracks.utils.SoundUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Unlock insert feature at controller.
 */
public class UnlockInsertMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "unlock_insert", UnlockInsertMessage::new);

    /**
     * Pos of the controller.
     */
    private BlockPos pos;

    /**
     * Empty constructor used when registering the
     */
    public UnlockInsertMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.pos = buf.readBlockPos();
    }

    /**
     * Create a new message.
     * @param pos the pos of the controller.
     */
    public UnlockInsertMessage(final BlockPos pos)
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
        final BlockEntity te = playerEntity.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof TileEntityController))
        {
            return;
        }

        if (!playerEntity.isCreative())
        {
            final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(playerEntity.getInventory()), Items.HOPPER);
            if (slot < 0)
            {
                SoundUtils.playErrorSound(playerEntity, pos);
                playerEntity.displayClientMessage(Component.translatable("com.storageracks.insert.unlock.failed"), false);
                return;
            }
            playerEntity.getInventory().getItem(slot).shrink(1);
        }

        playerEntity.displayClientMessage(Component.translatable("com.storageracks.insert.unlock.succeeded"), false);
        SoundUtils.playSuccessSound(playerEntity, pos);
        ((TileEntityController) te).unlockInsert();
    }
}
