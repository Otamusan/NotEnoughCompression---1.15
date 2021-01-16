package otamusan.nec.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Reloading;
import otamusan.nec.common.Lib;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigServer {

	static final ForgeConfigSpec SPEC;
	public static final ConfigServer CONFIG_SERVER;
	static {
		final Pair<ConfigServer, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigServer::new);
		SPEC = specPair.getRight();
		CONFIG_SERVER = specPair.getLeft();
	}

	public ForgeConfigSpec.ConfigValue<String> compressionCatalyst;

	public ConfigServer(ForgeConfigSpec.Builder builder) {
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
