package otamusan.nec.item;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;

public class ItemCompressedBlockUseContext extends BlockItemUseContext {
	public ItemCompressedBlockUseContext(BlockItemUseContext context, ItemStack stack) {
		//super(new ItemUseContext(context.player, context.hand, context.rayTraceResult));
		super(context.getWorld(), context.getPlayer(), context.getHand(), stack, new BlockRayTraceResult(
				context.getHitVec(), context.getFace(), context.getPos(), context.isInside()));
	}

	@Override
	public ItemStack getItem() {
		return super.getItem();
	}
}
