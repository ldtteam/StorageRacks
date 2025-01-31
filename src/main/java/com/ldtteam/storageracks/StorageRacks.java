package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.configuration.Configuration;
import com.ldtteam.storageracks.inv.ModContainers;
import com.ldtteam.storageracks.network.OpenInventoryMessage;
import com.ldtteam.storageracks.network.SortControllerMessage;
import com.ldtteam.storageracks.network.UnlockInsertMessage;
import com.ldtteam.storageracks.network.UnlockSortMessage;
import com.ldtteam.storageracks.tileentities.ModTileEntities;
import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.tileentities.TileEntityRack;
import com.ldtteam.storageracks.utils.Constants;
import com.ldtteam.storageracks.utils.RenderUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.ldtteam.storageracks.utils.Constants.MOD_ID;
import static net.neoforged.neoforge.common.NeoForge.EVENT_BUS;

@Mod("storageracks")
public class StorageRacks
{
    private static final    ResourceLocation                                   CREATIVE_TAB       = new ResourceLocation(MOD_ID, "racks");

    /**
     * The config instance.
     */
    public static Configuration config;

    public static final DeferredRegister<CreativeModeTab> TAB_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final Supplier<CreativeModeTab>
      GENERAL = TAB_REG.register("general", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 1).icon(() -> new ItemStack(ModBlocks.racks.get(0))).title(Component.translatable("gui.storageracks.allinventory")).displayItems((config, output) -> {
        output.accept(ModBlocks.stoneController.get());
        output.accept(ModBlocks.ironController.get());
        output.accept(ModBlocks.goldController.get());
        output.accept(ModBlocks.emeraldController.get());
        output.accept(ModBlocks.diamondController.get());
        output.accept(ModBlocks.netherite_controller.get());

        for (final DeferredBlock<RackBlock> rack : ModBlocks.racks)
        {
            output.accept(rack.get());
        }

        for (final DeferredBlock<CornerBlock> rack : ModBlocks.corners)
        {
            output.accept(rack.get());
        }
    }).build());

    public StorageRacks(final FMLModContainer modContainer, final Dist dist)
    {
        final IEventBus modBus = modContainer.getEventBus();
        final IEventBus forgeBus = NeoForge.EVENT_BUS;

        ModBlocks.BLOCKS.register(modBus);
        ModBlocks.ITEMS.register(modBus);
        ModContainers.CONTAINERS.register(modBus);
        ModTileEntities.BLOCK_ENTITIES.register(modBus);
        TAB_REG.register(modBus);

        config = new Configuration(modContainer);

        // Register ourselves for server and other game events we are interested in
        EVENT_BUS.register(EventManager.class);
        modBus.register(StorageRacks.class);

        if (dist.isClient())
        {
            modBus.register(RenderUtils.class);
            forgeBus.register(HighlightManager.class);
        }
    }

    @SubscribeEvent
    public static void onNetworkRegistry(final RegisterPayloadHandlersEvent event)
    {
        final String modVersion = ModList.get().getModContainerById(Constants.MOD_ID).get().getModInfo().getVersion().toString();
        final PayloadRegistrar registry = event.registrar(Constants.MOD_ID).versioned(modVersion);

        SortControllerMessage.TYPE.register(registry);
        UnlockInsertMessage.TYPE.register(registry);
        OpenInventoryMessage.TYPE.register(registry);
        UnlockSortMessage.TYPE.register(registry);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerCaps(final RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTileEntities.RACK.get(), TileEntityRack::getCapability);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModTileEntities.CONTROLLER.get(), TileEntityController::getCapability);
    }
}
