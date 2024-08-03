package com.ldtteam.storageracks.network;

import com.ldtteam.common.network.AbstractServerPlayMessage;
import com.ldtteam.common.network.PlayMessageType;
import com.ldtteam.storageracks.tileentities.AbstractTileEntityRack;
import com.ldtteam.storageracks.inv.CombinedItemHandler;
import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.utils.Constants;
import com.ldtteam.storageracks.utils.SortingUtils;
import com.ldtteam.storageracks.utils.SoundUtils;
import com.ldtteam.storageracks.utils.WorldUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Sorts the racks connected to the controller.
 */
public class SortControllerMessage extends AbstractServerPlayMessage
{
    public static final PlayMessageType<?> TYPE = PlayMessageType.forServer(Constants.MOD_ID, "sort_controller", SortControllerMessage::new);

    private BlockPos controllerPos;

    /**
     * Empty constructor used when registering the
     */
    public SortControllerMessage(final RegistryFriendlyByteBuf buf, final PlayMessageType<?> type)
    {
        super(buf, type);
        this.controllerPos = buf.readBlockPos();
    }

    /**
     * Creates the sort message with the pos of the controller.
     *
     * @param pos the pos.
     */
    public SortControllerMessage(final BlockPos pos)
    {
        super(TYPE);
        this.controllerPos = pos;
    }

    @Override
    protected void toBytes(final RegistryFriendlyByteBuf buf)
    {
        buf.writeBlockPos(controllerPos);
    }

    @Override
    protected void onExecute(final IPayloadContext context, final ServerPlayer player)
    {
        final Level world = player.level();
        final BlockEntity tileEntity = world.getBlockEntity(controllerPos);
        if (tileEntity instanceof TileEntityController && ((TileEntityController) tileEntity).isSortUnlocked())
        {
            final Set<IItemHandlerModifiable> handlers = new LinkedHashSet<>();

            for (final BlockPos pos : ((TileEntityController) tileEntity).racks)
            {
                if (WorldUtil.isBlockLoaded(world, pos))
                {
                    final BlockEntity te = world.getBlockEntity(pos);
                    if (te instanceof AbstractTileEntityRack)
                    {
                        handlers.add((IItemHandlerModifiable) Capabilities.ItemHandler.BLOCK.getCapability(world, pos, te.getBlockState(), te, null));
                    }
                }
            }
            SortingUtils.sort(new CombinedItemHandler("controller", handlers.toArray(new IItemHandlerModifiable[0])), player.level().registryAccess());
            SoundUtils.playSuccessSound(player, controllerPos);
        }
    }
}
