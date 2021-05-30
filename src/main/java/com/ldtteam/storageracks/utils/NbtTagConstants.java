package com.ldtteam.storageracks.utils;

/**
 * Some constants needed to store things to NBT.
 */
public final class NbtTagConstants
{
    /**
     *  Position tag.
     */
    public static final String TAG_POS                    = "pos";

    /**
     * Tag to store the insert unlock flag.
     */
    public static final String TAG_INSERT = "insert";

    /**
     * Tag to store the sort unlock flag.
     */
    public static final String TAG_SORT = "sort";

    /**
     * Tag to store the inventory to nbt.
     */
    public static final String TAG_INVENTORY = "inventory";

    /**
     * Tag used to store the size.
     */
    public static final String TAG_SIZE = "tagSIze";

    /**
     * Tag to store an empty stack to nbt.
     */
    public static final String TAG_EMPTY = "empty";

    /**
     * Private constructor to hide the implicit one.
     */
    private NbtTagConstants()
    {
        /*
         * Intentionally left empty.
         */
    }
}
