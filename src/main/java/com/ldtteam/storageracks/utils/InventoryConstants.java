package com.ldtteam.storageracks.utils;

/**
 * Some constants needed for the whole mod.
 */
public final class InventoryConstants
{
    /**
     * Amount of columns in the player inventory.
     */
    public static final int INVENTORY_COLUMNS = 9;

    /**
     * Initial x-offset of the inventory slot.
     */
    public static final int PLAYER_INVENTORY_INITIAL_X_OFFSET = 8;

    /**
     * Initial y-offset of the inventory slot.
     */
    public static final int PLAYER_INVENTORY_INITIAL_Y_OFFSET = 30;

    /**
     * Each offset of the inventory slots.
     */
    public static final int PLAYER_INVENTORY_OFFSET_EACH = 18;

    /**
     * Initial y-offset of the inventory slots in the hotbar.
     */
    public static final int PLAYER_INVENTORY_HOTBAR_OFFSET = 88;

    /**
     * Amount of rows in the player inventory.
     */
    public static final int INVENTORY_ROWS = 3;

    /**
     * The size of the the inventory hotbar.
     */
    public static final int INVENTORY_BAR_SIZE = 8;

    /**
     * Private constructor to hide implicit public one.
     */
    private InventoryConstants()
    {
        /*
         * Intentionally left empty.
         */
    }
}
