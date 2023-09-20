package com.ldtteam.storageracks.datagen;

import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.ldtteam.storageracks.utils.Constants.MOD_ID;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public class DefaultBlockTagsProvider extends BlockTagsProvider
{
    public DefaultBlockTagsProvider(
      @NotNull final DataGenerator generator,
      @Nullable final ExistingFileHelper existingFileHelper)
    {
        super(generator, MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
          .add(ModBlocks.stoneController.get())
          .add(ModBlocks.ironController.get())
          .add(ModBlocks.goldController.get())
          .add(ModBlocks.emeraldController.get())
          .add(ModBlocks.diamondController.get());

        final TagAppender<Block> axeTagAppender = tag(BlockTags.MINEABLE_WITH_AXE);
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
