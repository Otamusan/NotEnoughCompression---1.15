package otamusan.nec.register;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.ContainerType.IFactory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.block.BlockCompressedBrewingStand;
import otamusan.nec.block.BlockCompressedCrops;
import otamusan.nec.block.BlockCompressedFurnace;
import otamusan.nec.block.BlockCompressedSapling;
import otamusan.nec.block.tileentity.TileCompressedBlock;
import otamusan.nec.block.tileentity.compressedbrewingstand.CompressedBrewingStandContainer;
import otamusan.nec.block.tileentity.compressedbrewingstand.TileCompressedBrewingStand;
import otamusan.nec.block.tileentity.compressedfurnace.CompressedFurnaceContainer;
import otamusan.nec.block.tileentity.compressedfurnace.TileCompressedFurnace;
import otamusan.nec.common.Lib;
import otamusan.nec.config.ConfigCommon;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegister {

	public static BlockCompressed BLOCK_COMPRESSED;
	public static BlockCompressed BLOCK_COMPRESSEDFURNACE;
	public static BlockCompressed BLOCK_COMPRESSEDBREWINGSTAND;
	public static BlockCompressed BLOCK_COMPRESSEDSAPLING;
	public static BlockCompressed BLOCK_COMPRESSEDCROPS;

	public static TileEntityType<TileCompressedBlock> TILECOMPRESSEDTYPE;
	public static TileEntityType<TileCompressedFurnace> TILECOMPRESSEDFURNACETYPE;
	public static TileEntityType<TileCompressedBrewingStand> TILECOMPRESSEDBREWTINGSTANDETYPE;

	public static ContainerType<CompressedFurnaceContainer> CONTAINERTYPE_FURNACE = new ContainerType<CompressedFurnaceContainer>(
			new IFactory<CompressedFurnaceContainer>() {
				@Override
				public CompressedFurnaceContainer create(int p_create_1_, PlayerInventory p_create_2_) {
					return new CompressedFurnaceContainer(CONTAINERTYPE_FURNACE, IRecipeType.SMELTING, p_create_1_,
							p_create_2_);
				}
			});
	public static ContainerType<CompressedBrewingStandContainer> CONTAINERTYPE_BREWINGSTAND = new ContainerType<CompressedBrewingStandContainer>(
			new IFactory<CompressedBrewingStandContainer>() {
				@Override
				public CompressedBrewingStandContainer create(int p_create_1_, PlayerInventory p_create_2_) {
					return new CompressedBrewingStandContainer(p_create_1_,
							p_create_2_);
				}
			});

	@SubscribeEvent
	public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
		BLOCK_COMPRESSED = (BlockCompressed) new BlockCompressed(Block.Properties.create(Material.MISCELLANEOUS))
				.setRegistryName(Lib.BLOCK_COMPRESSED);
		BLOCK_COMPRESSEDFURNACE = (BlockCompressed) new BlockCompressedFurnace(
				Block.Properties.create(Material.MISCELLANEOUS).variableOpacity())
						.setRegistryName(Lib.BLOCK_COMPRESSEDFURNACE);
		BLOCK_COMPRESSEDBREWINGSTAND = (BlockCompressed) new BlockCompressedBrewingStand(
				Block.Properties.create(Material.MISCELLANEOUS).variableOpacity())
						.setRegistryName(Lib.BLOCK_COMPRESSEDBREWINGSTAND);
		BLOCK_COMPRESSEDSAPLING = (BlockCompressed) new BlockCompressedSapling(
				Block.Properties.create(Material.MISCELLANEOUS).tickRandomly().variableOpacity())
						.setRegistryName(Lib.BLOCK_COMPRESSEDSAPLING);
		BLOCK_COMPRESSEDCROPS = (BlockCompressed) new BlockCompressedCrops(
				Block.Properties.create(Material.MISCELLANEOUS).tickRandomly().variableOpacity())
						.setRegistryName(Lib.BLOCK_COMPRESSEDCROPS);
		if (ConfigCommon.CONFIG_COMMON.specializeCompressedFurnace.get())
			BLOCK_COMPRESSED.addChildren(BLOCK_COMPRESSEDFURNACE);
		if (ConfigCommon.CONFIG_COMMON.specializeCompressedBrewingStand.get())
			BLOCK_COMPRESSED.addChildren(BLOCK_COMPRESSEDBREWINGSTAND);
		if (ConfigCommon.CONFIG_COMMON.specializeCompressedSapling.get())
			BLOCK_COMPRESSED.addChildren(BLOCK_COMPRESSEDSAPLING);
		if (ConfigCommon.CONFIG_COMMON.specializeCompressedCrops.get())
			BLOCK_COMPRESSED.addChildren(BLOCK_COMPRESSEDCROPS);

		blockRegistryEvent.getRegistry().registerAll(BLOCK_COMPRESSED, BLOCK_COMPRESSEDFURNACE,
				BLOCK_COMPRESSEDBREWINGSTAND, BLOCK_COMPRESSEDSAPLING, BLOCK_COMPRESSEDCROPS);
	}

	@SubscribeEvent
	public static void registerTE(RegistryEvent.Register<TileEntityType<?>> evt) {
		TILECOMPRESSEDTYPE = TileEntityType.Builder
				.create(TileCompressedBlock::new, BlockRegister.BLOCK_COMPRESSED)
				.build(null);
		TILECOMPRESSEDFURNACETYPE = TileEntityType.Builder
				.create(TileCompressedFurnace::new, BlockRegister.BLOCK_COMPRESSEDFURNACE).build(null);
		TILECOMPRESSEDBREWTINGSTANDETYPE = TileEntityType.Builder
				.create(TileCompressedBrewingStand::new, BlockRegister.BLOCK_COMPRESSEDBREWINGSTAND).build(null);

		TILECOMPRESSEDTYPE.setRegistryName(Lib.TILETYPE_COMPRESSEDBLOCK);
		TILECOMPRESSEDFURNACETYPE.setRegistryName(Lib.TILETYPE_COMPRESSEDBLOCKFURNACE);
		TILECOMPRESSEDBREWTINGSTANDETYPE.setRegistryName(Lib.TILETYPE_COMPRESSEDBLOCKBREWINGSTAND);

		evt.getRegistry().registerAll(TILECOMPRESSEDTYPE, TILECOMPRESSEDFURNACETYPE, TILECOMPRESSEDBREWTINGSTANDETYPE);

	}

	@SubscribeEvent
	public static void registerContainer(RegistryEvent.Register<ContainerType<?>> evt) {
		CONTAINERTYPE_FURNACE.setRegistryName(Lib.CONTAINERTYPE_FURNACE);
		evt.getRegistry().register(CONTAINERTYPE_FURNACE);
		CONTAINERTYPE_BREWINGSTAND.setRegistryName(Lib.CONTAINERTYPE_BREWINGSTAND);
		evt.getRegistry().register(CONTAINERTYPE_BREWINGSTAND);
	}
}
