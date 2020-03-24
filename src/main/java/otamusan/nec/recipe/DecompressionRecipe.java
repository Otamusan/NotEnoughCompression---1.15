package otamusan.nec.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import otamusan.nec.config.ConfigCommon;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.register.ItemRegister;

public class DecompressionRecipe extends NECRecipe {

	public DecompressionRecipe(ResourceLocation rs) {
		super(rs);
	}

	public ItemStack getDecompresser() {
		return new ItemStack(ConfigCommon.CONFIG_COMMON.getDecompressionCatalyst());
	}

	public boolean isCompressable(ItemStack stack) {
		return ItemCompressed.isCompressable(stack);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		if (getStackedSlotCount(inv) == 1) {
			return getSlotCount(inv, item -> ItemCompressed.isCompressed(item)) == 1;
		} else if (getStackedSlotCount(inv) == 2) {
			return getSlotCount(inv, item -> areItemStacksEqual(item, getDecompresser())) == 1
					&& getSlotCount(inv, item -> ItemCompressed.isCompressed(item)) == 1;
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack compressed = ItemCompressed
				.createDecompressed(getStack(inv, item -> ItemCompressed.isCompressed(item)));
		compressed.setCount(8);
		return compressed;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ItemRegister.ITEM_COMPRESSED);
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return null;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		NonNullList<ItemStack> list = NonNullList.withSize(9, ItemStack.EMPTY);
		int slot = getSlot(inv, item -> areItemStacksEqual(item, getDecompresser()));
		if (slot == -1)
			return list;
		ItemStack compresser = inv.getStackInSlot(slot).copy();
		list.set(slot, compresser);

		return list;
	}

}
