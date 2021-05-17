package com.ldtteam.storageracks;

import com.ldtteam.storageracks.blocks.ModBlocks;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * Class used to handle the creativeTab of storageracks.
 */
public final class ModCreativeTabs
{
    public static final ItemGroup STORAGERACKS = new ItemGroup(Constants.MOD_ID)
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ModBlocks.racks.get(0));
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
