package com.ldtteam.storageracks.blocks;

import com.ldtteam.storageracks.ModCreativeTabs;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to create the modBlocks. References to the blocks can be made here
 * <p>
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBlocks
{
    public static List<RackBlock> racks = new ArrayList<>();

    public static ControllerBlock stoneController;
    public static ControllerBlock ironController;
    public static ControllerBlock goldController;
    public static ControllerBlock emeraldController;
    public static ControllerBlock diamondController;

    /**
     * Private constructor to hide the implicit public one.
     */
    private ModBlocks()
    {

    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        ModBlocks.init(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        ModBlocks.registerBlockItem(event.getRegistry());
    }

    /**
     * Initializes {@link ModBlocks} with the block instances.
     *
     * @param registry The registry to register the new blocks.
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public static void init(final IForgeRegistry<Block> registry)
    {
        for (final WoodType woodType : WoodType.values())
        {
            final List<RackBlock> list = new ArrayList<>();
            for (final FrameType frame : FrameType.values())
            {
                list.add(new RackBlock(woodType, frame, frame.getCost()));
            }

            for (int i = 0; i < list.size() - 1; i++)
            {
                list.get(i).setNext(list.get(i+1));
            }
            racks.addAll(list);
        }

        for (final RackBlock rack : racks)
        {
            registry.register(rack);
        }

        stoneController = new ControllerBlock("stone", Items.IRON_BLOCK, 1);
        ironController = new ControllerBlock("iron", Items.GOLD_BLOCK, 2);
        goldController = new ControllerBlock("gold", Items.EMERALD_BLOCK, 3);
        emeraldController = new ControllerBlock("emerald", Items.DIAMOND_BLOCK, 4);
        diamondController = new ControllerBlock("diamond", null, 5);

        stoneController.setNext(ironController);
        ironController.setNext(goldController);
        goldController.setNext(emeraldController);
        emeraldController.setNext(diamondController);

        registry.register(stoneController);
        registry.register(ironController);
        registry.register(goldController);
        registry.register(emeraldController);
        registry.register(diamondController);
    }

    /**
     * Initializes the registry with the relevant item produced by the relevant blocks.
     *
     * @param registry The item registry to add the items too.
     */
    public static void registerBlockItem(final IForgeRegistry<Item> registry)
    {
        final Item.Properties properties = new Item.Properties().tab(ModCreativeTabs.STORAGERACKS);

        for (final RackBlock rack : racks)
        {
            registry.register((new BlockItem(rack, properties)).setRegistryName(rack.getRegistryName()));
        }

        registry.register((new BlockItem(stoneController, properties)).setRegistryName(stoneController.getRegistryName()));
        registry.register((new BlockItem(ironController, properties)).setRegistryName(ironController.getRegistryName()));
        registry.register((new BlockItem(goldController, properties)).setRegistryName(goldController.getRegistryName()));
        registry.register((new BlockItem(emeraldController, properties)).setRegistryName(emeraldController.getRegistryName()));
        registry.register((new BlockItem(diamondController, properties)).setRegistryName(diamondController.getRegistryName()));
    }
}
