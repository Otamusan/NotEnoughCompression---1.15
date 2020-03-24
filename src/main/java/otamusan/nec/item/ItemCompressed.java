package otamusan.nec.item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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
		return new TranslationTextComponent("notenoughcompression.compressed", getTime(stack),
				getOriginal(stack).getDisplayName());
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		//tooltip.add(getOriginal(stack).getDisplayName());
		//tooltip.add(new StringTextComponent(getTime(stack) + " time"));

		tooltip.add(new TranslationTextComponent("notenoughcompression.total", (long) Math.pow(8, getTime(stack))));
		tooltip.add(new TranslationTextComponent("notenoughcompression.compresseditem",
				getOriginal(stack).getDisplayName()));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (playerIn.isCreative() && handIn == Hand.OFF_HAND) {
			ItemStack stack = playerIn.getHeldItem(handIn).copy();
			worldIn.addEntity(new ItemEntity(worldIn, playerIn.getPositionVec().x, playerIn.getPositionVec().y,
					playerIn.getPositionVec().z, stack));
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
		}
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
		if (!ConfigCommon.CONFIG_COMMON.isCompressable(stack.getItem()))
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
		return compressed;
	}

}
