package otamusan.nec.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import otamusan.nec.block.BlockCompressed;

public class MessageUpdateLight {
	public BlockPos pos;

	public MessageUpdateLight(BlockPos pos) {
		this.pos = pos;
	}

	public static void encoder(MessageUpdateLight msg, PacketBuffer buffer) {
		buffer.writeBlockPos(msg.pos);
	}

	public static MessageUpdateLight decoder(PacketBuffer buffer) {
		return new MessageUpdateLight(buffer.readBlockPos());
	}

	public static class Handler {
		public static void handle(MessageUpdateLight msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity sender = ctx.get().getSender();
				System.out.println(sender.world);
				System.out.println(msg.pos);

				BlockCompressed.lightCheck(sender.world, msg.pos);
			});
			ctx.get().setPacketHandled(true);
		}
	}
}
