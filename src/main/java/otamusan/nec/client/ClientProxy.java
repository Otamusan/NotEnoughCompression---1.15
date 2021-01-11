package otamusan.nec.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import otamusan.nec.block.tileentity.ITileCompressed;
import otamusan.nec.block.tileentity.compressedbrewingstand.CompressedBrewingStandContainer;
import otamusan.nec.block.tileentity.compressedbrewingstand.CompressedBrewingStandScreen;
import otamusan.nec.block.tileentity.compressedfurnace.CompressedFurnaceContainer;
import otamusan.nec.block.tileentity.compressedfurnace.CompressedFurnaceScreen;
import otamusan.nec.client.blockcompressed.ModelCompressedBlock;
import otamusan.nec.client.itemcompressed.ModelCompressed;
import otamusan.nec.common.Lib;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.lib.ColorUtil;
import otamusan.nec.network.NECPacketHandler;
import otamusan.nec.register.BlockRegister;
import otamusan.nec.register.ItemRegister;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy {

	public static final IBakedModel MODEL = new ModelCompressedBlock();

	public static IBlockColor blockColor = new IBlockColor() {
		@Override
		public int getColor(BlockState var1, ILightReader var2, BlockPos var3, int var4) {
			ITileCompressed tile = (ITileCompressed) var2.getTileEntity(var3);
			if (tile == null)
				return -1;

			if (var4 == 99) {
				return ColorUtil.getCompressedColor(ItemCompressed.getTime(tile.getCompressedData().getStack()))
						.getRGB();

			} else {

				int originalcolor = Minecraft.getInstance().getBlockColors().getColor(
						tile.getCompressedData().getState(), var2, var3,
						1);
				return ColorUtil
						.getCompressedColor(new Color(originalcolor),
								ItemCompressed.getTime(tile.getCompressedData().getStack()))
						.getRGB();
			}
		}
	};

	public static void setBlockColor(Block block, BlockColors colors) {
		colors.register(blockColor, block);
	}

	//call on FMLClientSetupEvent
	@OnlyIn(Dist.CLIENT)
	public static void addSpecialModels(Block block) {
		for (ModelResourceLocation location : getModelRLs(block)) {
			ModelLoader.addSpecialModel(location);
		}
	}

	//call on ModelBakeEvent
	@OnlyIn(Dist.CLIENT)
	public static void modelRegister(Block block, Map<ResourceLocation, IBakedModel> register) {
		for (ModelResourceLocation location : getModelRLs(block)) {
			register.put(location, MODEL);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static List<ModelResourceLocation> getModelRLs(Block block) {
		List<ModelResourceLocation> resourceLocations = new ArrayList<>();

		for (BlockState state : block.getStateContainer().getValidStates()) {
			resourceLocations.add(BlockModelShapes.getModelLocation(state));
		}

		return resourceLocations;
	}

	@SubscribeEvent
	public static void doClientStuff(final FMLClientSetupEvent event) {
		NECPacketHandler.registerMessage();

		ModelLoader.addSpecialModel(Lib.ITEM_COMPRESSED_MODEL);
		ModelLoader.addSpecialModel(Lib.ITEM_BLOCKCOMPRESSED_MODEL);
		ModelLoader.addSpecialModel(Lib.ITEM_COMPRESSEDTOOL_MODEL);
		ModelLoader.addSpecialModel(Lib.ITEM_COMPRESSEDSWORD_MODEL);

		event.getMinecraftSupplier().get().getItemRenderer().getItemModelMesher().register(ItemRegister.ITEM_COMPRESSED,
				Lib.ITEM_COMPRESSED_MODEL);

		event.getMinecraftSupplier().get().getItemRenderer().getItemModelMesher().register(
				ItemRegister.ITEM_BLOCKCOMPRESSED,
				Lib.ITEM_BLOCKCOMPRESSED_MODEL);

		event.getMinecraftSupplier().get().getItemRenderer().getItemModelMesher().register(
				ItemRegister.ITEM_COMPRESSEDTOOL,
				Lib.ITEM_COMPRESSEDTOOL_MODEL);

		event.getMinecraftSupplier().get().getItemRenderer().getItemModelMesher().register(
				ItemRegister.ITEM_COMPRESSEDSWORD,
				Lib.ITEM_COMPRESSEDSWORD_MODEL);

		event.getMinecraftSupplier().get().getItemRenderer().getItemModelMesher().register(
				ItemRegister.ITEM_FLUIDUNIT,
				Lib.ITEM_FLUIDUNIT_MODEL);

		addSpecialModels(BlockRegister.BLOCK_COMPRESSED);
		addSpecialModels(BlockRegister.BLOCK_COMPRESSEDFURNACE);
		addSpecialModels(BlockRegister.BLOCK_COMPRESSEDBREWINGSTAND);
		addSpecialModels(BlockRegister.BLOCK_COMPRESSEDSAPLING);
		addSpecialModels(BlockRegister.BLOCK_COMPRESSEDCROPS);

		setBlockColor(BlockRegister.BLOCK_COMPRESSED, event.getMinecraftSupplier().get().getBlockColors());
		setBlockColor(BlockRegister.BLOCK_COMPRESSEDFURNACE, event.getMinecraftSupplier().get().getBlockColors());
		setBlockColor(BlockRegister.BLOCK_COMPRESSEDBREWINGSTAND, event.getMinecraftSupplier().get().getBlockColors());
		setBlockColor(BlockRegister.BLOCK_COMPRESSEDSAPLING, event.getMinecraftSupplier().get().getBlockColors());
		setBlockColor(BlockRegister.BLOCK_COMPRESSEDCROPS, event.getMinecraftSupplier().get().getBlockColors());

		RenderTypeLookup.setRenderLayer(BlockRegister.BLOCK_COMPRESSED, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockRegister.BLOCK_COMPRESSEDFURNACE, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockRegister.BLOCK_COMPRESSEDBREWINGSTAND, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockRegister.BLOCK_COMPRESSEDSAPLING, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockRegister.BLOCK_COMPRESSEDCROPS, RenderType.getCutout());

		ScreenManager.registerFactory(BlockRegister.CONTAINERTYPE_FURNACE,
				new IScreenFactory<CompressedFurnaceContainer, CompressedFurnaceScreen>() {
					@Override
					public CompressedFurnaceScreen create(CompressedFurnaceContainer p_create_1_,
							PlayerInventory p_create_2_,
							ITextComponent p_create_3_) {
						return new CompressedFurnaceScreen(p_create_1_, p_create_2_, p_create_3_);
					}
				});
		ScreenManager.registerFactory(BlockRegister.CONTAINERTYPE_BREWINGSTAND,
				new IScreenFactory<CompressedBrewingStandContainer, CompressedBrewingStandScreen>() {
					@Override
					public CompressedBrewingStandScreen create(CompressedBrewingStandContainer p_create_1_,
							PlayerInventory p_create_2_,
							ITextComponent p_create_3_) {
						return new CompressedBrewingStandScreen(p_create_1_, p_create_2_, p_create_3_);
					}
				});
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onModelBakeEvent(ModelBakeEvent event) {

		event.getModelRegistry().put(Lib.ITEM_COMPRESSED_MODEL, new ModelCompressed());
		event.getModelRegistry().put(Lib.ITEM_BLOCKCOMPRESSED_MODEL, new ModelCompressed());
		event.getModelRegistry().put(Lib.ITEM_COMPRESSEDTOOL_MODEL, new ModelCompressed());
		event.getModelRegistry().put(Lib.ITEM_COMPRESSEDSWORD_MODEL, new ModelCompressed());
		event.getModelRegistry().put(Lib.ITEM_FLUIDUNIT_MODEL, new ModelFluidUnit());

		modelRegister(BlockRegister.BLOCK_COMPRESSED, event.getModelRegistry());
		modelRegister(BlockRegister.BLOCK_COMPRESSEDFURNACE, event.getModelRegistry());
		modelRegister(BlockRegister.BLOCK_COMPRESSEDBREWINGSTAND, event.getModelRegistry());
		modelRegister(BlockRegister.BLOCK_COMPRESSEDSAPLING, event.getModelRegistry());
		modelRegister(BlockRegister.BLOCK_COMPRESSEDCROPS, event.getModelRegistry());

	}
}
