package otamusan.nec.block.tileentity.compressedbrewingstand;

import java.util.Arrays;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.block.tileentity.TileCompressedBlock;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.register.BlockRegister;

public class TileCompressedBrewingStand extends TileCompressedBlock
		implements IInventory, ISidedInventory, ITickableTileEntity, INamedContainerProvider {
	private static final int[] SLOTS_FOR_UP = new int[] { 3 };
	private static final int[] SLOTS_FOR_DOWN = new int[] { 0, 1, 2, 3 };
	private static final int[] OUTPUT_SLOTS = new int[] { 0, 1, 2, 4 };
	private NonNullList<ItemStack> brewingItemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
	private int brewTime;
	private boolean[] filledSlots;
	private Item ingredientID;
	private int fuel;

	protected final IIntArray field_213954_a = new IIntArray() {
		public int get(int index) {
			switch (index) {
			case 0:
				return TileCompressedBrewingStand.this.brewTime;
			case 1:
				return TileCompressedBrewingStand.this.fuel;
			default:
				return 0;
			}
		}

		public void set(int index, int value) {
			switch (index) {
			case 0:
				TileCompressedBrewingStand.this.brewTime = value;
				break;
			case 1:
				TileCompressedBrewingStand.this.fuel = value;
			}

		}

		public int size() {
			return 2;
		}
	};

	@Override
	public TileEntityType<?> getType() {
		return BlockRegister.TILECOMPRESSEDBREWTINGSTANDETYPE;
	}

	public int getSizeInventory() {
		return this.brewingItemStacks.size();
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : this.brewingItemStacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void tick() {
		ItemStack itemstack = this.brewingItemStacks.get(4);
		if (this.fuel <= 0 && ItemCompressed.getOriginal(itemstack).getItem() == Items.BLAZE_POWDER && ItemCompressed.getTime(itemstack)==getTime()) {
			this.fuel = 20;
			itemstack.shrink(1);
			this.markDirty();
		}

		boolean flag = this.canBrew();
		boolean flag1 = this.brewTime > 0;
		ItemStack itemstack1 = this.brewingItemStacks.get(3);
		if (flag1) {
			--this.brewTime;
			boolean flag2 = this.brewTime == 0;
			if (flag2 && flag) {
				this.brewPotions();
				this.markDirty();
			} else if (!flag) {
				this.brewTime = 0;
				this.markDirty();
			} else if (this.ingredientID != ItemCompressed.getOriginal(itemstack1).getItem()) {
				this.brewTime = 0;
				this.markDirty();
			}
		} else if (flag && this.fuel > 0) {
			--this.fuel;
			this.brewTime = 400;
			this.ingredientID = ItemCompressed.getOriginal(itemstack1).getItem();
			this.markDirty();
		}

		if (!this.world.isRemote) {
			boolean[] aboolean = this.createFilledSlotsArray();
			if (!Arrays.equals(aboolean, this.filledSlots)) {
				this.filledSlots = aboolean;
				BlockState blockstate = BlockCompressed.getBlockStateIn(world, pos);
				if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
					return;
				}

				for (int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
					blockstate = blockstate.with(BrewingStandBlock.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
				}

				//this.world.setBlockState(this.pos, blockstate, 2);
				BlockCompressed.setBlockStateIn(world, pos, blockstate);
			}
		}

	}

	public boolean[] createFilledSlotsArray() {
		boolean[] aboolean = new boolean[3];

		for (int i = 0; i < 3; ++i) {
			if (!this.brewingItemStacks.get(i).isEmpty()) {
				aboolean[i] = true;
			}
		}

		return aboolean;
	}

	private boolean canBrew() {
		ItemStack itemstack = this.brewingItemStacks.get(3);
		NonNullList<ItemStack> stacks = NonNullList.create();
		brewingItemStacks.forEach(item -> {
			stacks.add(ItemCompressed.getOriginal(item));
		});
		if(!brewingItemStacks.stream().allMatch(stack -> (ItemCompressed.getTime(stack)==getTime()) || stack.isEmpty())) return false;
		if (!itemstack.isEmpty())
			return net.minecraftforge.common.brewing.BrewingRecipeRegistry.canBrew(stacks,
					ItemCompressed.getOriginal(itemstack),
					OUTPUT_SLOTS); // divert to VanillaBrewingRegistry
		if (itemstack.isEmpty()) {
			return false;
		} else if (!PotionBrewing.isReagent(ItemCompressed.getOriginal(itemstack))) {
			return false;
		} else {
			for (int i = 0; i < 3; ++i) {
				ItemStack itemstack1 = this.brewingItemStacks.get(i);
				if (!itemstack1.isEmpty() && PotionBrewing.hasConversions(ItemCompressed.getOriginal(itemstack1),
						ItemCompressed.getOriginal(itemstack))) {
					return true;
				}
			}

			return false;
		}
	}

	private void brewPotions() {
		NonNullList<ItemStack> stacks = NonNullList.withSize(5, ItemStack.EMPTY);
		for (int i = 0; i < brewingItemStacks.size(); i++) {
			stacks.set(i, ItemCompressed.getOriginal(brewingItemStacks.get(i)));
		}

		if (net.minecraftforge.event.ForgeEventFactory.onPotionAttemptBrew(stacks))
			return;
		ItemStack itemstack = this.brewingItemStacks.get(3);

		net.minecraftforge.common.brewing.BrewingRecipeRegistry.brewPotions(stacks,
				ItemCompressed.getOriginal(itemstack), OUTPUT_SLOTS);
		for (int i = 0; i < stacks.size(); i++) {
			brewingItemStacks.set(i, ItemCompressed.createCompressed(stacks.get(i), getTime()));
		}
		itemstack.shrink(1);
		net.minecraftforge.event.ForgeEventFactory.onPotionBrewed(stacks);
		BlockPos blockpos = this.getPos();
		/*if (itemstack.hasContainerItem()) {
			ItemStack itemstack1 = itemstack.getContainerItem();
			if (itemstack.isEmpty()) {
				itemstack = itemstack1;
			} else if (!this.world.isRemote) {
				InventoryHelper.spawnItemStack(this.world, (double) blockpos.getX(), (double) blockpos.getY(),
						(double) blockpos.getZ(), itemstack1);
			}
		}*/

		this.brewingItemStacks.set(3, itemstack);
		this.world.playEvent(1035, blockpos, 0);
	}

	public void read(CompoundNBT compound) {
		super.read(compound);
		this.brewingItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, this.brewingItemStacks);
		this.brewTime = compound.getShort("BrewTime");
		this.fuel = compound.getByte("Fuel");
	}

	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putShort("BrewTime", (short) this.brewTime);
		ItemStackHelper.saveAllItems(compound, this.brewingItemStacks);
		compound.putByte("Fuel", (byte) this.fuel);
		return compound;
	}

	public ItemStack getStackInSlot(int index) {
		return index >= 0 && index < this.brewingItemStacks.size() ? this.brewingItemStacks.get(index)
				: ItemStack.EMPTY;
	}

	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(this.brewingItemStacks, index, count);
	}

	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.brewingItemStacks, index);
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index >= 0 && index < this.brewingItemStacks.size()) {
			this.brewingItemStacks.set(index, stack);
		}

	}

	public boolean isUsableByPlayer(PlayerEntity player) {
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
					(double) this.pos.getZ() + 0.5D) > 64.0D);
		}
	}

	public int getTime() {
		return ItemCompressed.getTime(this.getCompressedData().getStack());
	}

	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (index == 3) {
			return net.minecraftforge.common.brewing.BrewingRecipeRegistry
					.isValidIngredient(ItemCompressed.getOriginal(stack)) && ItemCompressed.getTime(stack) == getTime();
		} else {
			if (index == 4) {
				return ItemCompressed.getOriginal(stack).getItem() == Items.BLAZE_POWDER
						&& ItemCompressed.getTime(stack) == getTime();
			} else {
				return net.minecraftforge.common.brewing.BrewingRecipeRegistry
						.isValidInput(ItemCompressed.getOriginal(stack))
						&& this.getStackInSlot(index).isEmpty() && ItemCompressed.getTime(stack) == getTime();
			}
		}
	}

	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.UP) {
			return SLOTS_FOR_UP;
		} else {
			return side == Direction.DOWN ? SLOTS_FOR_DOWN : OUTPUT_SLOTS;
		}
	}

	public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
		return this.isItemValidForSlot(index, itemStackIn);
	}

	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		if (index == 3) {
			return stack.getItem() == Items.GLASS_BOTTLE;
		} else {
			return true;
		}
	}

	public void clear() {
		this.brewingItemStacks.clear();
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
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new CompressedBrewingStandContainer(p_createMenu_1_, p_createMenu_2_, this, field_213954_a);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.brewing");
	}
}
