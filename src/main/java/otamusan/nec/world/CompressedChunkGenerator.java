package otamusan.nec.world;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.block.IBlockCompressed;
import otamusan.nec.client.blockcompressed.CompressedData;
import otamusan.nec.common.Lib;
import otamusan.nec.config.ConfigCommon;
import otamusan.nec.item.ItemCompressed;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompressedChunkGenerator extends Feature<NoFeatureConfig> {

	public CompressedChunkGenerator(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
		super(configFactoryIn);
	}

	public static Feature<NoFeatureConfig> compressedChunk;

	@SubscribeEvent
	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		IForgeRegistry<Feature<?>> registry = event.getRegistry();

		compressedChunk = new CompressedChunkGenerator(NoFeatureConfig::deserialize);
		compressedChunk.setRegistryName(Lib.FEATURE_COMPRESSEDCHUNK);

		registry.register(compressedChunk);
		for (Biome biome : Registry.BIOME) {
			biome.addFeature(Decoration.TOP_LAYER_MODIFICATION,
					new ConfiguredFeature<NoFeatureConfig, CompressedChunkGenerator>(
							(CompressedChunkGenerator) compressedChunk, IFeatureConfig.NO_FEATURE_CONFIG));
		}

	}

	@Override
	public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand,
			BlockPos pos, NoFeatureConfig config) {

		int x = pos.getX();
		int z = pos.getZ();

		if (!ConfigCommon.CONFIG_COMMON.isReplaceChunk.get())
			return false;

		if (rand.nextDouble() > ConfigCommon.CONFIG_COMMON.replaceRate.get())

			return false;

		int time = ConfigCommon.CONFIG_COMMON.replacedMaxTime.get() - (int) Math.floor(Math.log(rand.nextInt(
				(int) Math.pow(ConfigCommon.CONFIG_COMMON.deviationofTime.get(),
						ConfigCommon.CONFIG_COMMON.replacedMaxTime.get()))
				+ 1)
				/ Math.log(ConfigCommon.CONFIG_COMMON.deviationofTime.get()));
		for (int ix = x; ix < x + 16; ix++) {
			for (int iz = z; iz < z + 16; iz++) {
				for (int iy = 0; iy <= 255; iy++) {
					BlockPos pos2 = new BlockPos(ix, iy, iz);

					if (isReplaceable(pos2, worldIn)) {

						replace(pos2, worldIn, time);
						//worldIn.setBlockState(pos2, Blocks.STONE.getDefaultState(), 2);
					}

				}
			}
		}
		return true;
	}

	public boolean replace(BlockPos pos, IWorld worldIn, int time) {

		BlockState state = worldIn.getBlockState(pos);

		ItemStack item = state.getBlock().getItem(worldIn, pos, state);
		ItemStack compressed = ItemCompressed.createCompressed(item, time);

		CompressedData data = new CompressedData(state, compressed);

		boolean isSuccess = IBlockCompressed.setCompressedBlock(pos, worldIn, data, true);
		IBlockCompressed.lightCheck(worldIn, pos);

		if (!isSuccess) {
			worldIn.setBlockState(pos, state, 11);
			return false;
		}
		return true;
	}

	public boolean isReplaceable(BlockPos pos, IWorld worldIn) {

		BlockState state = worldIn.getBlockState(pos);

		ItemStack item = state.getBlock().getItem(worldIn, pos, state);

		return (!item.isEmpty()

				&& !BlockCompressed.isCompressedBlock(state.getBlock())
				&& state.getBlock().getBlockHardness(state, worldIn, pos) != -1
				&& ItemCompressed.isCompressable(item)
				&& ConfigCommon.CONFIG_COMMON.canPlace(state.getBlock()));
	}
}
