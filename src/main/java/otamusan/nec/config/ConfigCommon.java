package otamusan.nec.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Reloading;
import otamusan.nec.common.Lib;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigCommon {

	static final ForgeConfigSpec SPEC;
	public static final ConfigCommon CONFIG_COMMON;
	static {
		final Pair<ConfigCommon, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigCommon::new);
		SPEC = specPair.getRight();
		CONFIG_COMMON = specPair.getLeft();
	}

	public ForgeConfigSpec.ConfigValue<String> compressionCatalyst;
	public ForgeConfigSpec.ConfigValue<String> decompressionCatalyst;
	public ConfigValue<List<? extends String>> incompressibleList;
	public ForgeConfigSpec.BooleanValue isReplaceVanillaRecipe;

	public ForgeConfigSpec.BooleanValue isReplaceChunk;
	public DoubleValue replaceRate;
	public IntValue replacedMaxTime;
	public IntValue deviationofTime;
	public ConfigValue<List<? extends String>> placeExclusion;

	public ConfigCommon(ForgeConfigSpec.Builder builder) {
		builder.push("Compression");
		compressionCatalyst = builder.comment("Catalyst item of Compression").define("compressionCatalyst",
				"minecraft:piston");
		decompressionCatalyst = builder.comment("Catalyst item of Decompression").define("decompressionCatalyst",
				"minecraft:sticky_piston");
		incompressibleList = builder.comment("List of incompressible items").defineList("incompressibleList",
				new ArrayList<String>() {
					{
						add("minecraft:bedrock");
						add("minecraft:air");
					}
				}, item -> true);
		isReplaceVanillaRecipe = builder
				.comment("If true, all crafts can only be done with compressed items",
						"In other words, the amount of material per craft is increased eight times")
				.define("isReplaceVanillaRecipe", false);
		builder.pop();

		builder.push("World");
		isReplaceChunk = builder.comment("Whether to replace chunk with compressed one").define("isReplaceChunk", true);
		replaceRate = builder.comment("Chunk replacement rate").defineInRange("replaceRate", 0.005d, 0, 1);
		replacedMaxTime = builder.comment("Maximum compressed time of blocks in chunks replaced")
				.defineInRange("replacedMaxTime", 5, 1, Integer.MAX_VALUE);
		deviationofTime = builder.comment("Deviation of the replaced compressed time")
				.defineInRange("deviationofTime", 6, 1, Integer.MAX_VALUE);
		placeExclusion = builder.comment("Blocks written here can not be placed even if it is compressed").defineList(
				"placeExclusion", new ArrayList<String>(), block -> true);
		builder.pop();
	}

	public Item getCompressionCatalyst() {
		return Registry.ITEM.getOrDefault(new ResourceLocation(compressionCatalyst.get()));
	}

	public boolean isCompressionCatalyst(Item item) {
		return getCompressionCatalyst() == item;
	}

	public Item getDecompressionCatalyst() {
		return Registry.ITEM.getOrDefault(new ResourceLocation(decompressionCatalyst.get()));
	}

	public boolean isDecompressionCatalyst(Item item) {
		return getDecompressionCatalyst() == item;
	}

	public boolean isCompressable(Item item) {
		for (String name : incompressibleList.get()) {
			if (Registry.ITEM.getOrDefault(new ResourceLocation(name)) == item)
				return false;
		}
		return true;
	}

	public boolean canPlace(Block block) {
		for (String name : placeExclusion.get()) {
			if (Registry.BLOCK.getOrDefault(new ResourceLocation(name)) == block)
				return false;
		}
		return true;
	}

	public ForgeConfigSpec getSpec() {
		return SPEC;
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
	}

	@SubscribeEvent
	public static void onFileChange(final Reloading configEvent) {
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {

	}
}
