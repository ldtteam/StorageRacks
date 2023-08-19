package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.ControllerBlock;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.UpgradeableBlock;
import com.ldtteam.storageracks.tileentities.TileEntityRack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;

public class EventManager
{
    /**
     * BlockEvent.BreakEvent handler.
     *
     * @param event BlockEvent.BreakEvent
     */
    @SubscribeEvent
    public static void on(final PlayerInteractEvent.LeftClickBlock event)
    {
        if (!event.getLevel().isClientSide)
        {
            if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof UpgradeableBlock)
            {
                ((UpgradeableBlock) event.getLevel().getBlockState(event.getPos()).getBlock()).checkUpgrade(event.getPos(), event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void on(final BlockEvent.EntityPlaceEvent event)
    {
        if (!(event.getPlacedBlock().getBlock() instanceof RackBlock
                || event.getPlacedBlock().getBlock() instanceof ControllerBlock) || !(event.getEntity() instanceof Player))
        {
            return;
        }

        final HashSet<BlockPos> posSet = new HashSet<>();
        final BlockPos result = TileEntityRack.visitPositions((Level) event.getLevel(), posSet, event.getPos());
        posSet.removeIf(pos -> event.getLevel().getBlockState(pos).getBlock() instanceof CornerBlock);

        if (result == null || result.equals(BlockPos.ZERO))
        {
            if (event.getPlacedBlock().getBlock() instanceof ControllerBlock)
            {
                ((Player) event.getEntity()).displayClientMessage(Component.translatable("gui.storageracks.doublecontroller"), false);
            }
            else
            {
                ((Player) event.getEntity()).displayClientMessage(Component.translatable("gui.storageracks.notconnected"), false);
            }
            event.setCanceled(true);
        }
        else
        {
            final ControllerBlock controller = (ControllerBlock) event.getEntity().level.getBlockState(result).getBlock();
            if (posSet.size() - 1 > controller.getTier() * 20)
            {
                ((Player) event.getEntity()).displayClientMessage(Component.translatable("gui.storageracks.limitreached"), false);
                event.setCanceled(true);
            }
        }
    }
}
