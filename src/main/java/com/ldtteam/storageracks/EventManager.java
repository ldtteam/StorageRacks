package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.ControllerBlock;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.UpgradeableBlock;
import com.ldtteam.storageracks.tileentities.TileEntityRack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
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
        if (!event.getWorld().isClientSide)
        {
            if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof UpgradeableBlock)
            {
                ((UpgradeableBlock) event.getWorld().getBlockState(event.getPos()).getBlock()).checkUpgrade(event.getPos(), event.getPlayer());
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
        final BlockPos result = TileEntityRack.visitPositions((Level) event.getWorld(), posSet, event.getPos());
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
