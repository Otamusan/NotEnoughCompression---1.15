package otamusan.nec.item;

import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class ItemCompressedTool extends ItemCompressed {
	@Override
	public boolean isAvailable(Item item) {
		return !item.getToolTypes(new ItemStack(item)).isEmpty();
	}

	@Override
	public String getCompressedName() {
		return "tool";
	}

	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return ItemCompressed.getOriginal(stack).getDestroySpeed(state) * getCompressedModi(stack);
	}

	public static float getCompressedModi(ItemStack stack) {
		return (float) Math.pow(1.5f, getTime(stack));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		return super.onItemUse(context);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damageItem(2, attacker, (p_220039_0_) -> {
			p_220039_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		});

		return true;
	}

	@Override
	public ItemStack onCompress(ItemStack original, ItemStack compressed) {
		compressed.setDamage((int) (original.getDamage() * getCompressedModi(compressed)));
		return compressed;
	}

	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos,
			LivingEntity entityLiving) {
		if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
			stack.damageItem(1, entityLiving, (p_220038_0_) -> {
				p_220038_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		}
		return true;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		syncDamage(stack);
		return super.damageItem(stack, amount, entity, onBroken);
	}

	public static void syncDamage(ItemStack compressed) {
		ItemStack original = ItemCompressed.getOriginal(compressed);
		original.setDamage((int) (compressed.getDamage() / getCompressedModi(compressed)));
		ItemCompressed.setOriginal(compressed, original);
	}

	@Override
	public boolean canHarvestBlock(ItemStack stack, BlockState state) {
		return getOriginal(stack).getItem().canHarvestBlock(getOriginal(stack), state);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, ToolType tool, PlayerEntity player, BlockState blockState) {
		return getOriginal(stack).getHarvestLevel(tool, player, blockState);
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public boolean isDamaged(ItemStack stack) {
		return super.isDamaged(stack);
	}

	@Override
	public Set<ToolType> getToolTypes(ItemStack stack) {
		return getOriginal(stack).getToolTypes();
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		if (slot == EquipmentSlotType.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER,
					"Tool modifier", getAttackDamage(slot, stack) * getCompressedModi(stack),
					AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER,
					"Tool modifier", getAttackSpeed(slot, stack), AttributeModifier.Operation.ADDITION));
		}
		return multimap;
	}

	public double getAttackDamage(EquipmentSlotType type, ItemStack stack) {
		ItemStack original = ItemCompressed.getOriginal(stack);
		for (AttributeModifier modifier : original.getAttributeModifiers(type)
				.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
			if (modifier.getID() == ATTACK_DAMAGE_MODIFIER) {
				return modifier.getAmount();
			}
		}
		return 0;
	}

	public double getAttackSpeed(EquipmentSlotType type, ItemStack stack) {
		ItemStack original = ItemCompressed.getOriginal(stack);
		for (AttributeModifier modifier : original.getAttributeModifiers(type)
				.get(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
			if (modifier.getID() == ATTACK_SPEED_MODIFIER) {
				return modifier.getAmount();
			}
		}
		return 0;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return (int) (ItemCompressed.getOriginal(stack).getMaxDamage() * getCompressedModi(stack));
	}

	@Override
	public int getDamage(ItemStack stack) {
		return super.getDamage(stack);
	}

}
