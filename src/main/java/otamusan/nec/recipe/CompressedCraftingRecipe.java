package otamusan.nec.recipe;

import java.util.Optional;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.register.ItemRegister;

public class CompressedCraftingRecipe extends NECRecipe {

	public CompressedCraftingRecipe(ResourceLocation rs) {
		super(rs);
	}

	World world;

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		world = worldIn;

		int time = 0;
		for (ItemStack stack : getIterator(inv)) {
			if (stack.isEmpty())
				continue;
			if (!ItemCompressed.isCompressed(stack))
				return false;
			if (time != 0 && ItemCompressed.getTime(stack) != time)
				return false;
			time = ItemCompressed.getTime(stack);
		}
		if (time == 0)
			return false;

		Optional<ICraftingRecipe> optional = worldIn.getRecipeManager().getRecipe(IRecipeType.CRAFTING,
				getDecompresserdInv(inv), worldIn);

		if (!optional.isPresent())
			return false;

		return true;
	}

	public ICraftingRecipe getRecipe(World worldIn, CraftingInventory inv) {
		return worldIn.getRecipeManager().getRecipe(IRecipeType.CRAFTING,
				getDecompresserdInv(inv), worldIn).get();
	}

	protected CraftingInventory getDecompresserdInv(CraftingInventory inv) {
		CraftingInventory after = CICopy(inv);
		CraftingInventory newinv = CICopy(inv);
		for (int i = 0; i < newinv.getSizeInventory(); i++) {
			ItemStack slot = newinv.getStackInSlot(i).copy();
			ItemStack original;

			if (slot.isEmpty()) {
				original = slot;
			} else {
				original = ItemCompressed.getOriginal(slot);
			}

			after.setInventorySlotContents(i, original);
		}
		return after;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack stack = getRecipe(world, inv).getCraftingResult(inv);
		int time = ItemCompressed.getTime(getStack(inv, item -> !item.isEmpty()));
		ItemStack compressed = ItemCompressed.createCompressed(stack, time);
		compressed.setCount(stack.getCount());
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
		return list;
	}

}
