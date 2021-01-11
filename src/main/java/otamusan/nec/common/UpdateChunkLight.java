package otamusan.nec.common;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class UpdateChunkLight {
	@SubscribeEvent
	public static void update(ChunkEvent.Load e) {
		/*ChunkPos pos = e.getChunk().getPos();
		for (int z = pos.getZStart(); z < pos.getZEnd(); z++) {
			for (int x = pos.getXStart(); x < pos.getXEnd(); x++) {
				for (int y = 0; y < 255; y++) {
					if (!BlockCompressed.isCompressedBlock(e.getChunk().getBlockState(new BlockPos(x, y, z)).getBlock())
							|| e.getChunk().getBlockState(new BlockPos(x, y, z)).isSolid())
						continue;
		
					BlockCompressed.lightCheck(e.getWorld(), new BlockPos(x, y, z));
					NECPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
							new MessageUpdateLight(new BlockPos(x, y, z)));
				}
			}
		}*/
		//BlockCompressed.lightCheck(null, null);
	}
}
