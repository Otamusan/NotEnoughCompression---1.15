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

public class CompressionRecipe extends NECRecipe {

	public CompressionRecipe(ResourceLocation rs) {
		super(rs);
	}

	public static ItemStack getCompresser() {
		return new ItemStack(ConfigCommon.CONFIG_COMMON.getCompressionCatalyst());
	}

	public boolean isCompressable(ItemStack stack) {
		return ItemCompressed.isCompressable(stack);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		if (!areItemStacksEqual(inv.getStackInSlot(4), getCompresser()))
			return false;
		ItemStack item = inv.getStackInSlot(0);
		if (!isCompressable(item))
			return false;
		if (!isMatchStacks(inv, new int[] { 0, 1, 2, 3, 5, 6, 7, 8 }, item))
			return false;
		return true;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack compressed = ItemCompressed.createCompressed(inv.getStackInSlot(0));
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
		list.set(4, getStack(inv, item -> areItemStacksEqual(item, getCompresser())).copy());
		return list;
	}

}
