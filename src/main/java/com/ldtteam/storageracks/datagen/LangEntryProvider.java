package com.ldtteam.storageracks.datagen;

import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Locale;

public class LangEntryProvider extends LanguageProvider
{
    public LangEntryProvider(PackOutput gen)
    {
        super(gen, "c", "en_us");
    }

    @Override
    protected void addTranslations()
    {
        for (final DeferredBlock<CornerBlock> corner : ModBlocks.corners)
        {
            add("block." + Constants.MOD_ID + "." + corner.getKey().location().getPath(),
              corner.get().getWoodType().getSerializedName().substring(0, 1).toUpperCase(Locale.US) + corner.get().getWoodType().getSerializedName().substring(1) + " "
                + corner.get().frameType.getSerializedName().substring(0, 1).toUpperCase(Locale.US) + corner.get().frameType.getSerializedName().substring(1) + " Corner");
        }

        for (final DeferredBlock<RackBlock> rack : ModBlocks.racks)
        {
            add("block." + Constants.MOD_ID + "." + rack.getKey().location().getPath(),
              rack.get().getWoodType().getSerializedName().substring(0, 1).toUpperCase(Locale.US) + rack.get().getWoodType().getSerializedName().substring(1) + " "
                + rack.get().frameType.getSerializedName().substring(0, 1).toUpperCase(Locale.US) + rack.get().frameType.getSerializedName().substring(1) + " Rack");
        }

        add("block.storageracks.stone_controller", "Stone Controller");
        add("block.storageracks.iron_controller", "Iron Controller");
        add("block.storageracks.gold_controller", "Gold Controller");
        add("block.storageracks.emerald_controller", "Emerald Controller");
        add("block.storageracks.diamond_controller", "Diamond Controller");
        add("block.storageracks.netherite_controller", "Netherite Controller");

        add("gui.storageracks.notconnected",
          "Invalid Placement. Racks must be placed directly connected to a Controller or Rack but without connection to multiple Controllers.");
        add("gui.storageracks.limitreached", "Max Rack limit reached for this Controller. Upgrade the Controller to connect more Racks!");
        add("gui.storageracks.doublecontroller", "There can only be 1 Controller per Network!");
        add("gui.storageracks.allinventory", "Storage");
        add("gui.storageracks.locating", "Locating...");
        add("block.storageracks.controllertoolip", "Supports up to %d Racks");
        add("com.storageracks.sort.unlock.failed", "Unlocking Sort Failed. You are missing 1 RedStone Block in your Inventory!");
        add("com.storageracks.insert.unlock.failed", "Unlocking Insert Failed. You are missing 1 Hopper in your Inventory!");
        add("com.storageracks.sort.unlock.succeeded", "Successfully unlocked Sort Feature");
        add("com.storageracks.insert.unlock.succeeded", "Successfully unlocked Insert Feature");
        add("gui.storageracks.sort.unlock", "Unlock Sorting");
        add("gui.storageracks.insert.unlock", "Unlock Insertion");
        add("gui.storageracks.sort", "Sort");
        add("gui.storageracks.insert", "Insert");
        add("gui.storage.racks.missing", "Missing Required Item");
        add("gui.storage.racks.available", "Available in Inventory");
        add("container.title.rack", "Rack");
        add("container.title.insertion", "Insertion Controller");
    }
}
