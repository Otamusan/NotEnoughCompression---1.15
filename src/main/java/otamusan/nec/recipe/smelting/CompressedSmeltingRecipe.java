package otamusan.nec.recipe.smelting;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.register.ItemRegister;

//it has bug, cant use.
public class CompressedSmeltingRecipe<T extends IRecipe<IInventory>> extends AbstractCookingRecipe {

	public CompressedSmeltingRecipe(ResourceLocation rs, IRecipeType<? extends IRecipe<IInventory>> type,
			Class<? extends TileEntity> tile) {
		super(type, rs, "nec_smelting", Ingredient.EMPTY, ItemStack.EMPTY, 10, 10);
		this.tile = tile;
		this.invtype = type;
	}

	public static class Smelting extends CompressedSmeltingRecipe<FurnaceRecipe> {
		public Smelting(ResourceLocation rs) {
			super(rs, IRecipeType.SMELTING, FurnaceTileEntity.class);
		}
	}

	public static class Smorking extends CompressedSmeltingRecipe<FurnaceRecipe> {
		public Smorking(ResourceLocation rs) {
			super(rs, IRecipeType.SMOKING, SmokerTileEntity.class);
		}
	}

	public static class Blasting extends CompressedSmeltingRecipe<FurnaceRecipe> {
		public Blasting(ResourceLocation rs) {
			super(rs, IRecipeType.BLASTING, SmokerTileEntity.class);
		}
	}

	IRecipeType<? extends IRecipe<IInventory>> invtype;

	World world;
	IInventory inventorybuff;
	Class<? extends TileEntity> tile;

	@Override
	public ItemStack getRecipeOutput() {
		return getCraftingResult(inventorybuff);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return null;
	}

	public boolean matches(IInventory inv, World worldIn) {
		ItemStack compressed = inv.getStackInSlot(0);
		world = worldIn;
		inventorybuff = inv;
		if (!ItemCompressed.isCompressed(compressed))
			return false;
		if (inv.getClass() != tile)
			return false;

		Optional<? extends IRecipe<IInventory>> recipeOptional = getOriginalRecipe(compressed, inv, worldIn);

		return recipeOptional.isPresent();
	}

	public Optional<? extends IRecipe<IInventory>> getOriginalRecipe(ItemStack compressed, IInventory iInventory,
			World world) {
		ItemStack original = ItemCompressed.getOriginal(compressed);
		IInventory copiedInventory = new InventoryCopied(original.copy());
		Optional<? extends IRecipe<IInventory>> recipe = world.getRecipeManager().getRecipe(invtype, copiedInventory,
				world);
		return recipe;
	}

	public static class InventoryCopied implements IInventory {
		private ItemStack stack;

		public InventoryCopied(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public void clear() {
		}

		@Override
		public ItemStack decrStackSize(int arg0, int arg1) {
			return ItemStack.EMPTY;
		}

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int arg0) {
			if (arg0 == 0)
				return stack;
			return ItemStack.EMPTY;
		}

		@Override
		public boolean isEmpty() {
			return stack.isEmpty();
		}

		@Override
		public boolean isUsableByPlayer(PlayerEntity arg0) {
			return true;
		}

		@Override
		public void markDirty() {
		}

		@Override
		public ItemStack removeStackFromSlot(int arg0) {
			return ItemStack.EMPTY;
		}

		@Override
		public void setInventorySlotContents(int arg0, ItemStack arg1) {
		}

	}

	public ItemStack getCraftingResult(IInventory inv) {
		IRecipe<IInventory> recipe = getOriginalRecipe(inv.getStackInSlot(0), inv, world).get();
		IInventory inventory = new InventoryCopied(ItemCompressed.getOriginal(inv.getStackInSlot(0)));

		ItemStack result = recipe.getCraftingResult(inventory);

		ItemStack output = ItemCompressed.createCompressed(result,
				ItemCompressed.getTime(inv.getStackInSlot(0)));
		return output;
	}

	public boolean canFit(int width, int height) {
		return true;
	}

	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(Ingredient.fromItems(ItemRegister.ITEM_COMPRESSED));
		return nonnulllist;
	}

	public float getExperience() {
		return 0;
	}

	@Override
	public int getCookTime() {
		return 400;
	}
}
