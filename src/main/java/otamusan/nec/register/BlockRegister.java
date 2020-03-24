package otamusan.nec.register;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.ContainerType.IFactory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.block.BlockCompressedFurnace;
import otamusan.nec.block.tileentity.TileCompressedBlock;
import otamusan.nec.block.tileentity.compressedfurnace.CompressedFurnaceContainer;
import otamusan.nec.block.tileentity.compressedfurnace.TileCompressedFurnace;
import otamusan.nec.common.Lib;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegister {

	public static BlockCompressed BLOCK_COMPRESSED;
	public static BlockCompressed BLOCK_COMPRESSEDFURNACE;

	public static TileEntityType<TileCompressedBlock> TILECOMPRESSEDTYPE;
	public static TileEntityType<TileCompressedFurnace> TILECOMPRESSEDFURNACETYPE;

	public static ContainerType<CompressedFurnaceContainer> CONTAINERTYPE_FURNACE = new ContainerType<CompressedFurnaceContainer>(
			new IFactory<CompressedFurnaceContainer>() {
				@Override
				public CompressedFurnaceContainer create(int p_create_1_, PlayerInventory p_create_2_) {
					return new CompressedFurnaceContainer(CONTAINERTYPE_FURNACE, IRecipeType.SMELTING, p_create_1_,
							p_create_2_);
				}
			});

	@SubscribeEvent
	public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
		BLOCK_COMPRESSED = (BlockCompressed) new BlockCompressed()
				.setRegistryName(Lib.BLOCK_COMPRESSED);
		BLOCK_COMPRESSEDFURNACE = (BlockCompressed) new BlockCompressedFurnace()
				.setRegistryName(Lib.BLOCK_COMPRESSEDFURNACE);
		//BLOCK_COMPRESSED.addChildren(BLOCK_COMPRESSEDFURNACE);
		blockRegistryEvent.getRegistry().registerAll(BLOCK_COMPRESSED, BLOCK_COMPRESSEDFURNACE);
	}

	@SubscribeEvent
	public static void registerTE(RegistryEvent.Register<TileEntityType<?>> evt) {
		TILECOMPRESSEDTYPE = TileEntityType.Builder
				.create(TileCompressedBlock::new, BlockRegister.BLOCK_COMPRESSED)
				.build(null);
		TILECOMPRESSEDFURNACETYPE = TileEntityType.Builder
				.create(TileCompressedFurnace::new, BlockRegister.BLOCK_COMPRESSEDFURNACE).build(null);

		TILECOMPRESSEDTYPE.setRegistryName(Lib.TILETYPE_COMPRESSEDBLOCK);
		TILECOMPRESSEDFURNACETYPE.setRegistryName(Lib.TILETYPE_COMPRESSEDBLOCKFURNACE);

		evt.getRegistry().registerAll(TILECOMPRESSEDTYPE, TILECOMPRESSEDFURNACETYPE);

	}

	@SubscribeEvent
	public static void registerContainer(RegistryEvent.Register<ContainerType<?>> evt) {
		CONTAINERTYPE_FURNACE.setRegistryName(Lib.CONTAINERTYPE_FURNACE);
		evt.getRegistry().register(CONTAINERTYPE_FURNACE);
	}
}
