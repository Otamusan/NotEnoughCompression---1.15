package otamusan.nec.item.Using;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import otamusan.nec.common.CompressedItems;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.recipe.NECRecipe;

public class CompressedUsing {
	public static ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack compressed = playerIn.getHeldItem(handIn);
		ItemStack original = ItemCompressed.getOriginal(compressed);
		int time = ItemCompressed.getTime(compressed);
		int count = (int) Math.pow(8, time);
		Remains remains = new Remains();
		ActionResultType type = ActionResultType.FAIL;

		for (int i = 0; i < count; i++) {
			ItemStack using = original.copy();
			using.setCount(1);

			setHeldItem(handIn, using, playerIn);
			ActionResult<ItemStack> result = using.getItem().onItemRightClick(worldIn, playerIn, handIn);
			if (result.getType() == ActionResultType.SUCCESS)
				type = ActionResultType.SUCCESS;

			ItemStack remain = result.getResult().copy();
			remains.addItem(remain);

			setHeldItem(handIn, ItemStack.EMPTY, playerIn);
		}

		List<ItemStack> items = remains.getAllItems();

		if (items.size() == 1 && NECRecipe.areItemStacksEqual(items.get(0), compressed)) {
		} else {
			compressed.shrink(1);
			putItemsToPlayer(playerIn, items);
		}
		setHeldItem(handIn, compressed, playerIn);

		return new ActionResult<ItemStack>(type, compressed);
	}

	public static ActionResultType onItemUse(ItemUseContext context) {
		ItemStack compressed = context.getItem();
		ItemStack original = ItemCompressed.getOriginal(compressed);
		int time = ItemCompressed.getTime(compressed);
		int count = (int) Math.pow(8, time);
		PlayerEntity playerIn = context.getPlayer();
		Hand handIn = context.getHand();
		World worldIn = context.getWorld();
		Remains remains = new Remains();
		ActionResultType type = ActionResultType.FAIL;

		for (int i = 0; i < count; i++) {
			ItemStack using = original.copy();
			using.setCount(1);

			setHeldItem(handIn, using, playerIn);
			ItemUseContext replaced = getItemReplaced(context, using);
			ActionResultType result = using.getItem().onItemUse(replaced);
			if (result == ActionResultType.SUCCESS)
				type = ActionResultType.SUCCESS;

			ItemStack remain = playerIn.getHeldItem(handIn);
			remains.addItem(remain);

			setHeldItem(handIn, ItemStack.EMPTY, playerIn);
		}

		List<ItemStack> items = remains.getAllItems();

		if (items.size() == 1 && NECRecipe.areItemStacksEqual(items.get(0), compressed)) {

		} else {
			compressed.shrink(1);
			putItemsToPlayer(playerIn, items);
		}
		setHeldItem(handIn, compressed, playerIn);

		return type;
	}

	public static void setHeldItem(Hand hand, ItemStack stack, PlayerEntity entity) {
		//entity.setHeldItem(hand, stack);
		if (hand == Hand.MAIN_HAND) {
			setItemStackToSlot(EquipmentSlotType.MAINHAND, stack, entity);
		} else {
			if (hand != Hand.OFF_HAND) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}
			setItemStackToSlot(EquipmentSlotType.OFFHAND, stack, entity);
		}
	}

	public static void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack, PlayerEntity entity) {
		if (slotIn == EquipmentSlotType.MAINHAND) {
			//entity.playEquipSound(stack);
			entity.inventory.mainInventory.set(entity.inventory.currentItem, stack);
		} else if (slotIn == EquipmentSlotType.OFFHAND) {
			//entity.playEquipSound(stack);
			entity.inventory.offHandInventory.set(0, stack);
		} else if (slotIn.getSlotType() == EquipmentSlotType.Group.ARMOR) {
			//entity.playEquipSound(stack);
			entity.inventory.armorInventory.set(slotIn.getIndex(), stack);
		}

	}

	public static ItemUseContext getItemReplaced(ItemUseContext context, ItemStack stack) {
		return new UseContextWrapper(context, stack);
	}

	public static void putItemsToPlayer(LivingEntity player, List<ItemStack> stacks) {
		for (ItemStack itemStack : stacks) {
			ItemEntity entity = new ItemEntity(player.world, player.getPositionVec().x, player.getPositionVec().y,
					player.getPositionVec().z, itemStack.copy());
			entity.setPickupDelay(0);
			player.world.addEntity(entity);
		}
	}

	//Manage items after using
	public static class Remains {
		//Use CompressedItems each of original Itemstack
		private List<CompressedItems> remains = new ArrayList<CompressedItems>();

		public void addItem(ItemStack stack) {
			if (isItemHolding(stack)) {
				getCompressedItems(stack).addCompressed(stack.copy());
			} else {
				CompressedItems compressedItems = new CompressedItems(stack.copy());
				compressedItems.addCompressed(stack.copy());
				remains.add(compressedItems);
			}
		}

		public boolean isItemHolding(ItemStack any) {
			for (CompressedItems compressedItems : remains) {
				if (compressedItems.isContainable(any))
					return true;
			}
			return false;
		}

		public CompressedItems getCompressedItems(ItemStack any) {
			for (CompressedItems compressedItems : remains) {
				if (compressedItems.isContainable(any))
					return compressedItems;
			}
			return new CompressedItems(any);
		}

		public List<ItemStack> getAllItems() {
			List<ItemStack> stacks = new ArrayList<>();
			for (CompressedItems compressedItems : remains) {
				stacks.addAll(compressedItems.getCompressed());
			}
			return stacks;
		}
	}
}
