package otamusan.nec.recipe;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;

public abstract class NECRecipe extends SpecialRecipe {
	public NECRecipe(ResourceLocation rs) {
		super(rs);
	}

	public static Iterable<ItemStack> getIterator(CraftingInventory inv) {
		return new Iterable<ItemStack>() {

			@Override
			public Iterator<ItemStack> iterator() {
				return new Iterator<ItemStack>() {
					private int i = 0;
					private CraftingInventory iteinv = inv;

					@Override
					public boolean hasNext() {
						return i < iteinv.getSizeInventory();
					}

					@Override
					public ItemStack next() {
						i++;
						return iteinv.getStackInSlot(i);
					}
				};
			}
		};

	}

	public static CraftingInventory CICopy(CraftingInventory inv) {
		CraftingInventory newinv = new CraftingInventory(new Container(ContainerType.CRAFTING, 0) {
			@Override
			public boolean canInteractWith(PlayerEntity playerIn) {
				return false;
			}
		}, inv.getWidth(), inv.getHeight());

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i).isEmpty())
				continue;
			newinv.setInventorySlotContents(i, inv.getStackInSlot(i).copy());
		}
		return newinv;
	}

	public static int getSlot(CraftingInventory inv, ItemStack itemStack) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) == itemStack)
				return i;
		}
		return -1;
	}

	public static int getSlot(CraftingInventory inv, Function<ItemStack, Boolean> func) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (func.apply(inv.getStackInSlot(i)))
				return i;
		}
		return -1;
	}

	public static ItemStack getStack(CraftingInventory inv, Function<ItemStack, Boolean> func) {
		int i = getSlot(inv, func);
		if (i == -1)
			return ItemStack.EMPTY;
		return inv.getStackInSlot(i);
	}

	public static int getStackedSlotCount(CraftingInventory inv) {
		return getSlotCount(inv, stack -> !stack.isEmpty());
	}

	public static int getSlotCount(CraftingInventory inv, Function<ItemStack, Boolean> fuc) {
		int n = 0;
		for (ItemStack stack : getIterator(inv)) {
			if (fuc.apply(stack))
				n++;
		}
		return n;
	}

	public static int getSlotCount(CraftingInventory inv, BiFunction<Integer, ItemStack, Boolean> fuc) {
		int n = 0;
		int i = 0;
		for (ItemStack stack : getIterator(inv)) {
			if (fuc.apply(i, stack))
				n++;
			i++;
		}
		return n;
	}

	//ignore stacks size
	public static boolean areItemStacksEqual(ItemStack stack, ItemStack stack2) {
		ItemStack stack3 = stack.copy();
		stack3.setCount(1);
		ItemStack stack4 = stack2.copy();
		stack4.setCount(1);

		return ItemStack.areItemStacksEqual(stack3, stack4);
	}

	public static boolean isMatchStacks(CraftingInventory inv, int[] array, ItemStack stack) {
		for (int i = 0; i < array.length; i++) {
			int j = array[i];
			if (!areItemStacksEqual(stack, inv.getStackInSlot(j)))
				return false;
		}
		return true;
	}
}
