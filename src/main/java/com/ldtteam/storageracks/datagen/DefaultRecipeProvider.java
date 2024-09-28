package com.ldtteam.storageracks.datagen;

import com.ldtteam.storageracks.blocks.ControllerBlock;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class DefaultRecipeProvider extends RecipeProvider
{
    public DefaultRecipeProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(@NotNull final RecipeOutput consumer)
    {
        for (final DeferredBlock<RackBlock> state : ModBlocks.racks)
        {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, state)
              .pattern("FFF")
              .pattern("XCX")
              .pattern("FFF")
              .define('F', state.get().getWoodType().getMaterial().asItem())
              .define('C', state.get().frameType.getCreationCost())
              .define('X', Tags.Items.RODS_WOODEN)
              .unlockedBy("has_stick", has(Items.STICK))
              .save(consumer);
        }

        for (final DeferredBlock<CornerBlock> state : ModBlocks.corners)
        {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, state)
              .pattern("FFF")
              .pattern("FCF")
              .pattern("FFF")
              .define('F', state.get().getWoodType().getMaterial().asItem())
              .define('C', state.get().frameType.getCreationCost())
              .unlockedBy("has_frame_material", has(state.get().frameType.getCreationCost()))
              .save(consumer);
        }

        generateControllerRecipe(consumer, ModBlocks.stoneController.get(), Items.PAPER);
        generateControllerRecipe(consumer, ModBlocks.ironController.get(), ModBlocks.stoneController.get().asItem());
        generateControllerRecipe(consumer, ModBlocks.goldController.get(), ModBlocks.ironController.get().asItem());
        generateControllerRecipe(consumer, ModBlocks.emeraldController.get(), ModBlocks.goldController.get().asItem());
        generateControllerRecipe(consumer, ModBlocks.diamondController.get(), ModBlocks.emeraldController.get().asItem());

    }

    private void generateControllerRecipe(final RecipeOutput consumer, final ControllerBlock state, final Item prev)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, state)
          .pattern("SSS")
          .pattern("SPS")
          .pattern("SSS")
          .define('S', state.getBuildMaterial())
          .define('P', prev)
          .unlockedBy("has_build_material", has(state.getBuildMaterial()))
          .save(consumer);
    }
}
