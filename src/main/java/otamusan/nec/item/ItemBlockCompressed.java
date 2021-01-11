package otamusan.nec.item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import otamusan.nec.block.BlockCompressed;
import otamusan.nec.block.IBlockCompressed;
import otamusan.nec.block.tileentity.ITileCompressed;
import otamusan.nec.client.blockcompressed.CompressedData;
import otamusan.nec.client.itemcompressed.CompressedTEISR;
import otamusan.nec.register.BlockRegister;

public class ItemBlockCompressed extends BlockItem implements IItemCompressed {
	public ItemBlockCompressed() {
		super(BlockRegister.BLOCK_COMPRESSED,
				new Properties().group(ItemGroup.MISC).setISTER(new Supplier<Callable<ItemStackTileEntityRenderer>>() {
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
	public int getItemStackLimit(ItemStack stack) {
		return ItemCompressed.getOriginal(stack).getItem().getItemStackLimit(stack);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		if (isSpecialized(stack))
			return new TranslationTextComponent("notenoughcompression.compressed", ItemCompressed.getTime(stack),
					ItemCompressed.getOriginal(stack).getDisplayName()).applyTextStyles(TextFormatting.BOLD,
							TextFormatting.AQUA);
		return new TranslationTextComponent("notenoughcompression.compressed", ItemCompressed.getTime(stack),
				ItemCompressed.getOriginal(stack).getDisplayName());
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		//tooltip.add(getOriginal(stack).getDisplayName());
		//tooltip.add(new StringTextComponent(getTime(stack) + " time"));

		if (isSpecialized(stack)) {
			TextFormatting formatting = TextFormatting.GOLD;
			tooltip.add(
					new TranslationTextComponent("notenoughcompression.specialized").applyTextStyle(formatting));
			Block block = ((BlockItem) ItemCompressed.getOriginal(stack).getItem()).getBlock();
			IBlockCompressed blockCompressed = (IBlockCompressed) BlockCompressed
					.getCompressedBlockS(block);
			tooltip.add(
					new TranslationTextComponent(
							"notenoughcompression.blocktypedescription." + blockCompressed.getCompressedName())
									.applyTextStyle(formatting));
		}

		tooltip.add(new TranslationTextComponent(ItemCompressed.getCompressedAmount(ItemCompressed.getTime(stack))));

		tooltip.add(new TranslationTextComponent("notenoughcompression.compresseditem",
				ItemCompressed.getOriginal(stack).getDisplayName()));
	}

	public static boolean isSpecialized(ItemStack stack) {
		if (ItemCompressed.getOriginal(stack).isEmpty())
			return false;
		Block block = ((BlockItem) ItemCompressed.getOriginal(stack).getItem()).getBlock();
		IBlockCompressed blockCompressed = (IBlockCompressed) BlockCompressed
				.getCompressedBlockS(block);

		return blockCompressed.getCompressedName() != "base";
	}

	@Override
	public int getBurnTime(ItemStack itemStack) {
		return (int) Math.pow(8, ItemCompressed.getTime(itemStack))
				* ItemCompressed.getOriginal(itemStack).getBurnTime();
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		ActionResultType actionresulttype = this.tryPlace(new BlockItemUseContext(context));
		BlockCompressed.lightCheck(context.getWorld(), context.getPos());

		return actionresulttype != ActionResultType.SUCCESS && this.isFood()
				? this.onItemRightClick(context.getWorld(), context.getPlayer(), context.getHand()).getType()
				: actionresulttype;
	}

	public ActionResultType tryPlace(BlockItemUseContext context) {
		if (!context.canPlace()) {
			return ActionResultType.FAIL;
		} else {
			BlockItemUseContext blockitemusecontext = this.getBlockItemUseContext(context);
			if (blockitemusecontext == null) {
				return ActionResultType.FAIL;
			} else {
				BlockState blockstate = this.getStateForPlacement(blockitemusecontext);
				if (blockstate == null) {
					return ActionResultType.FAIL;
				} else if (!this.placeBlock(blockitemusecontext, blockstate)) {
					return ActionResultType.FAIL;
				} else {
					BlockPos blockpos = blockitemusecontext.getPos();
					World world = blockitemusecontext.getWorld();
					PlayerEntity playerentity = blockitemusecontext.getPlayer();
					ItemStack itemstack = blockitemusecontext.getItem();
					BlockState blockstate1 = world.getBlockState(blockpos);
					Block block = blockstate1.getBlock();
					if (block == blockstate.getBlock()) {

						BlockState state = blockstate1;
						blockstate1 = this.func_219985_a(blockpos, world, itemstack, blockstate1);

						if (state != blockstate1) {
							world.setBlockState(blockpos, blockstate1, 2);
						}

						this.onBlockPlaced(blockpos, world, playerentity, itemstack, blockstate1, blockitemusecontext);
						block.onBlockPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
						if (playerentity instanceof ServerPlayerEntity) {
							CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerentity, blockpos,
									itemstack);
						}
					}

					SoundType soundtype = blockstate1.getSoundType(world, blockpos, context.getPlayer());
					world.playSound(playerentity, blockpos,
							this.getPlaceSound(blockstate1, world, blockpos, context.getPlayer()), SoundCategory.BLOCKS,
							(soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					itemstack.shrink(1);
					return ActionResultType.SUCCESS;
				}

			}
		}
	}

	protected SoundEvent getPlaceSound(BlockState state) {
		return state.getSoundType().getPlaceSound();
	}

	protected SoundEvent getPlaceSound(BlockState state, World world, BlockPos pos, PlayerEntity entity) {
		return state.getSoundType(world, pos, entity).getPlaceSound();
	}

	@Nullable
	public BlockItemUseContext getBlockItemUseContext(BlockItemUseContext context) {
		return context;
	}

	public BlockItemUseContext getnew(BlockItemUseContext context, ItemStack stack) {
		BlockItemUseContext newcontext = new ItemCompressedBlockUseContext(context, stack);
		return newcontext;
	}

	protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack,
			BlockState state, BlockItemUseContext useContext) {

		ITileCompressed tile = (ITileCompressed) worldIn.getTileEntity(pos);
		Block block = ((BlockItem) ItemCompressed.getOriginal(stack).getItem()).getBlock();

		//BlockState state2 = func_219985_a(pos, worldIn, ItemCompressed.getOriginal(stack),
		//block.getStateForPlacement(getnew(useContext, ItemCompressed.getOriginal(stack))));
		BlockState state2 = getStateFromItem(stack);
		/*System.out.println(state2);
		System.out.println(state2);
		System.out.println(state2);
		System.out.println(state2);*/

		tile.setCompresseData(new CompressedData(state2, stack));

		return setTileEntityNBT(worldIn, player, pos, stack);
	}

	@Nullable
	protected BlockState getStateForPlacement(BlockItemUseContext context) {
		Block block = ((BlockItem) ItemCompressed.getOriginal(context.getItem()).getItem()).getBlock();
		BlockState blockstate = BlockCompressed
				.getCompressedBlockS(block)
				.getStateForPlacement(context);
		return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
	}

	private BlockState func_219985_a(BlockPos p_219985_1_, World p_219985_2_, ItemStack stack,
			BlockState p_219985_4_) {
		BlockState blockstate = p_219985_4_;
		CompoundNBT compoundnbt = ItemCompressed.getOriginal(stack).getTag();
		if (compoundnbt != null) {
			CompoundNBT compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
			StateContainer<Block, BlockState> statecontainer = p_219985_4_.getBlock().getStateContainer();

			for (String s : compoundnbt1.keySet()) {
				IProperty<?> iproperty = statecontainer.getProperty(s);
				if (iproperty != null) {
					String s1 = compoundnbt1.get(s).getString();
					blockstate = func_219988_a(blockstate, iproperty, s1);
				}
			}
		}

		return blockstate;
	}

	public static BlockState getStateFromItem(ItemStack stack) {
		BlockState blockstate = ((BlockItem) ItemCompressed.getOriginal(stack).getItem()).getBlock().getDefaultState();
		CompoundNBT compoundnbt = ItemCompressed.getOriginal(stack).getTag();
		if (compoundnbt != null) {
			CompoundNBT compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
			StateContainer<Block, BlockState> statecontainer = blockstate.getBlock().getStateContainer();

			for (String s : compoundnbt1.keySet()) {
				IProperty<?> iproperty = statecontainer.getProperty(s);
				if (iproperty != null) {
					String s1 = compoundnbt1.get(s).getString();
					blockstate = func_219988_a(blockstate, iproperty, s1);
				}
			}
		}
		return blockstate;
	}

	private static <T extends Comparable<T>> BlockState func_219988_a(BlockState p_219988_0_, IProperty<T> p_219988_1_,
			String p_219988_2_) {
		return p_219988_1_.parseValue(p_219988_2_).map((p_219986_2_) -> {
			return p_219988_0_.with(p_219988_1_, p_219986_2_);
		}).orElse(p_219988_0_);
	}

	protected boolean canPlace(BlockItemUseContext p_195944_1_, BlockState p_195944_2_) {
		PlayerEntity playerentity = p_195944_1_.getPlayer();
		ISelectionContext iselectioncontext = playerentity == null ? ISelectionContext.dummy()
				: ISelectionContext.forEntity(playerentity);

		BlockState state = getStateFromItem(p_195944_1_.getItem());
		return (!this.func_219987_d() || state.isValidPosition(p_195944_1_.getWorld(), p_195944_1_.getPos()))
				&& p_195944_1_.getWorld().func_226663_a_(p_195944_2_, p_195944_1_.getPos(), iselectioncontext);
	}

	protected boolean func_219987_d() {
		return true;
	}

	protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
		return context.getWorld().setBlockState(context.getPos(), state, 11);
	}

	public static boolean setTileEntityNBT(World worldIn, @Nullable PlayerEntity player, BlockPos pos,
			ItemStack stackIn) {
		MinecraftServer minecraftserver = worldIn.getServer();
		if (minecraftserver == null) {
			return false;
		} else {
			CompoundNBT compoundnbt = stackIn.getChildTag("BlockEntityTag");
			if (compoundnbt != null) {
				TileEntity tileentity = worldIn.getTileEntity(pos);
				if (tileentity != null) {
					if (!worldIn.isRemote && tileentity.onlyOpsCanSetNbt()
							&& (player == null || !player.canUseCommandBlock())) {
						return false;
					}

					CompoundNBT compoundnbt1 = tileentity.write(new CompoundNBT());
					CompoundNBT compoundnbt2 = compoundnbt1.copy();
					compoundnbt1.merge(compoundnbt);
					compoundnbt1.putInt("x", pos.getX());
					compoundnbt1.putInt("y", pos.getY());
					compoundnbt1.putInt("z", pos.getZ());
					if (!compoundnbt1.equals(compoundnbt2)) {
						tileentity.read(compoundnbt1);
						tileentity.markDirty();
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public boolean isAvailable(Item item) {
		return item instanceof BlockItem;
	}

	private ArrayList<IItemCompressed> list = new ArrayList<IItemCompressed>();
	private IItemCompressed parent;

	@Override
	public ArrayList<IItemCompressed> getChildren() {
		return list;
	}

	@Override
	public String getCompressedName() {
		return "block";
	}

	@Override
	public IItemCompressed getParent() {
		return parent;
	}

	@Override
	public void setParent(IItemCompressed iItemCompressed) {
		this.parent = iItemCompressed;
	}
}
