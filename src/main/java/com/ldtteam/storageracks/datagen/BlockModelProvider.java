package com.ldtteam.storageracks.datagen;

import com.google.gson.JsonObject;
import com.ldtteam.datagenerators.models.block.BlockModelJson;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.RackType;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;

public class BlockModelProvider implements DataProvider
{
    private final DataGenerator generator;

    public BlockModelProvider(final DataGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public void run(@NotNull CachedOutput cache) throws IOException
    {
        for (final RegistryObject<CornerBlock> state : ModBlocks.corners)
        {
            final BlockModelJson modelJson = new BlockModelJson();
            modelJson.setParent("storageracks:block/corner");

            final HashMap<String, String> textureMap = new HashMap<>();
            textureMap.put("0", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());
            textureMap.put("1", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getFrameType().getMaterial()).getPath());
            textureMap.put("particle", "block/" +  ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());

            modelJson.setTextures(textureMap);

            final String name = state.get().getWoodType().getSerializedName().toLowerCase(Locale.ROOT) + "_" + state.get().getFrameType().getSerializedName().toLowerCase(Locale.ROOT) + "_corner.json";
            final Path saveFile = this.generator.getOutputFolder().resolve(Constants.BRICK_BLOCK_MODELS_DIR).resolve(name);

            DataProvider.saveStable(cache, modelJson.serialize(), saveFile);
        }

        for (final RegistryObject<RackBlock> state : ModBlocks.racks)
        {
            for (final RackType type : RackType.values())
            {
                final BlockModelJson modelJson = new BlockModelJson();
                modelJson.setParent("storageracks:block/" + "rack" + type.getName());

                final HashMap<String, String> textureMap = new HashMap<>();
                textureMap.put("0", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());
                textureMap.put("1", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getFrameType().getMaterial()).getPath());
                textureMap.put("particle", "block/" +  ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());

                modelJson.setTextures(textureMap);

                final String name = "special/" + state.get().getWoodType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + state.get().getFrameType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + "rack" + type.getName() + ".json";
                final Path saveFile = this.generator.getOutputFolder().resolve(Constants.BRICK_BLOCK_MODELS_DIR).resolve(name);
                DataProvider.saveStable(cache, modelJson.serialize(), saveFile);
            }
        }

        for (final RegistryObject<RackBlock> state : ModBlocks.racks)
        {
            for (final RackType type : RackType.values())
            {
                final BlockModelJson modelJson = new BlockModelJson();
                final String parentName = state.get().getWoodType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + state.get().getFrameType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + "rack" + type.getName();
                modelJson.setParent("storageracks:block/special/" + parentName);

                final String name = state.get().getWoodType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + state.get().getFrameType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + "rack" + type.getName() + ".json";
                final Path saveFile = this.generator.getOutputFolder().resolve(Constants.BRICK_BLOCK_MODELS_DIR).resolve(name);

                JsonObject jsonObject = modelJson.serialize().getAsJsonObject();
                jsonObject.addProperty("loader", "domum_ornamentum:materially_textured");

                DataProvider.saveStable(cache, jsonObject, saveFile);
            }
        }
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Block Model Provider";
    }
}
