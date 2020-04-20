package com.jvanbruegge.techmod.cablecar;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class CablecarRenderer extends EntityRenderer<CablecarEntity> {
    private CablecarModel model = new CablecarModel();
    private static final ResourceLocation texture = new ResourceLocation("textures/painting/black.png");

    public CablecarRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(CablecarEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.push();

        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getEntitySolid(this.getEntityTexture(entityIn)));
        this.model.render(matrixStackIn, builder, packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(CablecarEntity entity) {
        return texture;
    }
}
