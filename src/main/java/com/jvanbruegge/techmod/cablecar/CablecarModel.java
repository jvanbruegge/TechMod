package com.jvanbruegge.techmod.cablecar;//Made with Blockbench
//Paste this code into your mod.

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CablecarModel extends EntityModel<CablecarEntity> {
	private final ModelRenderer hook;
	private final ModelRenderer body;

	public CablecarModel() {
		textureWidth = 16;
		textureHeight = 16;

		hook = new ModelRenderer(this);
		hook.setRotationPoint(0F, 0F, 0F);
		hook.addBox(-5F, 5.75F, -1F, 1F, 8F, 2F, 0F);
		hook.addBox(-4F, 12.75F, -1F, 8F, 1F, 2F, 0F);
		hook.addBox(-2.5F, 13.75F, -0.5F, 0.5F, 1F, 1F, 0F);
		hook.addBox(2F, 13.75F, -0.5F, 0.5F, 1F, 1F, 0F);
		hook.addBox(1F, 14.25F, -0.5F, 1F, 0.5F, 1F, 0F);
		hook.addBox(-2F, 14.25F, -0.5F, 1F, 0.5F, 1F, 0F);
		hook.addBox(4F, 5.75F, -1F, 1F, 8F, 2F, 0F);

		body = new ModelRenderer(this);
		body.setRotationPoint(0F, -8F, 0F);
		hook.addChild(body);
		body.addBox(-4F, 2.75F, -5F, 8F, 1F, 10F, 0F);
		body.addBox(-4F, 3.75F, -5F, 1F, 4F, 10F, 0F);
		body.addBox(3F, 3.75F, -5F, 1F, 4F, 10F, 0F);
		body.addBox(-3F, 3.75F, 4F, 6F, 4F, 1F, 0F);
		body.addBox(-3F, 3.75F, -5F, 6F, 4F, 1F, 0F);
	}

    @Override
    public void setRotationAngles(CablecarEntity entity, float f1, float f2, float f3, float f4, float f5) {}

    @Override
    public void render(MatrixStack stack, IVertexBuilder builder, int i1, int i2, float f1, float f2, float f3, float f4) {
        hook.render(stack, builder, i1, i2, f1, f2, f3, f4);
    }
}