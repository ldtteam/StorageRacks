package com.ldtteam.storageracks.gui;

import com.ldtteam.blockui.Color;
import com.ldtteam.blockui.Pane;
import com.ldtteam.blockui.PaneBuilders;
import com.ldtteam.blockui.controls.*;
import com.ldtteam.blockui.views.ScrollingList;
import com.ldtteam.storageracks.*;
import com.ldtteam.storageracks.network.*;
import com.ldtteam.storageracks.tileentities.TileEntityController;
import com.ldtteam.storageracks.tileentities.TileEntityRack;
import com.ldtteam.storageracks.utils.Constants;
import com.ldtteam.storageracks.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Tuple;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ldtteam.storageracks.utils.WindowConstants.*;

/**
 * Window for a hut name entry.
 */
public class WindowHutAllInventory extends AbstractWindowSkeleton
{
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static
    {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static final int RED   = Color.rgbaToInt(240, 150, 135, 255);
    public static final int GREEN = Color.rgbaToInt(85, 255, 255, 255);

    /**
     * List of all item stacks in the warehouse.
     */
    List<ItemStorage> allItems = new ArrayList<>();

    /**
     * Resource scrolling list.
     */
    private final ScrollingList stackList;

    /**
     * The filter for the resource list.
     */
    private String filter = "";

    /**
     * The sortDescriptor so how we want to sort
     */
    private int sortDescriptor = 0;

    /**
     * The owner controller.
     */
    private final TileEntityController controller;

    /**
     * Constructor for a hut inv display window.
     */
    public WindowHutAllInventory(final TileEntityController controller)
    {
        super(Constants.MOD_ID + HUT_ALL_INVENTORY_SUFFIX);
        this.controller = controller;
        registerButton(BUTTON_SORT, this::setSortFlag);
        this.stackList = findPaneOfTypeByID(LIST_ALLINVENTORY, ScrollingList.class);
        updateResources();
        registerButton(LOCATE, this::locate);
        registerButton(SORT, this::sort);
        registerButton(INSERT, this::insert);

        if (controller.isSortUnlocked())
        {
            findPaneOfTypeByID(SORT, ButtonImage.class).setText(new TranslatableComponent("gui.storageracks.sort"));
        }
        else
        {
            findPaneOfTypeByID(SORT, ButtonImage.class).setText(new TranslatableComponent("gui.storageracks.sort.unlock"));
            final ItemIcon icon = findPaneOfTypeByID("sortcost", ItemIcon.class);
            setupIcon(icon, Items.REDSTONE_BLOCK);
        }

        if (controller.isInsertUnlocked())
        {
            findPaneOfTypeByID(INSERT, ButtonImage.class).setText(new TranslatableComponent("gui.storageracks.insert"));
        }
        else
        {
            findPaneOfTypeByID(INSERT, ButtonImage.class).setText(new TranslatableComponent("gui.storageracks.insert.unlock"));
            final ItemIcon icon = findPaneOfTypeByID("insertcost", ItemIcon.class);
            setupIcon(icon, Items.HOPPER);
        }
    }

    private void setupIcon(final ItemIcon icon, final Item item)
    {
        icon.setVisible(true);
        icon.setItem(new ItemStack(item, 1));
        if (InventoryUtils.findFirstSlotInItemHandlerWith(new InvWrapper(Minecraft.getInstance().player.getInventory()), item) >= 0)
        {
            PaneBuilders.tooltipBuilder().hoverPane(icon).paragraphBreak().append(new TranslatableComponent("gui.storage.racks.available")).color(GREEN).build();
        }
        else
        {
            PaneBuilders.tooltipBuilder().hoverPane(icon).paragraphBreak().append(new TranslatableComponent("gui.storage.racks.missing")).color(RED).build();
        }
    }

    /**
     * If not yet unlocked, try to spend the player res and unlock. Else Open the insert window.
     */
    private void insert()
    {
        if (controller.isInsertUnlocked())
        {
            Network.getNetwork().sendToServer(new OpenInventoryMessage(this.controller.getBlockPos()));
        }
        else
        {
            Network.getNetwork().sendToServer(new UnlockInsertMessage(this.controller.getBlockPos()));
            close();
        }
    }

    /**
     * If not yet unlocked, try to spend the player res and unlock. Else Sends a message to the server side to sort all the inventories.
     */
    private void sort()
    {
        if (controller.isSortUnlocked())
        {
            Network.getNetwork().sendToServer(new SortControllerMessage(this.controller.getBlockPos()));
        }
        else
        {
            Network.getNetwork().sendToServer(new UnlockSortMessage(this.controller.getBlockPos()));
        }

        close();
    }

    private void locate(final Button button)
    {
        final int row = stackList.getListElementIndexByPane(button);
        final ItemStorage storage = allItems.get(row);
        final Set<BlockPos> containerList = new HashSet<>(controller.racks);
        HighlightManager.clearCategory("inventoryHighlight");

        Minecraft.getInstance().player.sendMessage(new TranslatableComponent("gui.storageracks.locating"), Minecraft.getInstance().player.getUUID());
        close();

        for (BlockPos blockPos : containerList)
        {
            final BlockEntity rack = Minecraft.getInstance().level.getBlockEntity(blockPos);
            if (rack instanceof TileEntityRack)
            {
                int count = ((TileEntityRack) rack).getCount(storage.getItemStack());
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
     * Increments the sortDescriptor and sets the GUI Button accordingly Valid Stages 0 - 4 NO_SORT 0   No Sorting, like wysiwyg ASC_SORT 1   Name Ascending DESC_SORT 2   Name
     * Descending COUNT_ASC_SORT 3   Itemcount Ascending COUNT_DESC_SORT 4   Itemcount Descending
     **/
    private void setSortFlag()
    {
        sortDescriptor++;
        if (sortDescriptor > 4)
        {
            sortDescriptor = NO_SORT;
        }
        switch (sortDescriptor)
        {
            case NO_SORT:
                findPaneOfTypeByID(BUTTON_SORT, ButtonImage.class).setText(new TextComponent("v^"));
                break;
            case ASC_SORT:
                findPaneOfTypeByID(BUTTON_SORT, ButtonImage.class).setText(new TextComponent("A^"));
                break;
            case DESC_SORT:
                findPaneOfTypeByID(BUTTON_SORT, ButtonImage.class).setText(new TextComponent("Av"));
                break;
            case COUNT_ASC_SORT:
                findPaneOfTypeByID(BUTTON_SORT, ButtonImage.class).setText(new TextComponent("1^"));
                break;
            case COUNT_DESC_SORT:
                findPaneOfTypeByID(BUTTON_SORT, ButtonImage.class).setText(new TextComponent("1v"));
                break;
            default:
                break;
        }

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
            if (rack instanceof TileEntityRack)
            {
                final Map<ItemStorage, Integer> rackStorage = ((TileEntityRack) rack).getAllContent();

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
        final Predicate<ItemStorage> filterPredicate = stack -> filter.isEmpty()
                                                                  || stack.getItemStack().getDescriptionId().toLowerCase(Locale.US).contains(filter.toLowerCase(Locale.US))
                                                                  || stack.getItemStack()
                                                                       .getDisplayName()
                                                                       .getString()
                                                                       .toLowerCase(Locale.US)
                                                                       .contains(filter.toLowerCase(Locale.US));

        allItems.clear();
        if (filter.isEmpty())
        {
            allItems.addAll(filterItems);
        }
        else
        {
            allItems.addAll(filterItems.stream().filter(filterPredicate).collect(Collectors.toList()));
        }

        if (!filter.isEmpty())
        {
            allItems.sort(Comparator.comparingInt(s1 -> StringUtils.getLevenshteinDistance(s1.getItemStack().getHoverName().getString(), filter)));
        }
        final Comparator<ItemStorage> compareByName = Comparator.comparing((ItemStorage o) -> o.getItemStack().getDisplayName().getString());
        final Comparator<ItemStorage> compareByCount = Comparator.comparingInt(ItemStorage::getAmount);
        switch (sortDescriptor)
        {
            case NO_SORT:
                break;
            case ASC_SORT:
                allItems.sort(compareByName);
                break;
            case DESC_SORT:
                allItems.sort(compareByName.reversed());
                break;
            case COUNT_ASC_SORT:
                allItems.sort(compareByCount);
                break;
            case COUNT_DESC_SORT:
                allItems.sort(compareByCount.reversed());
                break;
            default:
                break;
        }

        updateResourceList();
    }

    /**
     * Updates the resource list in the GUI with the info we need.
     */
    private void updateResourceList()
    {
        stackList.enable();

        //Creates a dataProvider for the unemployed stackList.
        stackList.setDataProvider(new ScrollingList.DataProvider()
        {
            /**
             * The number of rows of the list.
             * @return the number.
             */
            @Override
            public int getElementCount()
            {
                return allItems.size();
            }

            /**
             * Inserts the elements into each row.
             * @param index the index of the row/list element.
             * @param rowPane the parent Pane for the row, containing the elements to update.
             */
            @Override
            public void updateElement(final int index, @NotNull final Pane rowPane)
            {
                final ItemStorage resource = allItems.get(index);
                final Text resourceLabel = rowPane.findPaneOfTypeByID("ressourceStackName", Text.class);
                final String name = new TranslatableComponent(resource.getItemStack().getDescriptionId()).getString();
                resourceLabel.setText(name.substring(0, Math.min(17, name.length())));
                final Text qtys = rowPane.findPaneOfTypeByID("quantities", Text.class);
                if (!Screen.hasShiftDown())
                {
                    qtys.setText(format(resource.getAmount()));
                }
                else
                {
                    qtys.setText(Integer.toString(resource.getAmount()));
                }
                final Item imagesrc = resource.getItemStack().getItem();
                final ItemStack image = new ItemStack(imagesrc, 1);
                image.setTag(resource.getItemStack().getTag());
                rowPane.findPaneOfTypeByID(RESOURCE_ICON, ItemIcon.class).setItem(image);
            }
        });
    }

    /**
     * Formats a long value into a abbreviated string, ie: 1000->1k, 1200->1.2k, 13000->13k
     *
     * @param value to format
     * @return string version of the value
     */
    public static String format(long value)
    {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE)
        {
            return format(Long.MIN_VALUE + 1);
        }
        if (value < 0)
        {
            return "-" + format(-value);
        }
        if (value < 1000)
        {
            return Long.toString(value); //deal with easy case
        }

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10d);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    @Override
    public boolean onKeyTyped(final char ch, final int key)
    {
        final boolean result = super.onKeyTyped(ch, key);
        if (result)
        {
            filter = findPaneOfTypeByID("names", TextField.class).getText();
            updateResources();
        }
        return result;
    }
}
