package com.ldtteam.storageracks.gui;

import com.ldtteam.storageracks.inv.InsertContainer;
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
public class WindowInsert extends ContainerScreen<InsertContainer>
{
    /**
     * Hopper inv ui texture location.
     */
    private static final ResourceLocation HOPPER_LOCATION = new ResourceLocation("textures/gui/container/hopper.png");

    /**
     * Create a new insert window.
     * @param container the container it belongs to.
     * @param player the player opening it.
     * @param label the label to display.
     */
    public WindowInsert(@NotNull final InsertContainer container, @NotNull final PlayerInventory player, @NotNull final ITextComponent label)
    {
        super(container, player, label);
        this.passEvents = false;
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(@NotNull final MatrixStack stack, int partialTicks, int mouseX, float mouseY)
    {
        this.renderBackground(stack);
        super.render(stack, partialTicks, mouseX, mouseY);
        this.renderTooltip(stack, partialTicks, mouseX);
    }

    @Override
    protected void renderBg(@NotNull final MatrixStack stack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(HOPPER_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
