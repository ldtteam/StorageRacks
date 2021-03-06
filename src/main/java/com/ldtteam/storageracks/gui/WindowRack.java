package com.ldtteam.storageracks.gui;

import com.ldtteam.storageracks.inv.ContainerRack;
import com.ldtteam.storageracks.utils.Constants;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class WindowRack extends ContainerScreen<ContainerRack>
{
    /**
     * The resource LOCATION of the texture.
     */
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/generic_108.png");

    /**
     * Amount of slots each row.
     */
    private static final int SLOTS_EACH_ROW = 9;

    /**
     * Size of the custom texture.
     */
    private static final int TEXTURE_SIZE = 350;

    /**
     * Offset of each slot.
     */
    private static final int SLOT_OFFSET = 18;

    /**
     * Multiply the current size by this amount.
     */
    private static final int SIZE_MULTIPLIER = 3;

    /**
     * General y offset.
     */
    private static final int Y_OFFSET = 114;

    /**
     * Offet of the screen for the texture.
     */
    private static final int TEXTURE_HEIGHT = 96;

    /**
     * Offset inside the texture to use.
     */
    private static final int TEXTURE_OFFSET = 126 * 2 - 17;

    /**
     * Extra offset to move increase the texture if the inventory is huge.
     */
    private static final int EXTRA_OFFSET = 56;

    /**
     * Extra height to show the whole texture for big inventories.
     */
    private static final int EXTRA_HEIGHT = 50;

    /**
     * Used to calculate the window height.
     */
    private final int inventoryRows;

    /**
     * Create a new rack inv.
     * @param container the container.
     * @param playerInventory the player inv.
     * @param iTextComponent the text component for display.
     */
    public WindowRack(final ContainerRack container, final PlayerInventory playerInventory, final ITextComponent iTextComponent)
    {
        super(container, playerInventory, iTextComponent);

        this.inventoryRows = container.rack.getInventory().getSlots() / SLOTS_EACH_ROW;

        this.imageHeight = Y_OFFSET + Math.min(SLOTS_EACH_ROW, this.inventoryRows) * SLOT_OFFSET;
        if (this.inventoryRows > SLOTS_EACH_ROW - 1)
        {
            this.imageWidth = this.imageWidth + (this.inventoryRows - SLOTS_EACH_ROW) * (SLOTS_EACH_ROW + 1);
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void renderLabels(@NotNull final MatrixStack stack, int mouseX, int mouseY)
    {
        this.font.draw(stack, this.title.getString(), 8.0F, 6.0F, 4210752);
        this.font.draw(stack, this.inventory.getDisplayName().getString(), 8.0F, (float) (this.imageHeight - 94), 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void renderBg(@NotNull final MatrixStack stack, final float partialTicks, final int mouseX, final int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CHEST_GUI_TEXTURE);
        final int i = (this.width - this.imageWidth) / 2;
        final int j = (this.height - this.imageHeight) / 2;

        if (inventoryRows < SLOTS_EACH_ROW)
        {
            blit(stack, i, j, 0, 0, this.imageWidth, this.inventoryRows * SLOT_OFFSET + SLOT_OFFSET - 1, TEXTURE_SIZE, TEXTURE_SIZE);
            blit(stack, i, j + this.inventoryRows * SLOT_OFFSET + SLOT_OFFSET - 1, 0,
              TEXTURE_OFFSET, this.imageWidth, TEXTURE_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        }
        else
        {
            final int textureOffset = TEXTURE_OFFSET - EXTRA_OFFSET;
            blit(stack, i, j, 0, 0, (this.imageWidth * SIZE_MULTIPLIER) / 2, this.inventoryRows * SLOT_OFFSET + SLOT_OFFSET - 1, TEXTURE_SIZE, TEXTURE_SIZE);
            blit(stack, i,
              j + Math.min(SLOTS_EACH_ROW, this.inventoryRows) * SLOT_OFFSET + SLOT_OFFSET - 1,
              0,
              textureOffset,
              (this.imageWidth * SIZE_MULTIPLIER) / 2,
              TEXTURE_HEIGHT + EXTRA_HEIGHT,
              TEXTURE_SIZE,
              TEXTURE_SIZE);
        }
    }

    @Override
    public void render(@NotNull final MatrixStack stack, int x, int y, float z)
    {
        this.renderBackground(stack);
        super.render(stack, x, y, z);
        this.renderTooltip(stack, x, y);
    }
}
