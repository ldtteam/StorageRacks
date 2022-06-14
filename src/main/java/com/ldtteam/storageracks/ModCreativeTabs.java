package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to handle the creativeTab of storageracks.
 */
public final class ModCreativeTabs
{
    public static final CreativeModeTab STORAGERACKS = new CreativeModeTab(Constants.MOD_ID)
    {
        @NotNull
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ModBlocks.racks.get(0).get());
        }

        @Override
        public boolean hasSearchBar()
        {
            return true;
        }
    };

    /**
     * Private constructor to hide the implicit one.
     */
    private ModCreativeTabs()
    {
        /*
         * Intentionally left empty.
         */
    }
}
