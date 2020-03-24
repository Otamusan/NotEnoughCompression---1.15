package otamusan.nec.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.lib.InventoryUtil;
import otamusan.nec.recipe.CompressionRecipe;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AutoCompression {

	private static int time = 0;

	@SubscribeEvent
	public static void onUpdate(WorldTickEvent event) {
		time++;
		if (time <= 20)
			return;
		time = 0;
		Map<BlockPos, Item> candidates = Maps.newHashMap();
		if (event.world.isRemote)
			return;

		ServerWorld world = (ServerWorld) event.world;

		for (Entity entity : world.getEntities(EntityType.ITEM_FRAME, bool -> true)) {

			ItemFrameEntity frame = (ItemFrameEntity) entity;
			Item displayedItem = frame.getDisplayedItem().getItem();
			BlockPos pos = entity.getPosition().offset(frame.getAdjustedHorizontalFacing(), -1);
			candidates.put(pos, displayedItem);
		}

		for (Entry<BlockPos, Item> entry : candidates.entrySet()) {
			BlockPos pos = entry.getKey();
			Item type = entry.getValue();

			TileEntity tile = event.world.getTileEntity(pos);

			if (tile == null)
				continue;

			if (!tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent())
				continue;

			IItemHandler iItemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
					.orElse(null);

			if (CompressionRecipe.getCompresser().getItem() != type)
				continue;

			List<ItemStack> remains = autocompression(iItemHandler);

			for (ItemStack itemStack : remains) {
				Block.spawnAsEntity(event.world, pos.add(0.5, 0.5, 0.5), itemStack);
			}

		}
	}

	public static List<ItemStack> autocompression(IItemHandler inv) {
		List<ItemStack> remains = new ArrayList<>();
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack source;

			if (ItemCompressed.isCompressed(inv.getStackInSlot(i))) {
				source = ItemCompressed.getOriginal(inv.getStackInSlot(i));
			} else {
				source = inv.getStackInSlot(i).copy();
			}

			CompressedItems manager = new CompressedItems(source);
			if (source.isEmpty())
				continue;
			for (int j = i; j < inv.getSlots(); j++) {
				boolean containable = manager.addCompressed(inv.getStackInSlot(j));
				if (containable) {
					inv.extractItem(j, inv.getStackInSlot(j).getCount(), false);
				}
			}
			List<ItemStack> items = manager.getCompressed();
			remains.addAll(InventoryUtil.putStacksInInv(inv, items));
		}
		return remains;
	}

}
