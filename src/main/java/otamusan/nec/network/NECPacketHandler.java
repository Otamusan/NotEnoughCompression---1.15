package otamusan.nec.network;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import otamusan.nec.common.Lib;

public class NECPacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	/*public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			Lib.PACKET_NEC,
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);*/

	public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(Lib.PACKET_NEC)
			.clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

	private static int id = 1;

	public static void registerMessage() {
		INSTANCE.registerMessage(id++, MessageUpdateModel.class, MessageUpdateModel::encoder,
				MessageUpdateModel::decoder, MessageUpdateModel.Handler::handle);
		INSTANCE.registerMessage(id++, MessageUpdateLight.class, MessageUpdateLight::encoder,
				MessageUpdateLight::decoder, MessageUpdateLight.Handler::handle);

	}

}
