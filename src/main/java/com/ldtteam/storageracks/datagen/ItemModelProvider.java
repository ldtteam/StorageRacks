package com.ldtteam.storageracks.datagen;

import com.ldtteam.datagenerators.models.item.ItemModelJson;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemModelProvider implements DataProvider
{
    private final DataGenerator generator;

    public ItemModelProvider(final DataGenerator generator)
    {
        this.generator = generator;
    }

    /**
     * All generated models.
     */
    private final List<Tuple<ItemModelJson, String>> models = new ArrayList<>();

    @Override
    public CompletableFuture<?> run(@NotNull CachedOutput cache)
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

            this.models.add(new Tuple<>(modelJson, ForgeRegistries.BLOCKS.getKey(state.get()).getPath()));
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

            this.models.add(new Tuple<>(modelJson, ForgeRegistries.BLOCKS.getKey(state.get()).getPath()));
        }
        return generateAll(cache);
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache)
    {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[this.models.size()];
        int i = 0;

        for (Tuple<ItemModelJson, String> model : this.models)
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
                 .resolve(Constants.ITEM_MODEL_DIR)
                 .resolve(name + ".json");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Brick Item Model Provider";
    }
}
