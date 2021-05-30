package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.ControllerBlock;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.UpgradeableBlock;
import com.ldtteam.storageracks.tileentities.TileEntityRack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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
                || event.getPlacedBlock().getBlock() instanceof ControllerBlock) || !(event.getEntity() instanceof PlayerEntity))
        {
            return;
        }

        final HashSet<BlockPos> posSet = new HashSet<>();
        final BlockPos result = TileEntityRack.visitPositions((World) event.getWorld(), posSet, event.getPos());
        if (result == null || result.equals(BlockPos.ZERO))
        {
            if (event.getPlacedBlock().getBlock() instanceof ControllerBlock)
            {
                event.getEntity().sendMessage(new TranslationTextComponent("gui.storageracks.doublecontroller"), event.getEntity().getUUID());
            }
            else
            {
                event.getEntity().sendMessage(new TranslationTextComponent("gui.storageracks.notconnected"), event.getEntity().getUUID());
            }
            event.setCanceled(true);
        }
        else
        {
            final ControllerBlock controller = (ControllerBlock) event.getEntity().level.getBlockState(result).getBlock();
            if (posSet.size() > controller.getTier() * 20)
            {
                event.getEntity().sendMessage(new TranslationTextComponent("gui.storageracks.limitreached"), event.getEntity().getUUID());
                event.setCanceled(true);
            }
        }
    }
}
