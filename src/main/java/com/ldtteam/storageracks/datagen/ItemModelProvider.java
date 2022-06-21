package com.ldtteam.storageracks.datagen;

import com.ldtteam.datagenerators.models.item.ItemModelJson;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class ItemModelProvider implements DataProvider
{
    private final DataGenerator generator;

    public ItemModelProvider(final DataGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public void run(@NotNull CachedOutput cache) throws IOException
    {
        final ItemModelJson modelJson = new ItemModelJson();

        for (final RegistryObject<CornerBlock> state : ModBlocks.corners)
        {
            final String modelLocation = Constants.MOD_ID + ":item/corner";
            modelJson.setParent(modelLocation);

            final HashMap<String, String> textureMap = new HashMap<>();
            textureMap.put("0", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());
            textureMap.put("1", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getFrameType().getMaterial()).getPath());
            textureMap.put("particle", "block/" +  ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());
            modelJson.setTextures(textureMap);

            DataProvider.saveStable(
              cache,
              modelJson.serialize(),
              generator.getOutputFolder().resolve(Constants.ITEM_MODEL_DIR).resolve(ForgeRegistries.BLOCKS.getKey(state.get()).getPath() + ".json"));
        }

        for (final RegistryObject<RackBlock> state : ModBlocks.racks)
        {
            final String modelLocation = Constants.MOD_ID + ":item/rack";
            modelJson.setParent(modelLocation);

            final HashMap<String, String> textureMap = new HashMap<>();
            textureMap.put("0", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());
            textureMap.put("1", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getFrameType().getMaterial()).getPath());
            textureMap.put("particle", "block/" +  ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());
            modelJson.setTextures(textureMap);

            DataProvider.saveStable(
              cache,
              modelJson.serialize(),
              generator.getOutputFolder().resolve(Constants.ITEM_MODEL_DIR).resolve(ForgeRegistries.BLOCKS.getKey(state.get()).getPath() + ".json"));
        }
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Brick Item Model Provider";
    }
}
