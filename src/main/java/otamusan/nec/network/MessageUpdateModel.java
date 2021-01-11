package otamusan.nec.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdateModel {
	public BlockPos pos;

	public MessageUpdateModel(BlockPos pos) {
		this.pos = pos;
	}

	public static void encoder(MessageUpdateModel msg, PacketBuffer buffer) {
		buffer.writeBlockPos(msg.pos);
	}

	public static MessageUpdateModel decoder(PacketBuffer buffer) {
		return new MessageUpdateModel(buffer.readBlockPos());
	}

	public static class Handler {
		public static void handle(MessageUpdateModel msg, Supplier<NetworkEvent.Context> ctx) {

			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity sender = ctx.get().getSender();
				//World world = sender.world;
				try {
					Minecraft.getInstance().worldRenderer.markBlockRangeForRenderUpdate(msg.pos.getX() - 1,
							msg.pos.getY() - 1,
							msg.pos.getZ() - 1,
							msg.pos.getX() + 1, msg.pos.getY() + 1, msg.pos.getZ() + 1);
				} catch (Exception e) {
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}
