package com.ldtteam.storageracks.blocks;

import com.ldtteam.storageracks.ModCreativeTabs;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class to create the modBlocks. References to the blocks can be made here
 * <p>
 */
public final class ModBlocks
{
    /**
     * The deferred registry.
     */
    public final static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);
    public final static DeferredRegister<Item>  ITEMS  = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    /**
     * Utility shorthand to register blocks using the deferred registry.
     * Register item block together.
     * @param name the registry name of the block
     * @param block a factory / constructor to create the block on demand
     * @param <B> the block subclass for the factory response
     * @return the block entry saved to the registry
     */
    public static <B extends Block, I extends Item> RegistryObject<B> register(String name, Supplier<B> block, Function<B, I> item)
    {
        RegistryObject<B> registered = BLOCKS.register(name.toLowerCase(), block);
        ITEMS.register(name.toLowerCase(), () -> item.apply(registered.get()));
        return registered;
    }

    public static List<RegistryObject<RackBlock>> racks = new ArrayList<>();
    public static List<RegistryObject<CornerBlock>> corners = new ArrayList<>();

    public static RegistryObject<ControllerBlock> stoneController;
    public static RegistryObject<ControllerBlock> ironController;
    public static RegistryObject<ControllerBlock> goldController;
    public static RegistryObject<ControllerBlock> emeraldController;
    public static RegistryObject<ControllerBlock> diamondController;

    static
    {
        stoneController = register("stone_controller", () -> new ControllerBlock(FrameType.STONE.getSerializedName(), Items.IRON_BLOCK, 1), b -> new BlockItem(b, new Item.Properties().tab(ModCreativeTabs.STORAGERACKS)));
        ironController = register("iron_controller", () -> new ControllerBlock(FrameType.IRON.getSerializedName(), Items.GOLD_BLOCK, 2), b -> new BlockItem(b, new Item.Properties().tab(ModCreativeTabs.STORAGERACKS)));
        goldController = register("gold_controller", () -> new ControllerBlock(FrameType.GOLD.getSerializedName(), Items.EMERALD_BLOCK, 3), b -> new BlockItem(b, new Item.Properties().tab(ModCreativeTabs.STORAGERACKS)));
        emeraldController = register("emerald_controller", () -> new ControllerBlock(FrameType.EMERALD.getSerializedName(), Items.DIAMOND_BLOCK, 4), b -> new BlockItem(b, new Item.Properties().tab(ModCreativeTabs.STORAGERACKS)));
        diamondController = register("diamond_controller", () -> new ControllerBlock(FrameType.DIAMOND.getSerializedName(), null, 5), b -> new BlockItem(b, new Item.Properties().tab(ModCreativeTabs.STORAGERACKS)));

        for (final WoodType woodType : WoodType.values())
        {
            final List<RegistryObject<RackBlock>> list = new ArrayList<>();
            for (final FrameType frame : FrameType.values())
            {
                list.add(register(woodType.getSerializedName() + "_" + frame.getSerializedName() + "_rack", () -> new RackBlock(woodType, frame, frame.getUpgradeCost()), b -> new BlockItem(b, new Item.Properties().tab(ModCreativeTabs.STORAGERACKS))));
            }
            racks.addAll(list);
        }

        for (final WoodType woodType : WoodType.values())
        {
            final List<RegistryObject<CornerBlock>> list = new ArrayList<>();
            for (final FrameType frame : FrameType.values())
            {
                list.add(register(woodType.getSerializedName() + "_" + frame.getSerializedName() + "_corner", () -> new CornerBlock(woodType, frame), b -> new BlockItem(b, new Item.Properties().tab(ModCreativeTabs.STORAGERACKS))));
            }
            corners.addAll(list);
        }
    }

    /**
     * Private constructor to hide the implicit public one.
     */
    private ModBlocks()
    {

    }
}
