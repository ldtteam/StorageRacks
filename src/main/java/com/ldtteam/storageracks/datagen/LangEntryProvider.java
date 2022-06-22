package com.ldtteam.storageracks.datagen;

import com.ldtteam.datagenerators.lang.LangJson;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

public class LangEntryProvider implements DataProvider
{
    private final DataGenerator dataGenerator;
    private final LangJson      backingLangJson;

    public LangEntryProvider(final DataGenerator dataGenerator, LangJson backingLangJson)
    {
        this.dataGenerator = dataGenerator;
        this.backingLangJson = backingLangJson;
    }

    @Override
    public void run(@NotNull CachedOutput cache) throws IOException
    {
        for (final RegistryObject<CornerBlock> corner : ModBlocks.corners)
        {
            backingLangJson.put("block." + Constants.MOD_ID + "." + corner.getKey().location().getPath(),
              corner.get().getWoodType().getSerializedName().substring(0, 1).toUpperCase(Locale.US) + corner.get().getWoodType().getSerializedName().substring(1) + " "
                + corner.get().frameType.getSerializedName().substring(0, 1).toUpperCase(Locale.US) + corner.get().frameType.getSerializedName().substring(1) + " Corner");
        }

        for (final RegistryObject<RackBlock> rack : ModBlocks.racks)
        {
            backingLangJson.put("block." + Constants.MOD_ID + "." + rack.getKey().location().getPath(),
              rack.get().getWoodType().getSerializedName().substring(0, 1).toUpperCase(Locale.US) + rack.get().getWoodType().getSerializedName().substring(1) + " "
                + rack.get().frameType.getSerializedName().substring(0, 1).toUpperCase(Locale.US) + rack.get().frameType.getSerializedName().substring(1) + " Rack");
        }

        backingLangJson.put("block.storageracks.stone_controller", "Stone Controller");
        backingLangJson.put("block.storageracks.iron_controller", "Iron Controller");
        backingLangJson.put("block.storageracks.gold_controller", "Gold Controller");
        backingLangJson.put("block.storageracks.emerald_controller", "Emerald Controller");
        backingLangJson.put("block.storageracks.diamond_controller", "Diamond Controller");
        backingLangJson.put("gui.storageracks.notconnected",
          "Invalid Placement. Racks must be placed directly connected to a Controller or Rack but without connection to multiple Controllers.");
        backingLangJson.put("gui.storageracks.limitreached", "Max Rack limit reached for this Controller. Upgrade the Controller to connect more Racks!");
        backingLangJson.put("gui.storageracks.doublecontroller", "There can only be 1 Controller per Network!");
        backingLangJson.put("gui.storageracks.allinventory", "Storage");
        backingLangJson.put("gui.storageracks.locating", "Locating...");
        backingLangJson.put("block.storageracks.controllertoolip", "Supports up to %d Racks");
        backingLangJson.put("com.storageracks.sort.unlock.failed", "Unlocking Sort Failed. You are missing 1 RedStone Block in your Inventory!");
        backingLangJson.put("com.storageracks.insert.unlock.failed", "Unlocking Insert Failed. You are missing 1 Hopper in your Inventory!");
        backingLangJson.put("com.storageracks.sort.unlock.succeeded", "Successfully unlocked Sort Feature");
        backingLangJson.put("com.storageracks.insert.unlock.succeeded", "Successfully unlocked Insert Feature");
        backingLangJson.put("gui.storageracks.sort.unlock", "Unlock Sorting");
        backingLangJson.put("gui.storageracks.insert.unlock", "Unlock Insertion");
        backingLangJson.put("gui.storageracks.sort", "Sort");
        backingLangJson.put("gui.storageracks.insert", "Insert");
        backingLangJson.put("gui.storage.racks.missing", "Missing Required Item");
        backingLangJson.put("gui.storage.racks.available", "Available in Inventory");
        backingLangJson.put("container.title.rack", "Rack");
        backingLangJson.put("container.title.insertion", "Insertion Controller");

        DataProvider.saveStable(cache, backingLangJson.serialize(), dataGenerator.getOutputFolder().resolve(Constants.EN_US_LANG));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Lang Provider";
    }
}
