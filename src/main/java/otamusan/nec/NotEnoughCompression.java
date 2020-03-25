package otamusan.nec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import otamusan.nec.common.AutoCompression;
import otamusan.nec.common.GiveItems;
import otamusan.nec.common.Lib;
import otamusan.nec.config.ConfigClient;
import otamusan.nec.config.ConfigCommon;
import otamusan.nec.config.ConfigServer;

@Mod(Lib.MODID)

public class NotEnoughCompression {
	public static final Logger LOGGER = LogManager.getLogger();

	public NotEnoughCompression() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigServer.CONFIG_SERVER.getSpec());
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigClient.CONFIG_CLIENT.getSpec());
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigCommon.CONFIG_COMMON.getSpec());

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		MinecraftForge.EVENT_BUS.register(AutoCompression.class);
		MinecraftForge.EVENT_BUS.register(GiveItems.class);
	}

	private void processIMC(final InterModProcessEvent event) {
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
	}
}
