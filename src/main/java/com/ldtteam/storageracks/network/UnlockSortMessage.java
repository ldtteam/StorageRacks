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
 * Unlock sorting feature at controller.
 */
public class UnlockSortMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "unlock_sort", UnlockSortMessage::new);

    /**
     * Pos of the controller.
     */
    private BlockPos pos;

    /**
     * Empty constructor used when registering the
     */
    public UnlockSortMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.pos = buf.readBlockPos();
    }

    /**
     * Create a new message.
     * @param pos the pos of the controller.
     */
    public UnlockSortMessage(final BlockPos pos)
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
            final int slot = InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(playerEntity.getInventory()), Items.REDSTONE_BLOCK);
            if (slot < 0)
            {
                SoundUtils.playErrorSound(playerEntity, pos);
                playerEntity.displayClientMessage(Component.translatable("com.storageracks.sort.unlock.failed"), false);
                return;
            }
            playerEntity.getInventory().getItem(slot).shrink(1);
        }

        SoundUtils.playSuccessSound(playerEntity, pos);
        playerEntity.displayClientMessage(Component.translatable("com.storageracks.sort.unlock.succeeded"), false);
        ((TileEntityController) te).unlockSort();
    }
}
