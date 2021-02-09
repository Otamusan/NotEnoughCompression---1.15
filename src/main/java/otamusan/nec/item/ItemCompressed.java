package otamusan.nec.item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.sun.java.accessibility.util.java.awt.TextComponentTranslator;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.server.command.TextComponentHelper;
import otamusan.nec.client.itemcompressed.CompressedTEISR;
import otamusan.nec.common.Lib;
import otamusan.nec.config.ConfigCommon;
import otamusan.nec.item.Using.CompressedUsing;
import otamusan.nec.register.ItemRegister;

public class ItemCompressed extends Item implements IItemCompressed {

	public ItemCompressed() {
		super(new Properties().group(ItemGroup.MISC).setISTER(new Supplier<Callable<ItemStackTileEntityRenderer>>() {
			@Override
			public Callable<ItemStackTileEntityRenderer> get() {
				return new Callable<ItemStackTileEntityRenderer>() {
					@Override
					public ItemStackTileEntityRenderer call() {
						return new CompressedTEISR();
					}
				};
			}
		}));
	}

	public static long getTotal(ItemStack compressed) {
		return getTotal(getTime(compressed));
	}

	public static long getTotal(int time) {
		return (long) Math.pow(8, time);
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		return (int) Math.pow(8, getTime(itemStack)) * getOriginal(itemStack).getBurnTime();
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return getOriginal(stack).getItem().getDurabilityForDisplay(getOriginal(stack));
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return getOriginal(stack).getItem().getRGBDurabilityForDisplay(getOriginal(stack));
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return getOriginal(stack).getItem().getItemStackLimit(stack);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return getCompressedDisplayName(stack);
	}

	public static ITextComponent getCompressedDisplayName(ItemStack stack) {
		if (isSpecialized((IItemCompressed) stack.getItem()))
			return new TranslationTextComponent("notenoughcompression.compressed", ItemCompressed.getTime(stack),
					ItemCompressed.getOriginal(stack).getDisplayName()).applyTextStyles(TextFormatting.BOLD,
					TextFormatting.AQUA);
		return new TranslationTextComponent("notenoughcompression.compressed", getTime(stack),
				getOriginal(stack).getDisplayName());
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		addCompressedInformation(stack, worldIn, tooltip, flagIn);
	}

	public static void addCompressedInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		//tooltip.add(getOriginal(stack).getDisplayName());
		//tooltip.add(new StringTextComponent(getTime(stack) + " time"));
		if (isSpecialized((IItemCompressed) stack.getItem())) {
			TextFormatting formatting = TextFormatting.GOLD;
			tooltip.add(new TranslationTextComponent("notenoughcompression.specialized").applyTextStyle(formatting));
			tooltip.add(new TranslationTextComponent("notenoughcompression.itemtypedescription." + ((IItemCompressed) stack.getItem()).getCompressedName())
					.applyTextStyle(formatting));
		}

		tooltip.add(new TranslationTextComponent("notenoughcompression.total", getCompressedAmount(ItemCompressed.getTime(stack))));
		tooltip.add(new TranslationTextComponent("notenoughcompression.compresseditem",
				getOriginal(stack).getDisplayName()));
	}

	public static boolean isSpecialized(IItemCompressed itemCompressed) {
		return itemCompressed.getCompressedName() != "base";
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		return CompressedUsing.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		return CompressedUsing.onItemUse(context);
	}

	/**If already compressed, compress further
	 *
	 * */
	public static ItemStack createCompressed(ItemStack item) {
		if (isCompressed(item))
			return createCompressed(getOriginal(item), getTime(item) + 1);
		return createCompressed(item, 1);
	}

	public static ItemStack createDecompressed(ItemStack item) {
		if (!isCompressed(item))
			return ItemStack.EMPTY;
		return createCompressed(getOriginal(item), getTime(item) - 1);
	}

	//Pass this method to give the variety of Item class
	public static IItemCompressed getCompressedItem(ItemStack original) {
		return (IItemCompressed) ItemRegister.ITEM_COMPRESSED.getItem(original.getItem());
	}

	public static boolean isCompressable(ItemStack stack) {
		if (stack.isEmpty())
			return false;
		if (!ConfigCommon.isCompressable(stack.getItem()))
			return false;
		return true;
	}

	@Override
	public boolean isAvailable(Item item) {
		return true;
	}

	private ArrayList<IItemCompressed> list = new ArrayList<IItemCompressed>();
	private IItemCompressed parent;

	@Override
	public ArrayList<IItemCompressed> getChildren() {
		return list;
	}

	@Override
	public String getCompressedName() {
		return "base";
	}

	@Override
	public IItemCompressed getParent() {
		return parent;
	}

	@Override
	public void setParent(IItemCompressed iItemCompressed) {
		this.parent = iItemCompressed;
	}

	public static boolean isCompressed(ItemStack stack) {
		return stack.getItem() instanceof IItemCompressed;
	}

	public static int getTime(ItemStack stack) {
		if (!isCompressed(stack))
			return 0;
		if (!stack.getOrCreateTag().contains(Lib.ITEM_NBT_TIME))
			return 0;
		return stack.getTag().getInt(Lib.ITEM_NBT_TIME);
	}

	public static ItemStack getOriginal(ItemStack stack) {
		if (!isCompressed(stack))
			return ItemStack.EMPTY;
		if (!stack.getOrCreateTag().contains(Lib.ITEM_NBT_ORIGINAL))
			return ItemStack.EMPTY;
		return ItemStack.read(stack.getTag().getCompound(Lib.ITEM_NBT_ORIGINAL));
	}

	public static void setTime(ItemStack stack, int n) {
		stack.getOrCreateTag().putInt(Lib.ITEM_NBT_TIME, n);
	}

	public static void setOriginal(ItemStack stack, ItemStack original) {
		if (ItemCompressed.isCompressed(original))
			throw new Error("original item has alredy compressed");
		ItemStack aoriginal = original.copy();
		aoriginal.setCount(1);
		stack.getOrCreateTag().put(Lib.ITEM_NBT_ORIGINAL, aoriginal.write(new CompoundNBT()));
	}

	//If n is zero, it returns the original as is
	public static ItemStack createCompressed(ItemStack original, int n) {
		if (n <= 0)
			return original.copy();
		if (original.isEmpty())
			return ItemStack.EMPTY;

		ItemStack compressed = new ItemStack((Item) getCompressedItem(original));
		setTime(compressed, n);
		setOriginal(compressed, original);
		compressed = getCompressedItem(original).onCompress(original.copy(), compressed.copy());
		return compressed;
	}

	public static String getCompressedAmount(int time8) {
		double log10_8 = Math.log(8) / Math.log(10);
		double time10 = time8 * log10_8;
		int time10_int = (int) time10;

		int power_a;
		if (time10_int >= 6)
			power_a = time10_int % 3;
		else
			power_a = time10_int;

		double time10_fract = time10 - time10_int;
		double num_value = Math.pow(10, time10_fract);

		double pow_a = Math.pow(10, power_a);

		String str_value;
		if (time10_int >= 6)
			str_value = String.format("%.3f", num_value * pow_a);
		else
			str_value = String.format("%,.0f", num_value * pow_a);

		int pow_1000 = (time10_int - power_a) / 3;
		String str_1000;
		String key = Lib.MODID + ".unit." + pow_1000;
		if (time10_int >= 6 && !Screen.hasShiftDown())
			//if (time10_int >= 6 && (!(I18n.hasKey(key)) || GuiScreen.isShiftKeyDown()))

		str_1000 = new TranslationTextComponent(Lib.MODID + ".unit.huge", pow_1000).getString();
		else
			str_1000 = new TranslationTextComponent(key, pow_1000).getString();

		String str_num = I18n.format(Lib.MODID + ".unit", str_value, str_1000);

		return str_num;
	}

}
