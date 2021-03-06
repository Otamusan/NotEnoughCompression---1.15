package otamusan.nec.client.blockcompressed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import otamusan.nec.block.tileentity.ITileCompressed;

@OnlyIn(Dist.CLIENT)
public class ModelCompressedBlock implements IBakedModel {

	public final static ModelProperty<CompressedData> COMPRESSED_PROPERTY = new CompressedProperty();

	public static BlockState getState(IModelData data) {
		if (!data.hasProperty(COMPRESSED_PROPERTY))
			return Blocks.STONE.getDefaultState();
		return data.getData(COMPRESSED_PROPERTY).getState();
	}

	public static IBakedModel getModel(IModelData data) {
		return Minecraft.getInstance().getBlockRendererDispatcher()
				.getModelForState(getState(data));
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData data) {
		List<BakedQuad> quads = getModel(data)
				.getQuads(getState(data), side, rand, data);
		List<BakedQuad> newquads = new ArrayList<>();

		for (BakedQuad bakedQuad : quads) {
			newquads.add(new BakedQuad(bakedQuad.getVertexData(), bakedQuad.getTintIndex() + 100, bakedQuad.getFace(),
					bakedQuad.func_187508_a(), bakedQuad.shouldApplyDiffuseLighting()));
		}
		return newquads;
		//return quads;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return new ArrayList<BakedQuad>();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(Blocks.STONE.getDefaultState())
				.getParticleTexture();
	}

	@Override
	public TextureAtlasSprite getParticleTexture(IModelData data) {
		return getModel(data).getParticleTexture(data);
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.EMPTY;
	}

	@Override
	public IModelData getModelData(ILightReader world, BlockPos pos, BlockState state, IModelData tileData) {

		if (!(world.getTileEntity(pos) instanceof ITileCompressed))
			return tileData;
		ITileCompressed tile = (ITileCompressed) world.getTileEntity(pos);
		tileData.setData(COMPRESSED_PROPERTY, tile.getCompressedData());

		return tileData;
	}

	@Override
	public boolean func_230044_c_() {
		return true;
	}

}
