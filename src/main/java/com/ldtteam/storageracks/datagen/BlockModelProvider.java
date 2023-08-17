package com.ldtteam.storageracks.datagen;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.ldtteam.datagenerators.models.block.BlockModelJson;
import com.ldtteam.datagenerators.models.element.ModelElementJson;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.RackType;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class BlockModelProvider implements DataProvider
{
    private final DataGenerator generator;

    public BlockModelProvider(final DataGenerator generator)
    {
        this.generator = generator;
    }

    /**
     * All generated models.
     */
    private final List<Tuple<BlockModelJson, String>> models = new ArrayList<>();
    private final List<Tuple<BlockModelJson, String>> specialModels = new ArrayList<>();

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache)
    {
        for (final RegistryObject<CornerBlock> state : ModBlocks.corners)
        {
            final BlockModelJson modelJson = new BlockModelJson();
            modelJson.setParent("storageracks:block/corner");

            final HashMap<String, String> textureMap = new HashMap<>();
            textureMap.put("0", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());
            textureMap.put("1", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getFrameType().getMaterial()).getPath());
            textureMap.put("particle", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());

            modelJson.setTextures(textureMap);

            final String name =
              state.get().getWoodType().getSerializedName().toLowerCase(Locale.ROOT) + "_" + state.get().getFrameType().getSerializedName().toLowerCase(Locale.ROOT)
                + "_corner.json";
            models.add(new Tuple<>(modelJson, name));
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
                textureMap.put("particle", "block/" + ForgeRegistries.BLOCKS.getKey(state.get().getWoodType().getMaterial()).getPath());

                modelJson.setTextures(textureMap);

                final String name = state.get().getWoodType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + state.get().getFrameType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + "rack" + type.getName() + ".json";
                models.add(new Tuple<>(modelJson, "special/" + name));
            }
        }

        for (final RegistryObject<RackBlock> state : ModBlocks.racks)
        {
            for (final RackType type : RackType.values())
            {
                final BlockModelJson modelJson = new BlockModelJson();
                final String name = state.get().getWoodType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + state.get().getFrameType().getSerializedName().toLowerCase(Locale.ROOT)
                                      + "_" + "rack" + type.getName();

                modelJson.setParent("storageracks:block/special/" + name);


                specialModels.add(new Tuple<>(modelJson, name + ".json"));
            }
        }
        return generateAll(cache);
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache)
    {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[this.models.size() + this.specialModels.size()];
        int i = 0;

        for (Tuple<BlockModelJson, String> model : this.models)
        {
            Path target = getPath(model.getB());
            JsonObject obj = model.getA().serialize().getAsJsonObject();
            futures[i++] = DataProvider.saveStable(cache, obj, target);
        }

        for (Tuple<BlockModelJson, String> model : this.specialModels)
        {
            Path target = getPath(model.getB());
            JsonObject obj = model.getA().serialize().getAsJsonObject();
            obj.addProperty("loader", "domum_ornamentum:materially_textured");
            futures[i++] = DataProvider.saveStable(cache, obj, target);
        }

        return CompletableFuture.allOf(futures);
    }

    protected Path getPath(final String name)
    {
        return this.generator.getPackOutput()
                 .getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                 .resolve(Constants.MOD_ID)
                 .resolve(Constants.BRICK_BLOCK_MODELS_DIR)
                 .resolve(name);
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Block Model Provider";
    }
}
