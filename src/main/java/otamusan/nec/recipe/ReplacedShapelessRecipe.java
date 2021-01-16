package otamusan.nec.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import otamusan.nec.config.ConfigCommon;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.register.RecipeRegister;

public class ReplacedShapelessRecipe extends ShapelessRecipe implements ICraftingRecipe {
	private final ResourceLocation id;
	private final String group;
	private final ItemStack recipeOutput;
	private final NonNullList<Ingredient> recipeItems;
	private final boolean isSimple;
	private boolean replace = ConfigCommon.visReplaceVanillaRecipe;
	private ShapelessRecipe original;
	public ReplacedShapelessRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn,
			NonNullList<Ingredient> recipeItemsIn) {
		super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
		this.id = idIn;
		this.group = groupIn;
		this.recipeOutput = recipeOutputIn;
		this.recipeItems = recipeItemsIn;
		this.isSimple = recipeItemsIn.stream().allMatch(Ingredient::isSimple);
		this.original = new ShapelessRecipe(idIn,groupIn,recipeOutputIn,recipeItemsIn);
	}

	public ResourceLocation getId() {
		return this.id;
	}

	public IRecipeSerializer<?> getSerializer() {
		return RecipeRegister.replacedShapeless;
	}

	/**
	* Recipes with equal group are combined into one button in the recipe book
	*/
	public String getGroup() {
		return this.group;
	}

	/**
	* Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
	* possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
	*/
	public ItemStack getRecipeOutput() {
		return this.recipeOutput;
	}

	public NonNullList<Ingredient> getIngredients() {
		return this.recipeItems;
	}

	public boolean matches(CraftingInventory inv, World worldIn) {
		if(!replace) return original.matches(inv,worldIn);
		RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
		java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
		int i = 0;

		int time = 0;
		for (int j = 0; j < inv.getSizeInventory(); j++) {
			ItemStack stack = inv.getStackInSlot(j);
			if (stack.isEmpty())
				continue;
			if (!ItemCompressed.isCompressed(stack))
				return false;
			int btime = ItemCompressed.getTime(stack);
			if (time != 0 && time != btime)
				return false;
			time = btime;
		}

		//items are compressed

		for (int j = 0; j < inv.getSizeInventory(); ++j) {
			ItemStack compressed = inv.getStackInSlot(j);
			ItemStack itemstack = ItemCompressed.getOriginal(compressed);

			if (!itemstack.isEmpty()) {
				++i;
				if (isSimple)
					recipeitemhelper.func_221264_a(itemstack, 1);
				else
					inputs.add(itemstack);
			}
		}

		return i == this.recipeItems.size() && (isSimple ? recipeitemhelper.canCraft(this, (IntList) null)
				: net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.recipeItems) != null);
	}

	public ItemStack getCraftingResult(CraftingInventory inv) {
		if(!replace) return original.getCraftingResult(inv);
		int time = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			time = ItemCompressed.getTime(inv.getStackInSlot(i));
		}
		ItemStack compressed = ItemCompressed.createCompressed(this.recipeOutput.copy(), time - 1);
		compressed.setCount(this.recipeOutput.getCount());
		return compressed;
	}

	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		if(!replace) return original.getRemainingItems(inv);
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack compressed = inv.getStackInSlot(i);
			ItemStack item = ItemCompressed.getOriginal(compressed);
			int time = ItemCompressed.getTime(compressed);
			if (item.hasContainerItem()) {
				nonnulllist.set(i, ItemCompressed.createCompressed(item.getContainerItem(), time));
			}
		}

		return nonnulllist;
	}

	/**
	* Used to determine if this recipe can fit in a grid of the given width/height
	*/
	public boolean canFit(int width, int height) {
		return width * height >= this.recipeItems.size();
	}

	public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<ShapelessRecipe> {
		public static final ResourceLocation NAME = new ResourceLocation("minecraft", "crafting_shapeless");

		public ShapelessRecipe read(ResourceLocation recipeId, JsonObject json) {
			String s = JSONUtils.getString(json, "group", "");
			NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
			if (nonnulllist.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else if (nonnulllist.size() > ReplacedShapedRecipe.MAX_WIDTH * ReplacedShapedRecipe.MAX_HEIGHT) {
				throw new JsonParseException("Too many ingredients for shapeless recipe the max is "
						+ (ReplacedShapedRecipe.MAX_WIDTH * ReplacedShapedRecipe.MAX_HEIGHT));
			} else {
				ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
				//if (ConfigCommon.visReplaceVanillaRecipe) {
					return new ReplacedShapelessRecipe(recipeId, s, itemstack, nonnulllist);
				//}
				//return new ShapelessRecipe(recipeId, s, itemstack, nonnulllist);

			}
		}

		private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
			NonNullList<Ingredient> nonnulllist = NonNullList.create();

			for (int i = 0; i < p_199568_0_.size(); ++i) {
				Ingredient ingredient = Ingredient.deserialize(p_199568_0_.get(i));
				if (!ingredient.hasNoMatchingItems()) {
					nonnulllist.add(ingredient);
				}
			}

			return nonnulllist;
		}

		public ShapelessRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			String s = buffer.readString(32767);
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

			for (int j = 0; j < nonnulllist.size(); ++j) {
				nonnulllist.set(j, Ingredient.read(buffer));
			}

			ItemStack itemstack = buffer.readItemStack();
			//if (ConfigCommon.visReplaceVanillaRecipe) {
				return new ReplacedShapelessRecipe(recipeId, s, itemstack, nonnulllist);
			//} else {
			//	return new ShapelessRecipe(recipeId, s, itemstack, nonnulllist);
			//}
			//return new ReplacedShapelessRecipe(recipeId, s, new ItemStack(Blocks.STONE), nonnulllist);

		}

		public void write(PacketBuffer buffer, ShapelessRecipe recipe) {
			IRecipeSerializer.CRAFTING_SHAPELESS.write(buffer, recipe);
		}
	}
}