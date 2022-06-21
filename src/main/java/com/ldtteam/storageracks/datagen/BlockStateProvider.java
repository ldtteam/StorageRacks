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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BlockStateProvider implements DataProvider
{
    private final DataGenerator generator;

    public BlockStateProvider(DataGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public void run(@NotNull final CachedOutput cache) throws IOException
    {
        for (final RegistryObject<CornerBlock> state : ModBlocks.corners)
        {
            final Map<String, BlockstateVariantJson> variants = new HashMap<>();
            variants.put("", new BlockstateVariantJson(new BlockstateModelJson(Constants.MOD_ID + ":block/" + ForgeRegistries.BLOCKS.getKey(state.get()).getPath())));

            final BlockstateJson blockstate = new BlockstateJson(variants);

            final Path blockstateFolder = this.generator.getOutputFolder().resolve(Constants.BLOCKSTATE_DIR);
            final Path blockstatePath = blockstateFolder.resolve(ForgeRegistries.BLOCKS.getKey(state.get()).getPath() + ".json");

            DataProvider.saveStable(cache, blockstate.serialize(), blockstatePath);
        }

        for (final RegistryObject<RackBlock> state : ModBlocks.racks)
        {
            final Map<String, BlockstateVariantJson> variants = new HashMap<>();
            for (final RackType type : RackType.values())
            {
                variants.put("variant=" + type.getName(), new BlockstateVariantJson(new BlockstateModelJson(Constants.MOD_ID + ":block/" + ForgeRegistries.BLOCKS.getKey(state.get()).getPath() + type.getName())));
            }

            final BlockstateJson blockstate = new BlockstateJson(variants);

            final Path blockstateFolder = this.generator.getOutputFolder().resolve(Constants.BLOCKSTATE_DIR);
            final Path blockstatePath = blockstateFolder.resolve(ForgeRegistries.BLOCKS.getKey(state.get()).getPath() + ".json");

            DataProvider.saveStable(cache, blockstate.serialize(), blockstatePath);
        }
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Brick BlockStates Provider";
    }
}
