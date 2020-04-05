package com.jvanbruegge.techmod.cablecar;

import com.jvanbruegge.techmod.TechMod;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CableCarDeployerScreen extends ContainerScreen<CablecarDeployerContainer> {
    private final ResourceLocation guiTexture = new ResourceLocation(TechMod.MODID, "textures/gui/cablecar_deployer.png");

    public CableCarDeployerScreen(CablecarDeployerContainer container, PlayerInventory inventory, ITextComponent textComponent) {
        super(container, inventory, textComponent);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        this.blit(startX, startY, 0, 0, this.xSize, this.ySize);
    }
}
