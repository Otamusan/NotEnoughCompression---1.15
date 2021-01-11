package otamusan.nec.client.itemcompressed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

//Given the original model through ItemCompressedOverrideList
@OnlyIn(Dist.CLIENT)
public class ModelCompressedItemWrapper implements IBakedModel {

	IBakedModel original;

	public ModelCompressedItemWrapper(IBakedModel original) {
		this.original = original;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		//return original.getQuads(state, side, rand);
		return new ArrayList<>();
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData data) {
		List<BakedQuad> quads = original.getQuads(state, side, rand, data);
		List<BakedQuad> newquads = new ArrayList<>();

		for (BakedQuad bakedQuad : quads) {
			newquads.add(new BakedQuad(bakedQuad.getVertexData(), bakedQuad.getTintIndex() + 100, bakedQuad.getFace(),
					bakedQuad.func_187508_a(), bakedQuad.shouldApplyDiffuseLighting()));
		}
		//return newquads;
		return new ArrayList<>();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return original.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return original.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return original.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.EMPTY;
	}

	/*@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
			TransformType cameraTransformType) {
	
		Matrix4f matrix4f = original.handlePerspective(cameraTransformType).getRight();
		return Pair.of(this, matrix4f);
	}*/

	/*public boolean doesHandlePerspectives() {
		return true;
	}
	
	public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
		return original.handlePerspective(cameraTransformType, mat);
	}*/

	@Override
	public boolean func_230044_c_() {
		return true;
	}
}
