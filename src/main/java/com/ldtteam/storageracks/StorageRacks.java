package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.configuration.Configuration;
import com.ldtteam.storageracks.inv.ModContainers;
import com.ldtteam.storageracks.network.Network;
import com.ldtteam.storageracks.tileentities.ModTileEntities;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("storageracks")
public class StorageRacks
{
    private static final ResourceLocation CREATIVE_TAB = new ResourceLocation(Constants.MOD_ID, "racks");

    /**
     * The config instance.
     */
    public static Configuration config;

    public StorageRacks()
    {
        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModBlocks.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModContainers.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModTileEntities.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

        config = new Configuration();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(EventManager.class);
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(StorageRacks.class);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(HighlightManager.class));
    }

    @SubscribeEvent
    public static void CreativeTabEvent(final CreativeModeTabEvent.Register event)
    {
        event.registerCreativeModeTab(CREATIVE_TAB, (cf) -> cf.icon(() -> new ItemStack(ModBlocks.racks.get(0).get())).withSearchBar().displayItems((flagSet, output) -> {
            output.accept(ModBlocks.stoneController.get());
            output.accept(ModBlocks.ironController.get());
            output.accept(ModBlocks.goldController.get());
            output.accept(ModBlocks.emeraldController.get());
            output.accept(ModBlocks.diamondController.get());

            for (final RegistryObject<RackBlock> rack : ModBlocks.racks)
            {
                output.accept(rack.get());
            }

            for (final RegistryObject<CornerBlock> rack : ModBlocks.corners)
            {
                output.accept(rack.get());
            }
        }));
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
