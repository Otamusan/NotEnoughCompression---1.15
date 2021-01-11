package otamusan.nec.client.itemcompressed;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.lib.ColorUtil;

//render all compressed items including non-block items
@OnlyIn(Dist.CLIENT)
public class CompressedTEISR extends ItemStackTileEntityRenderer {
	//ItemRenderer
	@Override
	public void render(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer type,
			int p_228364_4_, int p_228364_5_) {
		IBakedModel model = Minecraft.getInstance().getItemRenderer()
				.getItemModelWithOverrides(ItemCompressed.getOriginal(stack), null, null);
		matrix.translate(0.5D, 0.5D, 0.5D);
		renderItem(stack, geTransformType(), false, matrix, type, p_228364_4_, p_228364_5_, model);
	}

	public TransformType geTransformType() {
		StackTraceElement[] ste = new Throwable().getStackTrace();
		if (ste[3].getMethodName() == "renderItemModelIntoGUI")
			return TransformType.GUI;

		if (ste[5].getMethodName() == "renderEntityStatic")
			return TransformType.GROUND;

		if (ste[4].getMethodName() == "renderItemSide")
			return TransformType.FIRST_PERSON_LEFT_HAND;

		return TransformType.GUI;
	}

	public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType,
			boolean p_229111_3_, MatrixStack matrix, IRenderTypeBuffer typeBuffer, int p_229111_6_,
			int p_229111_7_, IBakedModel model) {
		if (stack.isEmpty())
			return;
		matrix.push();
		boolean flag = transformType == ItemCameraTransforms.TransformType.GUI;
		boolean flag1 = flag || transformType == ItemCameraTransforms.TransformType.GROUND
				|| transformType == ItemCameraTransforms.TransformType.FIXED;

		if (ItemCompressed.getOriginal(stack).getItem() == Items.TRIDENT && flag1) {
			model = Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager()
					.getModel(new ModelResourceLocation("minecraft:trident#inventory"));
		}

		model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrix, model,
				transformType, p_229111_3_);
		matrix.translate(-0.5D, -0.5D, -0.5D);
		if (!model.isBuiltInRenderer() && (ItemCompressed.getOriginal(stack).getItem() != Items.TRIDENT || flag1)) {
			RenderType rendertype = RenderTypeLookup.getRenderType(ItemCompressed.getOriginal(stack));
			RenderType rendertype1;
			if (flag && Objects.equals(rendertype, Atlases.getTranslucentBlockType())) {
				rendertype1 = Atlases.getTranslucentCullBlockType();
			} else {
				rendertype1 = rendertype;
			}

			IVertexBuilder ivertexbuilder = getBuffer(typeBuffer, rendertype1, true,
					ItemCompressed.getOriginal(stack).hasEffect());
			this.renderModel(model, stack, p_229111_6_, p_229111_7_, matrix, ivertexbuilder);
		} else {
			ItemCompressed.getOriginal(stack).getItem().getItemStackTileEntityRenderer().render(
					ItemCompressed.getOriginal(stack),
					matrix,
					typeBuffer, p_229111_6_, p_229111_7_);
		}

		matrix.pop();
		;
	}

	public static IVertexBuilder getBuffer(IRenderTypeBuffer bufferIn, RenderType renderTypeIn, boolean isItemIn,
			boolean glintIn) {
		return glintIn ? VertexBuilderUtils.newDelegate(
				bufferIn.getBuffer(isItemIn ? RenderType.getGlint() : RenderType.getEntityGlint()),
				bufferIn.getBuffer(renderTypeIn)) : bufferIn.getBuffer(renderTypeIn);
	}

	private void renderModel(IBakedModel model, ItemStack stack, int p_229114_3_, int p_229114_4_,
			MatrixStack matrix, IVertexBuilder vertexBuilder) {
		Random random = new Random();
		long i = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			this.renderQueds(matrix, vertexBuilder, model.getQuads((BlockState) null, direction, random),
					stack, p_229114_3_, p_229114_4_);
		}

		random.setSeed(42L);
		this.renderQueds(matrix, vertexBuilder, model.getQuads((BlockState) null, (Direction) null, random),
				stack, p_229114_3_, p_229114_4_);
	}

	public void renderQueds(MatrixStack matrix, IVertexBuilder vertexBuilder, List<BakedQuad> queds,
			ItemStack stack, int p_229112_5_, int p_229112_6_) {
		boolean flag = !stack.isEmpty();
		MatrixStack.Entry matrixstack$entry = matrix.getLast();

		for (BakedQuad bakedquad : queds) {
			int i = -1;
			if (flag && bakedquad.hasTintIndex()) {
				i = Minecraft.getInstance().getItemColors().getColor(ItemCompressed.getOriginal(stack),
						bakedquad.getTintIndex());
				i = ColorUtil.getCompressedColor(new Color(i), ItemCompressed.getTime(stack)).getRGB();
			} else {
				i = ColorUtil.getCompressedColor(ItemCompressed.getTime(stack)).getRGB();
			}
			float f = (float) (i >> 16 & 255) / 255.0F;
			float f1 = (float) (i >> 8 & 255) / 255.0F;
			float f2 = (float) (i & 255) / 255.0F;
			vertexBuilder.addVertexData(matrixstack$entry, bakedquad, f, f1, f2, p_229112_5_, p_229112_6_, true);
		}

	}
}
