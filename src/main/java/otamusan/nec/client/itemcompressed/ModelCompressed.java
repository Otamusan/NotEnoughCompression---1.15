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

//the model to be registered
//put out a wrapped the original model using getOverrides()
@OnlyIn(Dist.CLIENT)
public class ModelCompressed implements IBakedModel {

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return new ArrayList<BakedQuad>();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemCompressedOverrideList();
	}

	@Override
	public boolean func_230044_c_() {
		return false;
	}

}
