package otamusan.nec.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Reloading;

public class ConfigClient {
	static final ForgeConfigSpec SPEC;
	public static final ConfigClient CONFIG_CLIENT;
	static {
		final Pair<ConfigClient, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigClient::new);
		SPEC = specPair.getRight();
		CONFIG_CLIENT = specPair.getLeft();
	}

	public ForgeConfigSpec.ConfigValue<String> compressionCatalyst;

	public ConfigClient(ForgeConfigSpec.Builder builder) {
	}

	public static void bake(){

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
		if (event.getConfig().getSpec() == ConfigCommon.SPEC) {
			bake();
		}
	}
}
