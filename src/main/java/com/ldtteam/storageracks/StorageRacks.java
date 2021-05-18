package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.UpgradeableBlock;
import com.ldtteam.storageracks.configuration.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("storageracks")
public class StorageRacks
{
    /**
     * The config instance.
     */
    public static Configuration config;

    public StorageRacks()
    {
        config = new Configuration();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this.getClass());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(HighlightManager.class));
    }

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
}
