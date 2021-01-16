package otamusan.nec.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
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

	public static final ForgeConfigSpec SPEC;
	public static final ConfigCommon CONFIG_COMMON;
	static {
		final Pair<ConfigCommon, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigCommon::new);
		SPEC = specPair.getRight();
		CONFIG_COMMON = specPair.getLeft();
	}

	private ForgeConfigSpec.ConfigValue<String> compressionCatalyst;
	private ForgeConfigSpec.ConfigValue<String> decompressionCatalyst;
	private ConfigValue<List<? extends String>> incompressibleList;
	private ForgeConfigSpec.BooleanValue isReplaceVanillaRecipe;

	private ForgeConfigSpec.BooleanValue isReplaceChunk;
	private DoubleValue replaceRate;
	private IntValue maxSize;
	private IntValue replacedMaxTime;
	private IntValue deviationofTime;
	private ConfigValue<List<? extends String>> placeExclusion;

	private BooleanValue specializeCompressedBrewingStand;
	private BooleanValue specializeCompressedCrops;
	private BooleanValue slowDownCropsGrowthByTick;
	private BooleanValue slowDownCropsGrowthByBoneMeal;
	private BooleanValue specializeCompressedFurnace;
	private BooleanValue specializeCompressedSapling;
	private BooleanValue slowDownTreeGrowthByTick;
	private BooleanValue slowDownTreeGrowthByBoneMeal;
	private BooleanValue specializeCompressedSword;
	private BooleanValue specializeCompressedTool;
	private DoubleValue modifierofAttackDamage;
	private DoubleValue modifierofMiningSpeed;
	private DoubleValue modifierofMaxDurability;


	public static String vcompressionCatalyst;
	public static String vdecompressionCatalyst;
	public static List<? extends String> vincompressibleList;
	public static Boolean visReplaceVanillaRecipe;

	public static Boolean visReplaceChunk;
	public static Double vreplaceRate;
	public static Integer vmaxSize;
	public static Integer vreplacedMaxTime;
	public static Integer vdeviationofTime;
	public static List<? extends String> vplaceExclusion;

	public static Boolean vspecializeCompressedBrewingStand;
	public static Boolean vspecializeCompressedCrops;
	public static Boolean vslowDownCropsGrowthByTick;
	public static Boolean vslowDownCropsGrowthByBoneMeal;
	public static Boolean vspecializeCompressedFurnace;
	public static Boolean vspecializeCompressedSapling;
	public static Boolean vslowDownTreeGrowthByTick;
	public static Boolean vslowDownTreeGrowthByBoneMeal;
	public static Boolean vspecializeCompressedSword;
	public static Boolean vspecializeCompressedTool;
	public static Double vmodifierofAttackDamage;
	public static Double vmodifierofMiningSpeed;
	public static Double vmodifierofMaxDurability;
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
		replaceRate = builder.comment("Chunk replacement rate").defineInRange("replaceRate", 0.5d, 0, 1);
		maxSize = builder.comment("Max radius of Chunk").defineInRange("maxSize", 6, 0, 16);
		replacedMaxTime = builder.comment("Maximum compressed time of blocks in chunks replaced")
				.defineInRange("replacedMaxTime", 5, 1, Integer.MAX_VALUE);
		deviationofTime = builder.comment("Deviation of the replaced compressed time")
				.defineInRange("deviationofTime", 6, 1, Integer.MAX_VALUE);
		placeExclusion = builder.comment("Blocks written here can not be placed even if it is compressed").defineList(
				"placeExclusion", new ArrayList<String>(), block -> true);
		builder.pop();

		builder.push("Specialize");
		specializeCompressedBrewingStand = builder.comment("Whether to specialize compressed brewing stand")
				.define("specializeCompressedBrewingStand", true);
		specializeCompressedCrops = builder.comment("Whether to specialize compressed crops")
				.define("specializeCompressedCrops", true);
		slowDownCropsGrowthByTick = builder.comment("Whether to slow down compressed crops by randamtick")
				.define("slowDownCropsGrowthByTick", false);
		slowDownCropsGrowthByBoneMeal = builder.comment("Whether to slow down compressed crops by boneMeal")
				.define("slowDownCropsGrowthByBoneMeal", true);
		specializeCompressedFurnace = builder.comment("Whether to specialize compressed furnace")
				.define("specializeCompressedFurnace", true);
		specializeCompressedSapling = builder.comment("Whether to specialize compressed sapling")
				.define("specializeCompressedSapling", true);
		slowDownTreeGrowthByTick = builder.comment("Whether to slow down compressed tree by randamtick")
				.define("slowDownTreeGrowthByTick", false);
		slowDownTreeGrowthByBoneMeal = builder.comment("Whether to slow down compressed tree by boneMeal")
				.define("slowDownTreeGrowthByBoneMeal", true);
		specializeCompressedSword = builder.comment("Whether to specialize compressed sword")
				.define("specializeCompressedSword", true);
		specializeCompressedTool = builder.comment("Whether to specialize compressed tool")
				.define("specializeCompressedTool", true);
		modifierofAttackDamage = builder.comment("Modifier per compression of attack damage")
				.defineInRange("modifierofAttackDamage", 1.5, 1, Double.MAX_VALUE);
		modifierofMiningSpeed = builder.comment("Modifier per compression of mining speed")
				.defineInRange("modifierofMiningSpeed", 1.5, 1, Double.MAX_VALUE);
		modifierofMaxDurability = builder.comment("Modifier per compression of max durability")
				.defineInRange("modifierofMaxDurability", 1.5, 1, Double.MAX_VALUE);
		builder.pop();
	}

	public static void bake(){
		vcompressionCatalyst=CONFIG_COMMON.compressionCatalyst.get();
		vdecompressionCatalyst= CONFIG_COMMON.decompressionCatalyst.get();
		vincompressibleList= CONFIG_COMMON.incompressibleList.get();
		visReplaceVanillaRecipe=CONFIG_COMMON.isReplaceVanillaRecipe.get();

		visReplaceChunk=CONFIG_COMMON.isReplaceChunk.get();
		vreplaceRate=CONFIG_COMMON.replaceRate.get();
		vmaxSize=CONFIG_COMMON.maxSize.get();
		vreplacedMaxTime=CONFIG_COMMON.replacedMaxTime.get();
		vdeviationofTime=CONFIG_COMMON.deviationofTime.get();
		vplaceExclusion=CONFIG_COMMON.placeExclusion.get();

		vspecializeCompressedBrewingStand=CONFIG_COMMON.specializeCompressedBrewingStand.get();
		vspecializeCompressedCrops=CONFIG_COMMON.specializeCompressedCrops.get();
		vslowDownCropsGrowthByTick=CONFIG_COMMON.slowDownCropsGrowthByTick.get();
		vslowDownCropsGrowthByBoneMeal=CONFIG_COMMON.slowDownCropsGrowthByBoneMeal.get();
		vspecializeCompressedFurnace=CONFIG_COMMON.specializeCompressedFurnace.get();
		vspecializeCompressedSapling=CONFIG_COMMON.specializeCompressedSapling.get();
		vslowDownTreeGrowthByTick=CONFIG_COMMON.slowDownTreeGrowthByTick.get();
		vslowDownTreeGrowthByBoneMeal=CONFIG_COMMON.slowDownTreeGrowthByBoneMeal.get();
		vspecializeCompressedSword=CONFIG_COMMON.specializeCompressedSword.get();
		vspecializeCompressedTool=CONFIG_COMMON.specializeCompressedTool.get();
		vmodifierofAttackDamage=CONFIG_COMMON.modifierofAttackDamage.get();
		vmodifierofMiningSpeed=CONFIG_COMMON.modifierofMiningSpeed.get();
		vmodifierofMaxDurability=CONFIG_COMMON.modifierofMaxDurability.get();
	}

	public static Item getCompressionCatalyst() {
		return Registry.ITEM.getOrDefault(new ResourceLocation(vcompressionCatalyst));
	}

	public static boolean isCompressionCatalyst(Item item) {
		return getCompressionCatalyst() == item;
	}

	public static Item getDecompressionCatalyst() {
		return Registry.ITEM.getOrDefault(new ResourceLocation(vdecompressionCatalyst));
	}

	public static boolean isDecompressionCatalyst(Item item) {
		return getDecompressionCatalyst() == item;
	}

	public static boolean isCompressable(Item item) {
		for (String name : vincompressibleList) {
			if (Registry.ITEM.getOrDefault(new ResourceLocation(name)) == item)
				return false;
		}
		return true;
	}

	public static boolean canPlace(Block block) {
		for (String name : vplaceExclusion) {
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
	public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
		if (event.getConfig().getSpec() == ConfigCommon.SPEC) {
			bake();
		}
	}

	@SubscribeEvent
	public static void onFileChange(final Reloading configEvent) {

	}

}
