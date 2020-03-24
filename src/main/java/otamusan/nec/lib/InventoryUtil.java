package otamusan.nec.lib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class InventoryUtil {
	public static List<ItemStack> putStacksInInv(IItemHandler itemHandler, List<ItemStack> stacks) {
		List<ItemStack> remains = new ArrayList<ItemStack>();
		for (ItemStack itemStack : stacks) {
			ItemStack remain = ItemHandlerHelper.insertItemStacked(itemHandler, itemStack, false);
			if (!remain.isEmpty()) {
				remains.add(remain);
			}
		}
		return remains;
	}
}
