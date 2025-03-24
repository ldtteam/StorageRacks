package com.ldtteam.storageracks.gui;

import com.ldtteam.blockui.Color;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.controls.*;
import com.ldtteam.blockui.views.BOWindow;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.storageracks.HighlightManager;
import com.ldtteam.storageracks.ItemStorage;
import com.ldtteam.storageracks.network.OpenInventoryMessage;
import com.ldtteam.storageracks.network.SortControllerMessage;
import com.ldtteam.storageracks.network.UnlockInsertMessage;
import com.ldtteam.storageracks.network.UnlockSortMessage;
import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.tileentities.TileEntityRack;
import com.ldtteam.storageracks.utils.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static com.ldtteam.storageracks.utils.WindowConstants.*;

/**
 * Window for a hut name entry.
 */
public class WindowHutAllInventory extends BOWindow implements ButtonHandler
{
    /**
     * Red and green colors for in world highlights
     */
    private static final int RED   = Color.rgbaToInt(240, 150, 135, 255);
    private static final int GREEN = Color.rgbaToInt(85, 255, 255, 255);

    /**
     * The formatting suffixes for numeric values.
     */
    private static final NavigableMap<Integer, String> SUFFIXES = new TreeMap<>(Map.ofEntries(Map.entry(1000, "k"), Map.entry(1000000, "M"), Map.entry(1000000000, "G")));

    /**
     * Comparator functions used in sorting.
     */
    private static final Comparator<ItemStorage> COMPARE_BY_NAME  = Comparator.comparing((o) -> o.getItemStack().getDisplayName().getString());
    private static final Comparator<ItemStorage> COMPARE_BY_COUNT = Comparator.comparingInt(ItemStorage::getAmount);

    /**
     * Filter methods used for filtering.
     */
    private static final BiPredicate<String, ItemStorage> FILTER_DISPLAY_NAME  =
        (filter, stack) -> StringUtils.containsIgnoreCase(stack.getItemStack().getDisplayName().getString(), filter);
    private static final BiPredicate<String, ItemStorage> FILTER_TOOLTIP_LINES = (filter, stack) -> StringUtils.containsIgnoreCase(stack.getItemStack()
        .getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.Default.NORMAL)
        .stream()
        .map(Component::getString)
        .collect(Collectors.joining(" ")), filter);

    /**
     * Translatable constants.
     */
    private static final String TEXT_SORT            = "gui.storageracks.sort";
    private static final String TEXT_SORT_UNLOCK     = "gui.storageracks.sort.unlock";
    private static final String TEXT_INSERT          = "gui.storageracks.insert";
    private static final String TEXT_INSERT_UNLOCK   = "gui.storageracks.insert.unlock";
    private static final String TEXT_ITEMS_AVAILABLE = "gui.storage.racks.available";
    private static final String TEXT_ITEMS_MISSING   = "gui.storage.racks.missing";

    /**
     * List of all item stacks in the warehouse.
     */
    private final List<ItemStorage> allItems = new ArrayList<>();

    /**
     * The owner controller.
     */
    private final TileEntityController controller;

    /**
     * Resource scrolling list.
     */
    private final ScrollingList stackList;

    /**
     * The sortDescriptor so how we want to sort
     */
    private SortDescriptor sortDescriptor = SortDescriptor.ALPHABETICAL_ASC;

    /**
     * Constructor for a hut inv display window.
     */
    public WindowHutAllInventory(final TileEntityController controller)
    {
        super(new ResourceLocation(Constants.MOD_ID, HUT_ALL_INVENTORY_SUFFIX));
        this.controller = controller;
        this.stackList = findPaneOfTypeByID(LIST_ALL_INVENTORY, ScrollingList.class);
        this.stackList.setDataProvider(new ScrollingList.DataProvider()
        {
            @Override
            public int getElementCount()
            {
                return allItems.size();
            }

            @Override
            public boolean shouldUpdate()
            {
                return false;
            }

            @Override
            public void updateElement(final int index, @NotNull final Pane rowPane)
            {
                final ItemStorage resource = allItems.get(index);
                rowPane.findPaneOfTypeByID(ITEM_ICON_RESOURCE_ICON, ItemIcon.class).setItem(resource.getItemStack().copyWithCount(1));
                final String text = resource.getItemStack().getDisplayName().getString().replace("[", "").replace("]", "");
                final String filter = getFilter();
                if (filter.isBlank())
                {
                    rowPane.findPaneOfTypeByID(LABEL_RESOURCE_NAME, Text.class).setText(Component.literal(text));
                }
                else
                {
                    final int startIndex = StringUtils.indexOfIgnoreCase(text, filter);
                    final int endIndex = startIndex + filter.length();

                    final MutableComponent first = Component.literal(text.substring(0, startIndex)).setStyle(Style.EMPTY.withColor(ChatFormatting.BLACK));
                    final MutableComponent middle = Component.literal(text.substring(startIndex, endIndex)).setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
                    final MutableComponent last = Component.literal(text.substring(endIndex)).setStyle(Style.EMPTY.withColor(ChatFormatting.BLACK));
                    rowPane.findPaneOfTypeByID(LABEL_RESOURCE_NAME, Text.class).setText(first.append(middle).append(last));
                }

                final Text quantityText = rowPane.findPaneOfTypeByID(LABEL_RESOURCE_QUANTITY, Text.class);
                if (Screen.hasShiftDown())
                {
                    quantityText.setText(Component.translatable(Integer.toString(resource.getAmount())));
                }
                else
                {
                    if (resource.getAmount() < 1000)
                    {
                        quantityText.setText(Component.literal(Long.toString(resource.getAmount())));
                    }
                    else
                    {
                        final Map.Entry<Integer, String> suffix = SUFFIXES.floorEntry(resource.getAmount());
                        quantityText.setText(Component.literal(Math.floor((double) resource.getAmount() / suffix.getKey() * 10) / 10d + suffix.getValue()));
                    }
                }
            }
        });

        updateSwitchSortButton();

        findPaneOfTypeByID(BUTTON_SORT, ButtonImage.class).setText(Component.translatable(controller.isSortUnlocked() ? TEXT_SORT : TEXT_SORT_UNLOCK));
        findPaneOfTypeByID(BUTTON_INSERT, ButtonImage.class).setText(Component.translatable(controller.isInsertUnlocked() ? TEXT_INSERT : TEXT_INSERT_UNLOCK));

        final ItemIcon sortIcon = findPaneOfTypeByID(ITEM_ICON_SORT_COST, ItemIcon.class);
        sortIcon.setVisible(!controller.isSortUnlocked());
        if (!controller.isSortUnlocked())
        {
            sortIcon.setItem(new ItemStack(Items.REDSTONE_BLOCK, 1));
            final boolean hasItem = Minecraft.getInstance().player.getInventory().contains(sortIcon.getItem());
            PaneBuilders.tooltipBuilder()
                .hoverPane(sortIcon)
                .paragraphBreak()
                .append(Component.translatable(hasItem ? TEXT_ITEMS_AVAILABLE : TEXT_ITEMS_MISSING))
                .color(hasItem ? GREEN : RED)
                .build();
        }

        final ItemIcon insertIcon = findPaneOfTypeByID(ITEM_ICON_INSERT_COST, ItemIcon.class);
        insertIcon.setVisible(!controller.isInsertUnlocked());
        if (!controller.isInsertUnlocked())
        {
            insertIcon.setItem(new ItemStack(Items.HOPPER, 1));
            final boolean hasItem = Minecraft.getInstance().player.getInventory().contains(insertIcon.getItem());
            PaneBuilders.tooltipBuilder()
                .hoverPane(insertIcon)
                .paragraphBreak()
                .append(Component.translatable(hasItem ? TEXT_ITEMS_AVAILABLE : TEXT_ITEMS_MISSING))
                .color(hasItem ? GREEN : RED)
                .build();
        }
    }

    /**
     * Get the current filter text.
     *
     * @return the filter text.
     */
    private String getFilter()
    {
        return findPaneOfTypeByID("names", TextField.class).getText();
    }

    /**
     * Update the switch sort button.
     */
    private void updateSwitchSortButton()
    {
        final Button switchSortButton = findPaneOfTypeByID(BUTTON_SWITCH_SORT, Button.class);
        switchSortButton.setText(Component.literal(sortDescriptor.symbol));
        PaneBuilders.tooltipBuilder().hoverPane(switchSortButton).append(Component.translatable(sortDescriptor.hoverText)).build();
    }

    @Override
    public boolean onKeyTyped(final char ch, final int key)
    {
        final boolean result = super.onKeyTyped(ch, key);
        if (result)
        {
            updateResources();
        }
        return result;
    }

    @Override
    public void onOpened()
    {
        updateResources();
    }

    /**
     * Update the item list.
     */
    private void updateResources()
    {
        final Set<BlockPos> containerList = new HashSet<>(controller.racks);

        final Map<ItemStorage, Integer> storedItems = new HashMap<>();
        final Level level = Minecraft.getInstance().level;

        for (final BlockPos blockPos : containerList)
        {
            final BlockEntity rack = level.getBlockEntity(blockPos);
            if (rack instanceof TileEntityRack tileEntityRack)
            {
                final Map<ItemStorage, Integer> rackStorage = tileEntityRack.getAllContent();

                for (final Map.Entry<ItemStorage, Integer> entry : rackStorage.entrySet())
                {
                    if (storedItems.containsKey(entry.getKey()))
                    {
                        storedItems.put(entry.getKey(), storedItems.get(entry.getKey()) + entry.getValue());
                    }
                    else
                    {
                        storedItems.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        final List<ItemStorage> filterItems = new ArrayList<>();
        storedItems.forEach((storage, amount) -> {
            storage.setAmount(amount);
            filterItems.add(storage);
        });
        final String filter = getFilter();

        allItems.clear();
        if (filter.isBlank())
        {
            allItems.addAll(filterItems);
        }
        else
        {
            allItems.addAll(filterItems.stream().filter(stack -> FILTER_DISPLAY_NAME.test(filter, stack)).filter(stack -> FILTER_TOOLTIP_LINES.test(filter, stack)).toList());
        }

        allItems.sort(sortDescriptor.comparator);

        stackList.refreshElementPanes();
    }

    @Override
    public void onButtonClicked(final Button button)
    {
        switch (button.getID())
        {
            case BUTTON_SWITCH_SORT -> switchSort();
            case BUTTON_LOCATE -> locate(button);
            case BUTTON_SORT -> sort();
            case BUTTON_INSERT -> insert();
        }
    }

    /**
     * Switch the sort value for the next one.
     */
    private void switchSort()
    {
        sortDescriptor = sortDescriptor.getNext();
        updateSwitchSortButton();
        updateResources();
    }

    /**
     * Locate all the racks given a specific item.
     *
     * @param button the button which was clicked.
     */
    private void locate(final Button button)
    {
        final int row = stackList.getListElementIndexByPane(button);
        final ItemStorage storage = allItems.get(row);
        final Set<BlockPos> containerList = new HashSet<>(controller.racks);
        HighlightManager.clearCategory("inventoryHighlight");

        Minecraft.getInstance().player.displayClientMessage(Component.translatable("gui.storageracks.locating"), false);
        close();

        for (BlockPos blockPos : containerList)
        {
            final BlockEntity rack = Minecraft.getInstance().level.getBlockEntity(blockPos);
            if (rack instanceof TileEntityRack tileEntityRack)
            {
                int count = tileEntityRack.getCount(storage.getItemStack());
                if (count > 0)
                {
                    // Varies the color between yellow(low count) to green(64+)
                    final int color = 0x00FF00 + 0xFF0000 * Math.max(0, 1 - count / 64);
                    HighlightManager.addRenderBox("inventoryHighlight",
                        new HighlightManager.TimedBoxRenderData().setPos(blockPos)
                            .setRemovalTimePoint(Minecraft.getInstance().level.getGameTime() + 60 * 20)
                            .addText("" + count)
                            .setColor(color));
                }
            }
        }
    }

    /**
     * If not yet unlocked, try to spend the player res and unlock. Else Sends a message to the server side to sort all the inventories.
     */
    private void sort()
    {
        if (controller.isSortUnlocked())
        {
            new SortControllerMessage(this.controller.getBlockPos()).sendToServer();
        }
        else
        {
            new UnlockSortMessage(this.controller.getBlockPos()).sendToServer();
            close();
        }
    }

    /**
     * If not yet unlocked, try to spend the player res and unlock. Else Open the insert window.
     */
    private void insert()
    {
        if (controller.isInsertUnlocked())
        {
            new OpenInventoryMessage(this.controller.getBlockPos()).sendToServer();
        }
        else
        {
            new UnlockInsertMessage(this.controller.getBlockPos()).sendToServer();
            close();
        }
    }

    /**
     * Sorting descriptor used in the item list.
     */
    private enum SortDescriptor
    {
        ALPHABETICAL_ASC(0, COMPARE_BY_NAME, "A↑", "gui.storageracks.sort.alphabetical.asc"),
        ALPHABETICAL_DESC(1, COMPARE_BY_NAME.reversed(), "A↓", "gui.storageracks.sort.alphabetical.desc"),
        COUNT_ASC(2, COMPARE_BY_COUNT.thenComparing(COMPARE_BY_NAME), "x↑", "gui.storageracks.sort.count.asc"),
        COUNT_DESC(3, COMPARE_BY_COUNT.reversed().thenComparing(COMPARE_BY_NAME), "x↓", "gui.storageracks.sort.count.desc");

        /**
         * The index of the sort descriptor.
         */
        private final int index;

        /**
         * The comparator to use to sort the items.
         */
        private final Comparator<ItemStorage> comparator;

        /**
         * The symbol to display on the sort button for the current sorting descriptor.
         */
        private final String symbol;

        /**
         * The text shown in a tooltip when hovering over the sort button.
         */
        private final String hoverText;

        /**
         * Internal constructor.
         */
        SortDescriptor(final int index, final Comparator<ItemStorage> comparator, final String symbol, final String hoverText)
        {
            this.index = index;
            this.comparator = comparator;
            this.symbol = symbol;
            this.hoverText = hoverText;
        }

        /**
         * Get the next sort descriptor after the current one.
         *
         * @return the next enum value.
         */
        public SortDescriptor getNext()
        {
            final int max = SortDescriptor.values().length;
            final int next = index + 1 == max ? 0 : index + 1;
            return Arrays.stream(SortDescriptor.values()).filter(desc -> desc.index == next).findFirst().orElse(ALPHABETICAL_ASC);
        }
    }
}
