package otamusan.nec.common;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.block.tileentity.ITileCompressed;
import otamusan.nec.item.ItemCompressed;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BreakCompressedBlock {

	@SubscribeEvent
	public static void onBreak(BreakEvent event) {
		ItemStack stack = BlockCompressed.getOriginalItem(event.getWorld(), event.getPos());
		ItemStack tool = event.getPlayer().getHeldItem(event.getPlayer().getActiveHand());

		if (!BlockCompressed
				.isCompressedBlock(BlockCompressed.getOriginalState(event.getWorld(), event.getPos()).getBlock()))
			return;
		if (!(event.getWorld().getTileEntity(event.getPos()) instanceof ITileCompressed))
			return;
		if (!((ITileCompressed) event.getWorld().getTileEntity(event.getPos())).isNatural())
			return;

		tool.damageItem((int) ItemCompressed.getTotal(stack) - 1, event.getPlayer(), (p_220039_0_) -> {
			p_220039_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		});
	}
}
