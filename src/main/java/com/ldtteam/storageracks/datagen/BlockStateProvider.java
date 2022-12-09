package com.ldtteam.storageracks.datagen;

import com.ldtteam.datagenerators.blockstate.BlockstateJson;
import com.ldtteam.datagenerators.blockstate.BlockstateModelJson;
import com.ldtteam.datagenerators.blockstate.BlockstateVariantJson;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BlockStateProvider implements DataProvider
{
    private final DataGenerator generator;

    public BlockStateProvider(DataGenerator generator)
    {
        this.generator = generator;
    }

    /**
     * All generated models.
     */
    private final List<Tuple<BlockstateJson, String>> models = new ArrayList<>();

    @Override
    public CompletableFuture<?> run(@NotNull final CachedOutput cache)
    {
        for (final RegistryObject<CornerBlock> state : ModBlocks.corners)
        {
            final Map<String, BlockstateVariantJson> variants = new HashMap<>();
            variants.put("", new BlockstateVariantJson(new BlockstateModelJson(Constants.MOD_ID + ":block/" + ForgeRegistries.BLOCKS.getKey(state.get()).getPath())));

            final BlockstateJson blockstate = new BlockstateJson(variants);
            models.add(new Tuple<>(blockstate, ForgeRegistries.BLOCKS.getKey(state.get()).getPath()));
        }

        for (final RegistryObject<RackBlock> state : ModBlocks.racks)
        {
            final Map<String, BlockstateVariantJson> variants = new HashMap<>();
            for (final RackType type : RackType.values())
            {
                variants.put("variant=" + type.getName(), new BlockstateVariantJson(new BlockstateModelJson(Constants.MOD_ID + ":block/" + ForgeRegistries.BLOCKS.getKey(state.get()).getPath() + type.getName())));
            }

            final BlockstateJson blockstate = new BlockstateJson(variants);

            models.add(new Tuple<>(blockstate, ForgeRegistries.BLOCKS.getKey(state.get()).getPath()));
        }
        return generateAll(cache);
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache)
    {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[this.models.size()];
        int i = 0;

        for (Tuple<BlockstateJson, String> model : this.models)
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
                 .resolve(Constants.BLOCKSTATE_DIR)
                 .resolve(name + ".json");
    }


    @NotNull
    @Override
    public String getName()
    {
        return "Brick BlockStates Provider";
    }
}
