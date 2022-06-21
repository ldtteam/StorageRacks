package com.ldtteam.storageracks.datagen;

import com.ldtteam.datagenerators.recipes.RecipeIngredientJson;
import com.ldtteam.datagenerators.recipes.RecipeIngredientKeyJson;
import com.ldtteam.datagenerators.recipes.RecipeResultJson;
import com.ldtteam.datagenerators.recipes.shaped.ShapedPatternJson;
import com.ldtteam.datagenerators.recipes.shaped.ShapedRecipeJson;
import com.ldtteam.storageracks.blocks.CornerBlock;
import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.blocks.RackBlock;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RecipeProvider implements DataProvider
{
    private final DataGenerator generator;

    public RecipeProvider(DataGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public void run(@NotNull final CachedOutput cache) throws IOException
    {
        for (final RegistryObject<RackBlock> state : ModBlocks.racks)
        {
            final ShapedPatternJson pattern =  new ShapedPatternJson("FFF","XCX","FFF");
            final Map<String, RecipeIngredientKeyJson> keys = new HashMap<>();
            keys.put("F", new RecipeIngredientKeyJson(new RecipeIngredientJson(ForgeRegistries.ITEMS.getKey((state.get().getWoodType().getMaterial().asItem())).toString(), false)));
            keys.put("C", new RecipeIngredientKeyJson(new RecipeIngredientJson(ForgeRegistries.ITEMS.getKey(state.get().frameType.getCreationCost()).toString(), false)));
            keys.put("X", new RecipeIngredientKeyJson(new RecipeIngredientJson(Tags.Items.RODS_WOODEN.location().toString(), true)));

            final ShapedRecipeJson json = new ShapedRecipeJson("racks", pattern, keys, new RecipeResultJson(1, ForgeRegistries.ITEMS.getKey(state.get().asItem()).toString()));
            final Path recipeFolder = this.generator.getOutputFolder().resolve(Constants.RECIPES_DIR);
            final Path blockstatePath = recipeFolder.resolve(ForgeRegistries.BLOCKS.getKey(state.get()).getPath() + ".json");

            DataProvider.saveStable(cache, json.serialize(), blockstatePath);
        }

        for (final RegistryObject<CornerBlock> state : ModBlocks.corners)
        {
            final ShapedPatternJson pattern =  new ShapedPatternJson("FFF","FCF","FFF");
            final Map<String, RecipeIngredientKeyJson> keys = new HashMap<>();
            keys.put("F", new RecipeIngredientKeyJson(new RecipeIngredientJson(ForgeRegistries.ITEMS.getKey(state.get().getWoodType().getMaterial().asItem()).toString(), false)));
            keys.put("C", new RecipeIngredientKeyJson(new RecipeIngredientJson(ForgeRegistries.ITEMS.getKey(state.get().frameType.getCreationCost()).toString(), false)));

            final ShapedRecipeJson json = new ShapedRecipeJson("corners", pattern, keys, new RecipeResultJson(8, ForgeRegistries.ITEMS.getKey(state.get().asItem()).toString()));
            final Path recipeFolder = this.generator.getOutputFolder().resolve(Constants.RECIPES_DIR);
            final Path blockstatePath = recipeFolder.resolve(ForgeRegistries.BLOCKS.getKey(state.get()).getPath() + ".json");

            DataProvider.saveStable(cache, json.serialize(), blockstatePath);
        }
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Brick Blocks Recipe Provider";
    }
}
