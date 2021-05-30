package com.ldtteam.storageracks;

import com.ldtteam.storageracks.configuration.Configuration;
import com.ldtteam.storageracks.network.Network;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

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
        MinecraftForge.EVENT_BUS.register(EventManager.class);
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(this.getClass());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(HighlightManager.class));
    }

    /**
     * Event handler for forge pre init event.
     *
     * @param event the forge pre init event.
     */
    @SubscribeEvent
    public static void preInit(@NotNull final FMLCommonSetupEvent event)
    {
        Network.getNetwork().registerCommonMessages();
    }
}
