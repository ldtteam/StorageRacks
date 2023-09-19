package com.ldtteam.storageracks.datagen;

import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.DataGenerator;
import com.ldtteam.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Locale;

public class LangEntryProvider extends LanguageProvider
{

    public LangEntryProvider(final DataGenerator gen)
    {
        super(gen, Constants.MOD_ID, "en_us", List.of(new GlobalLanguageEntries()));
    }

    @Override
    public String getName()
    {
        return "Storage Rack Lang Provider";
    }

    private final static class GlobalLanguageEntries implements SubProvider
    {
        @Override
        public void addTranslations(LanguageAcceptor acceptor) {
            for (final RegistryObject<CornerBlock> corner : ModBlocks.corners)
            {
                acceptor.add("block." + Constants.MOD_ID + "." + corner.getKey().location().getPath(),
                  corner.get().getWoodType().getSerializedName().substring(0, 1).toUpperCase(Locale.US) + corner.get().getWoodType().getSerializedName().substring(1) + " "
                    + corner.get().frameType.getSerializedName().substring(0, 1).toUpperCase(Locale.US) + corner.get().frameType.getSerializedName().substring(1) + " Corner");
            }

            for (final RegistryObject<RackBlock> rack : ModBlocks.racks)
            {
                acceptor.add("block." + Constants.MOD_ID + "." + rack.getKey().location().getPath(),
                  rack.get().getWoodType().getSerializedName().substring(0, 1).toUpperCase(Locale.US) + rack.get().getWoodType().getSerializedName().substring(1) + " "
                    + rack.get().frameType.getSerializedName().substring(0, 1).toUpperCase(Locale.US) + rack.get().frameType.getSerializedName().substring(1) + " Rack");
            }

            acceptor.add("block.storageracks.stone_controller", "Stone Controller");
            acceptor.add("block.storageracks.iron_controller", "Iron Controller");
            acceptor.add("block.storageracks.gold_controller", "Gold Controller");
            acceptor.add("block.storageracks.emerald_controller", "Emerald Controller");
            acceptor.add("block.storageracks.diamond_controller", "Diamond Controller");
            acceptor.add("block.storageracks.netherite_controller", "Netherite Controller");

            acceptor.add("gui.storageracks.notconnected",
              "Invalid Placement. Racks must be placed directly connected to a Controller or Rack but without connection to multiple Controllers.");
            acceptor.add("gui.storageracks.limitreached", "Max Rack limit reached for this Controller. Upgrade the Controller to connect more Racks!");
            acceptor.add("gui.storageracks.doublecontroller", "There can only be 1 Controller per Network!");
            acceptor.add("gui.storageracks.allinventory", "Storage");
            acceptor.add("gui.storageracks.locating", "Locating...");
            acceptor.add("block.storageracks.controllertoolip", "Supports up to %d Racks");
            acceptor.add("com.storageracks.sort.unlock.failed", "Unlocking Sort Failed. You are missing 1 RedStone Block in your Inventory!");
            acceptor.add("com.storageracks.insert.unlock.failed", "Unlocking Insert Failed. You are missing 1 Hopper in your Inventory!");
            acceptor.add("com.storageracks.sort.unlock.succeeded", "Successfully unlocked Sort Feature");
            acceptor.add("com.storageracks.insert.unlock.succeeded", "Successfully unlocked Insert Feature");
            acceptor.add("gui.storageracks.sort.unlock", "Unlock Sorting");
            acceptor.add("gui.storageracks.insert.unlock", "Unlock Insertion");
            acceptor.add("gui.storageracks.sort", "Sort");
            acceptor.add("gui.storageracks.insert", "Insert");
            acceptor.add("gui.storage.racks.missing", "Missing Required Item");
            acceptor.add("gui.storage.racks.available", "Available in Inventory");
            acceptor.add("container.title.rack", "Rack");
            acceptor.add("container.title.insertion", "Insertion Controller");
        }
    }
}
