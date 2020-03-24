package otamusan.nec.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import otamusan.nec.block.blockstate.CompressedBlockState;
import otamusan.nec.block.tileentity.ITileCompressed;
import otamusan.nec.block.tileentity.TileCompressedBlock;
import otamusan.nec.client.blockcompressed.CompressedData;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.register.BlockRegister;

public class BlockCompressed extends Block implements IBlockCompressed {

	public StateContainer<Block, BlockState> stateContainer;

	public BlockCompressed() {
		super(Block.Properties.create(Material.MISCELLANEOUS).func_226896_b_());
		//this.setDefaultState(this.stateContainer.getBaseState().with(PRO_SIDERENDER, true));
		StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
		this.fillStateContainer(builder);
		this.stateContainer = builder.create(CompressedBlockState::new);
		this.setDefaultState(stateContainer.getBaseState().with(PRO_SIDERENDER, true));
	}

	@Override
	public StateContainer<Block, BlockState> getStateContainer() {
		return this.stateContainer;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileCompressedBlock();
	}

	public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_,
			PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
		ITileCompressed tile = (ITileCompressed) p_225533_2_.getTileEntity(p_225533_3_);
		return ActionResultType.PASS;
	}

	/*public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}*/
	//TorchBlock

	public static BlockState getOriginalState(IBlockReader world, BlockPos pos) {
		return getCompressedData(world, pos).getState();
	}

	public static ItemStack getOriginalItem(IBlockReader world, BlockPos pos) {
		return getCompressedData(world, pos).getStack();
	}

	//Give property of whether to render side instead of tileentity
	//public static PropertySideRender PRO_SIDERENDER = new PropertySideRender();
	public static BooleanProperty PRO_SIDERENDER = BooleanProperty.create("siderender");

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getState(context.getWorld(), context.getItem(), context.getPos());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	/*@Override
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		if (!state.has(PRO_SIDERENDER))
			return false;
		return !state.get(PRO_SIDERENDER).booleanValue();
	}*/

	/*@Override
	public boolean isSolid(BlockState state) {
		return true;
	}*/

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBlockHarvested(worldIn, pos, state, player);
		ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
		ITileCompressed tileCompressed = (ITileCompressed) worldIn.getTileEntity(pos);
		BlockState BlockState = tileCompressed.getCompressedData().getState();
		if (worldIn.isRemote)
			return;

		if (BlockState != null) {
			ServerWorld world = (ServerWorld) worldIn;
			int time = ItemCompressed.getTime(tileCompressed.getCompressedData().getStack());

			List<ItemStack> itemlist = Block.getDrops(BlockState, world, pos, worldIn.getTileEntity(pos), player,
					heldItem);

			for (ItemStack itemStack : itemlist) {
				ItemStack compressed = ItemCompressed.createCompressed(itemStack, time);
				compressed.setCount(itemStack.getCount());
				spawnAsEntity(worldIn, pos, compressed);
			}

		} else {
			ItemStack itemCompressed = tileCompressed.getCompressedData().getStack().copy();
			itemCompressed.setCount(1);
			spawnAsEntity(worldIn, pos, itemCompressed);
		}
	}

	public static CompressedData getCompressedData(IBlockReader world, BlockPos pos) {
		if (!(world.getTileEntity(pos) instanceof ITileCompressed))
			return new CompressedData();
		ITileCompressed tile = (ITileCompressed) world.getTileEntity(pos);
		return tile.getCompressedData().copy();
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return getOriginalItem(worldIn, pos).copy();
	}

	public static Block getCompressedBlockS(Block original) {
		return BlockRegister.BLOCK_COMPRESSED.getCompressedBlock(original);
	}

	public static boolean isCompressedBlock(Block block) {
		return block instanceof IBlockCompressed;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(PRO_SIDERENDER);
		super.fillStateContainer(builder);
	}

	private ArrayList<IBlockCompressed> children = new ArrayList<>();
	private IBlockCompressed parent;

	@Override
	public boolean isAvailable(Block item) {
		return true;
	}

	@Override
	public ArrayList<IBlockCompressed> getChildren() {
		return children;
	}

	@Override
	public String getCompressedName() {
		return "base";
	}

	@Override
	public IBlockCompressed getParent() {
		return parent;
	}

	@Override
	public void setParent(IBlockCompressed iBlockCompressed) {
		parent = iBlockCompressed;
	}

	@Override
	public IBlockCompressed getBlocktoIBlockCompressed() {
		return this;
	}

}
