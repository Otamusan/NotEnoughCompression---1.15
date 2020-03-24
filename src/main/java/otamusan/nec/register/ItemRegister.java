package otamusan.nec.register;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import otamusan.nec.common.Lib;
import otamusan.nec.item.ItemBlockCompressed;
import otamusan.nec.item.ItemCompressed;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegister {
	public static ItemCompressed ITEM_COMPRESSED;
	public static ItemBlockCompressed ITEM_BLOCKCOMPRESSED;

	@SubscribeEvent
	public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {

		ITEM_COMPRESSED = (ItemCompressed) new ItemCompressed()
				.setRegistryName(Lib.ITEM_COMPRESSED);
		ITEM_BLOCKCOMPRESSED = (ItemBlockCompressed) new ItemBlockCompressed()
				.setRegistryName(Lib.ITEM_BLOCKCOMPRESSED);
		itemRegistryEvent.getRegistry().registerAll(
				ITEM_COMPRESSED, ITEM_BLOCKCOMPRESSED);
		ITEM_COMPRESSED.addChildren(ITEM_BLOCKCOMPRESSED);

	}
}
