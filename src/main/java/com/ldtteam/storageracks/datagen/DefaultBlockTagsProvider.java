package com.ldtteam.storageracks.datagen;

import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.ldtteam.storageracks.utils.Constants.MOD_ID;

@SuppressWarnings({"ConstantConditions"})
public class DefaultBlockTagsProvider extends BlockTagsProvider
{

    public DefaultBlockTagsProvider(
      final PackOutput output,
      final CompletableFuture<HolderLookup.Provider> lookupProvider,
      @Nullable final ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final HolderLookup.@NotNull Provider holder)
    {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
          .add(ModBlocks.stoneController.get())
          .add(ModBlocks.ironController.get())
          .add(ModBlocks.goldController.get())
          .add(ModBlocks.emeraldController.get())
          .add(ModBlocks.diamondController.get());

        final IntrinsicTagAppender<Block> axeTagAppender = tag(BlockTags.MINEABLE_WITH_AXE);
        for (RegistryObject<RackBlock> rack : ModBlocks.racks)
        {
            axeTagAppender.add(rack.get());
        }
        for (RegistryObject<CornerBlock> rack : ModBlocks.corners)
        {
            axeTagAppender.add(rack.get());
        }
    }
}
