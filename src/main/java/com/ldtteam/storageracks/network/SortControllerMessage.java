package com.ldtteam.storageracks.network;

import com.ldtteam.storageracks.tileentities.AbstractTileEntityRack;
import com.ldtteam.storageracks.inv.CombinedItemHandler;
import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.utils.SortingUtils;
import com.ldtteam.storageracks.utils.SoundUtils;
import com.ldtteam.storageracks.utils.WorldUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Sorts the racks connected to the controller.
 */
public class SortControllerMessage implements IMessage
{
    private BlockPos controllerPos;

    /**
     * Empty constructor used when registering the
     */
    public SortControllerMessage()
    {
        super();
    }

    /**
     * Creates the sort message with the pos of the controller.
     *
     * @param pos the pos.
     */
    public SortControllerMessage(final BlockPos pos)
    {
        this.controllerPos = pos;
    }

    @Override
    public void toBytes(final FriendlyByteBuf packetBuffer)
    {
        packetBuffer.writeBlockPos(controllerPos);
    }

    @Override
    public void fromBytes(final FriendlyByteBuf buf)
    {
        this.controllerPos = buf.readBlockPos();
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.SERVER;
    }

    @Override
    public void onExecute(final NetworkEvent.Context context, final boolean b)
    {
        final Level world = context.getSender().getCommandSenderWorld();
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
                        handlers.add((IItemHandlerModifiable) te.getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve().get());
                    }
                }
            }
            SortingUtils.sort(new CombinedItemHandler("controller", handlers.toArray(new IItemHandlerModifiable[0])));
            SoundUtils.playSuccessSound(context.getSender(), controllerPos);
        }
    }
}
