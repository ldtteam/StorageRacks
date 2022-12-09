package com.ldtteam.storageracks.datagen;

import com.google.gson.JsonElement;
import com.ldtteam.datagenerators.models.block.BlockModelJson;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.blocks.RackType;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

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
                models.add(new Tuple<>(modelJson, name));
            }
        }
        return generateAll(cache);
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache)
    {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[this.models.size()];
        int i = 0;

        for (Tuple<BlockModelJson, String> model : this.models)
        {
            Path target = getPath(model.getB());
            futures[i++] = DataProvider.saveStable(cache, model.getA().serialize(), target);
        }

        return CompletableFuture.allOf(futures);
    }

    protected Path getPath(final String name)
    {
        return this.generator.getPackOutput()
                 .getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                 .resolve(Constants.MOD_ID)
                 .resolve(Constants.BRICK_BLOCK_MODELS_DIR)
                 .resolve(name + ".json");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Block Model Provider";
    }
}
