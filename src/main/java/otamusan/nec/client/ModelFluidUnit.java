package otamusan.nec.client;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.registries.ForgeRegistries;

public class ModelFluidUnit implements IBakedModel {

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		IBakedModel snow = Minecraft.getInstance().getItemRenderer()
				.getItemModelWithOverrides(new ItemStack(Items.SNOWBALL), null, null);

		Fluid fluid = ForgeRegistries.FLUIDS.getValue(Fluids.WATER.getRegistryName());
		TextureAtlasSprite tempsprite = Minecraft.getInstance()
				.getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
				.apply(Items.SNOWBALL.getRegistryName());
		//tempsprite = snow.getParticleTexture();

		TextureAtlasSprite fluidsprite = Minecraft.getInstance()
				.getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
				.apply(fluid.getAttributes().getStillTexture(Minecraft.getInstance().world, new BlockPos(0, 0, 0)));
		System.out.println(tempsprite);
		System.out.println(fluidsprite);
		return ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), tempsprite, fluidsprite, 0,
				side, 0, 0);
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
	public boolean func_230044_c_() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return Minecraft.getInstance()
				.getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
				.apply(new ResourceLocation("minecraft:snowball"));
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.EMPTY;
	}

}
