package otamusan.nec.common;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.item.ItemCompressed;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BreakCompressedBlock {

	@SubscribeEvent
	public static void onBreak(BreakEvent event) {
		ItemStack stack = BlockCompressed.getOriginalItem(event.getWorld(), event.getPos());
		ItemStack tool = event.getPlayer().getHeldItem(event.getPlayer().getActiveHand());

		//for (int i = 0; i < ItemCompressed.getTotal(stack); i++) {
		//	tool.onBlockDestroyed(event.getWorld().getWorld(), event.getWorld().getBlockState(event.getPos()),
		//			event.getPos(), event.getPlayer());
		//	//ToolItem
		//}
		tool.damageItem((int) ItemCompressed.getTotal(stack), event.getPlayer(), (p_220039_0_) -> {
			p_220039_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		});
	}
}
