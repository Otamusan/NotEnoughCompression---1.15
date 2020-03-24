package otamusan.nec.client.itemcompressed;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import otamusan.nec.item.ItemCompressed;

@OnlyIn(Dist.CLIENT)
public class ItemCompressedOverrideList extends ItemOverrideList {
	public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn,
			@Nullable LivingEntity entityIn) {
		ItemStack original = ItemCompressed.getOriginal(stack);
		IBakedModel model2 = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(original, worldIn,
				entityIn);
		return new ModelCompressedItemWrapper(model2);
	}
}
