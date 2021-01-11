package otamusan.nec.item.Using;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.math.BlockRayTraceResult;

public class UseContextWrapper extends ItemUseContext {
	public ItemUseContext originalContext;
	public ItemStack stack;

	public UseContextWrapper(ItemUseContext original, ItemStack stack) {
		super(original.getPlayer(), original.getHand(), new BlockRayTraceResult(original.getHitVec(),
				original.getFace(), original.getPos(), original.isInside()));
		this.originalContext = original;
		this.stack = stack;
	}

	@Override
	public ItemStack getItem() {
		return stack;
	}
}
