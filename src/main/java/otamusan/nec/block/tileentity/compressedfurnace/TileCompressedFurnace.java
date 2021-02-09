package otamusan.nec.block.tileentity.compressedfurnace;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.block.tileentity.TileCompressedBlock;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.recipe.NECRecipe;
import otamusan.nec.register.BlockRegister;

//FurnaceTileEntity
public class TileCompressedFurnace extends TileCompressedBlock
		implements ISidedInventory, ITickableTileEntity,
		INamedContainerProvider {
	public TileCompressedFurnace() {
	}

	@Override
	public TileEntityType<?> getType() {
		return BlockRegister.TILECOMPRESSEDFURNACETYPE;
	}

	private static final int[] SLOTS_UP = new int[] { 0 };
	private static final int[] SLOTS_DOWN = new int[] { 2, 1 };
	private static final int[] SLOTS_HORIZONTAL = new int[] { 1 };
	protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	private int burnTime;
	private int recipesUsed;
	private int cookTime;
	private int cookTimeTotal;
	protected final IIntArray furnaceData = new IIntArray() {
		public int get(int index) {
			switch (index) {
			case 0:
				return TileCompressedFurnace.this.burnTime;
			case 1:
				return TileCompressedFurnace.this.recipesUsed;
			case 2:
				return TileCompressedFurnace.this.cookTime;
			case 3:
				return TileCompressedFurnace.this.cookTimeTotal;
			default:
				return 0;
			}
		}

		public void set(int index, int value) {
			switch (index) {
			case 0:
				TileCompressedFurnace.this.burnTime = value;
				break;
			case 1:
				TileCompressedFurnace.this.recipesUsed = value;
				break;
			case 2:
				TileCompressedFurnace.this.cookTime = value;
				break;
			case 3:
				TileCompressedFurnace.this.cookTimeTotal = value;
			}

		}

		public int size() {
			return 4;
		}
	};
	private final Map<ResourceLocation, Integer> field_214022_n = Maps.newHashMap();
	protected final IRecipeType<? extends AbstractCookingRecipe> recipeType = IRecipeType.SMELTING;

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	public void read(CompoundNBT compound) {
		super.read(compound);
		this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, this.items);
		this.burnTime = compound.getInt("BurnTime");
		this.cookTime = compound.getInt("CookTime");
		this.cookTimeTotal = compound.getInt("CookTimeTotal");
		this.recipesUsed = this.getBurnTime(this.items.get(1));
		int i = compound.getShort("RecipesUsedSize");

		for (int j = 0; j < i; ++j) {
			ResourceLocation resourcelocation = new ResourceLocation(compound.getString("RecipeLocation" + j));
			int k = compound.getInt("RecipeAmount" + j);
			this.field_214022_n.put(resourcelocation, k);
		}

	}

	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putInt("BurnTime", this.burnTime);
		compound.putInt("CookTime", this.cookTime);
		compound.putInt("CookTimeTotal", this.cookTimeTotal);
		ItemStackHelper.saveAllItems(compound, this.items);
		compound.putShort("RecipesUsedSize", (short) this.field_214022_n.size());
		int i = 0;

		for (Entry<ResourceLocation, Integer> entry : this.field_214022_n.entrySet()) {
			compound.putString("RecipeLocation" + i, entry.getKey().toString());
			compound.putInt("RecipeAmount" + i, entry.getValue());
			++i;
		}

		return compound;
	}

	public static class InvWrapper implements IInventory {

		private ItemStack original;
		private IInventory inv;

		public InvWrapper(IInventory inv, ItemStack original) {
			this.inv = inv;
			this.original = original;
		}

		@Override
		public void clear() {
		}

		@Override
		public int getSizeInventory() {
			return inv.getSizeInventory();
		}

		@Override
		public boolean isEmpty() {
			return inv.isEmpty();
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			if (index == 0)
				return original.copy();
			return inv.getStackInSlot(index);
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			return ItemStack.EMPTY;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
		}

		@Override
		public void markDirty() {
		}

		@Override
		public boolean isUsableByPlayer(PlayerEntity player) {
			return inv.isUsableByPlayer(player);
		}

	}

	public void tick() {
		boolean flag = this.isBurning();
		boolean flag1 = false;
		if (this.isBurning()) {
			--this.burnTime;
		}

		if (!this.world.isRemote) {
			ItemStack itemstack = this.items.get(1);
			ItemStack ingre = this.items.get(0);

			if (this.isBurning() || !itemstack.isEmpty() && !this.items.get(0).isEmpty()) {
				IRecipe<?> irecipe = this.world.getRecipeManager()
						.getRecipe((IRecipeType<AbstractCookingRecipe>) this.recipeType,
								new InvWrapper(this, ItemCompressed.getOriginal(ingre)), this.world)
						.orElse(null);
				if (!this.isBurning() && this.canSmelt(irecipe,ingre)) {
					this.burnTime = this.getBurnTime(itemstack);
					this.recipesUsed = this.burnTime;
					if (this.isBurning()) {
						flag1 = true;
						if (itemstack.hasContainerItem())
							this.items.set(1, ItemCompressed.createCompressed(itemstack.getContainerItem().copy(),getTime()));
						else if (!itemstack.isEmpty()) {
							Item item = itemstack.getItem();
							itemstack.shrink(1);
							if (itemstack.isEmpty()) {
								this.items.set(1, ItemCompressed.createCompressed(itemstack.getContainerItem().copy(),getTime()));
							}
						}
					}
				}

				if (this.isBurning() && this.canSmelt(irecipe, ingre)) {
					++this.cookTime;
					if (this.cookTime == this.cookTimeTotal) {
						this.cookTime = 0;
						this.cookTimeTotal = this.func_214005_h();
						this.smelt(irecipe,ingre);
						flag1 = true;
					}
				} else {
					this.cookTime = 0;
				}
			} else if (!this.isBurning() && this.cookTime > 0) {
				this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
			}

			if (flag != this.isBurning()) {
				flag1 = true;
				//this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT,
				//	Boolean.valueOf(this.isBurning())), 3);
				BlockCompressed.setBlockStateIn(world, pos,
						data.getState().with(AbstractFurnaceBlock.LIT,
								Boolean.valueOf(this.isBurning())));
				/*this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT,
						Boolean.valueOf(this.isBurning())), 3);*/
			}
		}

		if (flag1) {
			this.markDirty();
		}

	}

	public int getTime() {
		return ItemCompressed.getTime(getCompressedData().getStack());
	}

	protected boolean canSmelt(@Nullable IRecipe<?> recipeIn, ItemStack stack) {
		if (!this.items.get(0).isEmpty() && recipeIn != null) {
			ItemStack reciperesult = ItemCompressed.createCompressed(recipeIn.getRecipeOutput(), getTime());
			if (recipeIn.getRecipeOutput().isEmpty() || getTime() != ItemCompressed.getTime(stack)) {
				return false;
			} else {
				ItemStack smelted = this.items.get(2);
				if (smelted.isEmpty()) {
					return true;
				} else if (!smelted.isItemEqual(reciperesult)) {
					return false;
				} else if (smelted.getCount() + reciperesult.getCount() <= this.getInventoryStackLimit()
						&& smelted.getCount() + reciperesult.getCount() <= smelted.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
					return true;
				} else {
					return smelted.getCount() + reciperesult.getCount() <= reciperesult.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
				}
			}
		} else {
			return false;
		}
	}

	private void smelt(@Nullable IRecipe<?> p_214007_1_, ItemStack stack) {
		if (p_214007_1_ != null && this.canSmelt(p_214007_1_, stack)) {
			ItemStack material = this.items.get(0);
			ItemStack recipeResult = ItemCompressed.createCompressed(p_214007_1_.getRecipeOutput(), getTime());
			ItemStack smelted = this.items.get(2);
			if (smelted.isEmpty()) {
				this.items.set(2, recipeResult.copy());
				//} else if (smelted.getItem() == recipeResult.getItem()) {
			} else if (NECRecipe.areItemStacksEqual(recipeResult, smelted)) {
				smelted.grow(recipeResult.getCount());
			}
			material.shrink(1);
		}
	}

	protected int getBurnTime(ItemStack p_213997_1_) {
		if (p_213997_1_.isEmpty()) {
			return 0;
		} else {
			return net.minecraftforge.common.ForgeHooks.getBurnTime(ItemCompressed.getOriginal(p_213997_1_));
		}
	}

	protected int func_214005_h() {
		return this.world.getRecipeManager()
				.getRecipe((IRecipeType<AbstractCookingRecipe>) this.recipeType, this, this.world)
				.map(AbstractCookingRecipe::getCookTime).orElse(200);
	}

	public boolean isFuel(ItemStack p_213991_0_) {
		return net.minecraftforge.common.ForgeHooks.getBurnTime(ItemCompressed.getOriginal(p_213991_0_)) > 0
				&& ItemCompressed.getTime(p_213991_0_) == getTime();
	}

	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.DOWN) {
			return SLOTS_DOWN;
		} else {
			return side == Direction.UP ? SLOTS_UP : SLOTS_HORIZONTAL;
		}
	}

	public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
		return this.isItemValidForSlot(index, itemStackIn);
	}

	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		if (direction == Direction.DOWN && index == 1) {
			Item item = stack.getItem();
			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
				return false;
			}
		}

		return true;
	}

	public int getSizeInventory() {
		return this.items.size();
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public ItemStack getStackInSlot(int index) {
		return this.items.get(index);
	}

	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(this.items, index, count);
	}

	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.items, index);
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemstack = this.items.get(index);
		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack)
				&& ItemStack.areItemStackTagsEqual(stack, itemstack);
		this.items.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}

		if (index == 0 && !flag) {
			this.cookTimeTotal = this.func_214005_h();
			this.cookTime = 0;
			this.markDirty();
		}

	}

	public boolean isUsableByPlayer(PlayerEntity player) {
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
					(double) this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	public boolean isItemValidForSlot(int index, ItemStack stack) {
		/*if (index == 2) {
			return false;
		} else if (index != 1) {
			return true;
		} else {
			ItemStack itemstack = this.items.get(1);
			return isFuel(stack) || stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
		}*/

		switch (index) {
		case 0:
			return ItemCompressed.getTime(stack) == getTime();
		case 1:
			return isFuel(stack);
		default:
			return false;
		}
	}

	public void clear() {
		this.items.clear();
	}

	public void onCrafting(PlayerEntity player) {
	}

	net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers = net.minecraftforge.items.wrapper.SidedInvWrapper
			.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(
			net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
		if (!this.removed && facing != null
				&& capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == Direction.UP)
				return handlers[0].cast();
			else if (facing == Direction.DOWN)
				return handlers[1].cast();
			else
				return handlers[2].cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void remove() {
		super.remove();
		for (int x = 0; x < handlers.length; x++)
			handlers[x].invalidate();
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.furnace");
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new CompressedFurnaceContainer(BlockRegister.CONTAINERTYPE_FURNACE, recipeType, p_createMenu_1_,
				p_createMenu_2_, this,
				this.furnaceData);
	}

	private static void func_214003_a(PlayerEntity p_214003_0_, int p_214003_1_, float p_214003_2_) {
		if (p_214003_2_ == 0.0F) {
			p_214003_1_ = 0;
		} else if (p_214003_2_ < 1.0F) {
			int i = MathHelper.floor((float) p_214003_1_ * p_214003_2_);
			if (i < MathHelper.ceil((float) p_214003_1_ * p_214003_2_)
					&& Math.random() < (double) ((float) p_214003_1_ * p_214003_2_ - (float) i)) {
				++i;
			}

			p_214003_1_ = i;
		}

		while (p_214003_1_ > 0) {
			int j = ExperienceOrbEntity.getXPSplit(p_214003_1_);
			p_214003_1_ -= j;
			p_214003_0_.world.addEntity(new ExperienceOrbEntity(p_214003_0_.world, p_214003_0_.getPositionVec().x,
					p_214003_0_.getPositionVec().y + 0.5D, p_214003_0_.getPositionVec().z + 0.5D, j));
		}

	}

}
