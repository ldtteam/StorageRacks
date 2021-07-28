package com.ldtteam.storageracks.utils;

/**
 * Class which contains all constants required for windows.
 */
public final class WindowConstants
{
    /**
     * Id of the page view in the GUI.
     */
    public static final String VIEW_PAGES = "pages";

    public static final String RESOURCE_ICON             = "resourceIcon";

    /**
     * Window all Items list gui file.
     */
    public static final String HUT_ALL_INVENTORY_SUFFIX = ":gui/windowallinventory.xml";
    /**
     * No Sorting stage. how it comes from Database so it gets feeded
     */
    public static final int    NO_SORT                  = 0;
    /**
     * Name Ascending
     */
    public static final int    ASC_SORT                 = 1;
    /**
     * Name Descending
     */
    public static final int    DESC_SORT                = 2;
    /**
     * Itemcount Ascending
     */
    public static final int    COUNT_ASC_SORT           = 3;
    /**
     * Itemcount Descending
     */
    public static final int    COUNT_DESC_SORT          = 4;
    /**
     * The Stringdefine for the GUI page
     */
    public static final String LIST_ALLINVENTORY        = "allinventorylist";
    /**
     * The Sort Button
     */
    public static final String BUTTON_SORT              = "sortStorageFilter";

    public static final String BUTTON_PREVPAGE     = "prevPage";
    public static final String BUTTON_NEXTPAGE     = "nextPage";

    public static final String LABEL_PAGE_NUMBER = "pageNum";
    public static final String LOCATE = "locate";
    public static final String SORT = "sort";
    public static final String INSERT = "insert";

    /**
     * Private constructor to hide implicit public one.
     */
    private WindowConstants()
    {
        /*
         * Intentionally left empty.
         */
    }
}
