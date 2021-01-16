package otamusan.nec.common;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import otamusan.nec.common.GiveItems.Provider.Ref;
import otamusan.nec.config.ConfigCommon;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GiveItems {
	@CapabilityInject(Boolean.class)
	public static Capability<Ref<Boolean>> ISFIRST;

	/*@SubscribeEvent
	public static void giveItem(PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();
		boolean first = player.getDataManager().get(ISFIRST);
		System.out.println(first);
		player.getDataManager().register(null, null);
		if (ConfigCommon.CONFIG_COMMON.isReplaceVanillaRecipe.get() && first) {
			give(player);
		}
	}*/

	@SubscribeEvent
	public static void capRegister(AttachCapabilitiesEvent<Entity> event) {

		if (!(event.getObject() instanceof PlayerEntity))
			return;
		LazyOptional<Ref<Boolean>> optional = event.getObject().getCapability(ISFIRST);
		if (optional.isPresent())
			return;
		event.addCapability(Lib.CAPPLAYER_ISFIRST, new Provider());

	}

	public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
		public Ref<Boolean> isfirst = new Ref<Boolean>(true);

		public static class Ref<T> {
			public T t;

			public Ref(T t) {
				this.t = t;
			}
		}

		@Override
		public CompoundNBT serializeNBT() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putBoolean(Lib.MODID + "isfirst", isfirst.t);
			return nbt;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			isfirst.t = nbt.getBoolean(Lib.MODID + "isfirst");
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return getCapability(cap);
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap) {
			if (cap == ISFIRST)
				return (LazyOptional<T>) LazyOptional.of(() -> isfirst);
			return LazyOptional.empty();
		}

	}

	@SubscribeEvent
	public static void onCloned(PlayerEvent.Clone event) {
		if (!event.isWasDeath())
			return;
		LazyOptional<Ref<Boolean>> cap = event.getOriginal()
				.getCapability(ISFIRST);

		cap.ifPresent(oldvalue -> {
			event.getPlayer().getCapability(ISFIRST).ifPresent(newvalue -> {
				newvalue.t = oldvalue.t;
			});
		});
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (!ConfigCommon.visReplaceVanillaRecipe)
			return;

		event.getPlayer().getCapability(ISFIRST).ifPresent(isfirst -> {
			if (!isfirst.t)
				return;
			event.getPlayer().inventory
					.addItemStackToInventory(new ItemStack(Blocks.PISTON.asItem()));
			event.getPlayer().inventory
					.addItemStackToInventory(new ItemStack(Blocks.CRAFTING_TABLE.asItem()));
			isfirst.t = false;
		});
	}
}