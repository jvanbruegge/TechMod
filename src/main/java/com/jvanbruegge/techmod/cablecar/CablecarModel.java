package com.jvanbruegge.techmod.cablecar;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CablecarModel extends EntityModel<CablecarEntity> {
    private final ModelRenderer hook;
    private final ModelRenderer body;

    public CablecarModel() {
        textureWidth = 128/2;
        textureHeight = 128/2;

        hook = new ModelRenderer(this);
        hook.setRotationPoint(0F, 16F, 0F);
        hook.addBox(-5F, -10.25F, -1F, 1F, 8F, 2F);
        hook.addBox(4F, -10.25F, -1F, 1F, 8F, 2F);
        hook.setTextureOffset(0, 23);
        hook.addBox(-4F, -3.25F, -1F, 8F, 1F, 2F);
        hook.setTextureOffset(0, 11);
        hook.addBox(-2.5F, -2.25F, -0.5F, 0.5F, 1F, 1F);
        hook.addBox(2F, -2.25F, -0.5F, 0.5F, 1F, 1F);
        hook.addBox(1F, -1.75F, -0.5F, 1F, 0.5F, 1F);
        hook.addBox(-2F, -1.75F, -0.5F, 1F, 0.5F, 1F);

        body = new ModelRenderer(this);
        body.setRotationPoint(0F, -9.25F, 0F);
        hook.addChild(body);
        body.addBox(-4F, -4F, -5F, 8F, 1F, 10F);
        body.setTextureOffset(0, 11);
        body.addBox(-4F, -3F, -4F, 1F, 4F, 8F);
        body.addBox(3F, -3F, -4F, 1F, 4F, 8F, true);
        body.setTextureOffset(10, 11);
        body.addBox(-4F, -3F, 4F, 8F, 4F, 1F);
        body.setTextureOffset(17, 16);
        body.addBox(-4F, -3F, -5F, 8F, 4F, 1F, true);
    }

    @Override
    public void setRotationAngles(CablecarEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrix, IVertexBuilder builder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        hook.render(matrix, builder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}