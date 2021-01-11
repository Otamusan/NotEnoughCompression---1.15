package otamusan.nec.register;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import otamusan.nec.common.Lib;
import otamusan.nec.config.ConfigCommon;
import otamusan.nec.item.ItemBlockCompressed;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.item.ItemCompressedSword;
import otamusan.nec.item.ItemCompressedTool;
import otamusan.nec.item.ItemFluidUnit;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegister {
	public static ItemCompressed ITEM_COMPRESSED;
	public static ItemBlockCompressed ITEM_BLOCKCOMPRESSED;
	public static ItemCompressedTool ITEM_COMPRESSEDTOOL;
	public static ItemCompressedSword ITEM_COMPRESSEDSWORD;

	public static ItemFluidUnit ITEM_FLUIDUNIT;

	@SubscribeEvent
	public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {

		ITEM_COMPRESSED = (ItemCompressed) new ItemCompressed()
				.setRegistryName(Lib.ITEM_COMPRESSED);
		ITEM_BLOCKCOMPRESSED = (ItemBlockCompressed) new ItemBlockCompressed()
				.setRegistryName(Lib.ITEM_BLOCKCOMPRESSED);
		ITEM_COMPRESSEDTOOL = (ItemCompressedTool) new ItemCompressedTool()
				.setRegistryName(Lib.ITEM_COMPRESSEDTOOL);
		ITEM_COMPRESSEDSWORD = (ItemCompressedSword) new ItemCompressedSword()
				.setRegistryName(Lib.ITEM_COMPRESSEDSWORD);

		ITEM_FLUIDUNIT = (ItemFluidUnit) new ItemFluidUnit().setRegistryName(Lib.ITEM_FLUIDUNIT);

		itemRegistryEvent.getRegistry().registerAll(
				ITEM_COMPRESSED, ITEM_BLOCKCOMPRESSED, ITEM_COMPRESSEDTOOL, ITEM_COMPRESSEDSWORD, ITEM_FLUIDUNIT);
		ITEM_COMPRESSED.addChildren(ITEM_BLOCKCOMPRESSED);
		if (ConfigCommon.CONFIG_COMMON.specializeCompressedTool.get())
			ITEM_COMPRESSED.addChildren(ITEM_COMPRESSEDTOOL);
		if (ConfigCommon.CONFIG_COMMON.specializeCompressedSword.get())
			ITEM_COMPRESSED.addChildren(ITEM_COMPRESSEDSWORD);

	}
}
