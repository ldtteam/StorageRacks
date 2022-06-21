package com.ldtteam.storageracks.utils;

public class Constants
{
    /**
     * The mod id.
     */
    public static final String MOD_ID = "storageracks";

    /**
     * Default size of the inventory.
     */
    public static final int DEFAULT_SIZE = 18;

    /**
     * Slots per line.
     */
    public static final int SLOT_PER_LINE = 9;

    /**
     * Datagen folders.
     */
    private static final String DATAPACK_DIR = "data/" + Constants.MOD_ID + "/";
    private static final String RESOURCEPACK_DIR = "assets/" + Constants.MOD_ID + "/";
    public static final String ITEM_MODEL_DIR = RESOURCEPACK_DIR + "/models/item/";
    public static final String BLOCKSTATE_DIR = RESOURCEPACK_DIR + "blockstates/";
    public static final String RECIPES_DIR = DATAPACK_DIR + "/recipes/";
    public static final String BRICK_BLOCK_MODELS_DIR = RESOURCEPACK_DIR + "models/block/";
    public static final String EN_US_LANG = RESOURCEPACK_DIR +  "/lang/en_us.json";
    public static final String LOOT_TABLES_DIR = DATAPACK_DIR + "loot_tables/blocks";
}
