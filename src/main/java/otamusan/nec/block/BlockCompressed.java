package otamusan.nec.block;

import java.util.ArrayList;
import java.util.List;

import io.netty.handler.codec.EncoderException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import otamusan.nec.block.blockstate.CompressedBlockState;
import otamusan.nec.block.tileentity.ITileCompressed;
import otamusan.nec.block.tileentity.TileCompressedBlock;
import otamusan.nec.client.blockcompressed.CompressedData;
import otamusan.nec.item.ItemCompressed;
import otamusan.nec.register.BlockRegister;

public class BlockCompressed extends Block implements IBlockCompressed {

	public StateContainer<Block, BlockState> stateContainer;

	public BlockCompressed(Block.Properties properties) {
		super(properties);
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

	@Override
	public IBlockCompressed getBlocktoIBlockCompressed() {
		return this;
	}

	/*
	 * Give property of whether to render side instead of tileentity
	 */
	public static BooleanProperty PRO_SIDERENDER = BooleanProperty.create("siderender");

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getState(context.getItem());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isVariableOpacity() {
		return true;
	}

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

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(PRO_SIDERENDER);
		super.fillStateContainer(builder);
	}

	/*
	 * If you create a Block class that does not inherit from BlockCompressed but inherits from IBlockCompressed,
	 * you should copy the above methods from here
	 */

	public static BlockState getOriginalState(IBlockReader world, BlockPos pos) {
		return getCompressedData(world, pos).getState();
	}

	public static ItemStack getOriginalItem(IBlockReader world, BlockPos pos) {
		return getCompressedData(world, pos).getStack();
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

	public static BlockState getBlockStateIn(IBlockReader worldIn, BlockPos pos) {
		if (!(worldIn.getTileEntity(pos) instanceof ITileCompressed))
			throw new Error("not compressedtile");
		ITileCompressed tileCompressed = (ITileCompressed) worldIn.getTileEntity(pos);
		return tileCompressed.getCompressedData().getState();
	}

	public static void setBlockStateIn(World world, BlockPos pos, BlockState state) {
		setBlockStateIn(world, pos, state, true);
	}

	public static void setBlockStateIn(World world, BlockPos pos, BlockState state, boolean isUpdateClient) {
		if (!(world.getTileEntity(pos) instanceof ITileCompressed))
			throw new Error("not compressedtile");
		ITileCompressed tileCompressed = (ITileCompressed) world.getTileEntity(pos);
		tileCompressed.setCompresseData(new CompressedData(state, tileCompressed.getCompressedData().getStack()));

		if (!isUpdateClient)
			return;

		world.setBlockState(pos, getState(state), 11);
		world.setTileEntity(pos, (TileEntity) tileCompressed);

		updateClient(world, pos);
	}

	public static void updateClient(World world, BlockPos pos) {
		ITileCompressed tileCompressed = (ITileCompressed) world.getTileEntity(pos);
		//lightCheck(world, pos);

		for (PlayerEntity player : world.getPlayers()) {
			if (player instanceof ServerPlayerEntity) {
				ServerPlayerEntity entityPlayer = (ServerPlayerEntity) player;
				entityPlayer.connection.sendPacket(new SChangeBlockPacket(world, pos));
				entityPlayer.connection.sendPacket(((TileEntity) tileCompressed).getUpdatePacket());
			}
		}
		try {
			Minecraft.getInstance().worldRenderer.markBlockRangeForRenderUpdate(pos.getX() - 1, pos.getY() - 1,
					pos.getZ() - 1,
					pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		} catch (Exception e) {
		}
	}

	public static void lightCheck(IWorld worldIn, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			worldIn.getChunkProvider().getLightManager().checkBlock(pos.offset(direction));
		}

		/*for (PlayerEntity player : worldIn.getPlayers()) {
			if (player instanceof ServerPlayerEntity) {
				ServerPlayerEntity entityPlayer = (ServerPlayerEntity) player;
				entityPlayer.connection.sendPacket(
						new SUpdateLightPacket(new ChunkPos(pos), worldIn.getChunkProvider().getLightManager()));
			}
		}
		worldIn.getChunkProvider().getLightManager().getLightEngine(LightType.BLOCK)
				.updateSectionStatus(SectionPos.from(pos), false);*/
	}

	public static boolean setCompressedBlock(BlockPos pos, IWorld worldIn, CompressedData data, boolean isNatural) {
		BlockState compressedstate = getState(data.getState());
		worldIn.setBlockState(pos, compressedstate, 11);

		if (!(worldIn.getTileEntity(pos) instanceof ITileCompressed)) {
			return false;
		}

		ITileCompressed tileCompressed = (ITileCompressed) worldIn.getTileEntity(pos);

		if (tileCompressed == null) {
			return false;
		}

		tileCompressed.setCompresseData(data);
		tileCompressed.setNatural(isNatural);
		return true;

	}

	public static BlockState getState(ItemStack compressed) {
		BlockState original = ((BlockItem) (ItemCompressed.getOriginal(compressed).getItem())).getBlock()
				.getDefaultState();
		return getState(original);
	}

	public static BlockState getState(BlockState original) {
		BlockState state = BlockCompressed.getCompressedBlockS(original.getBlock()).getDefaultState();
		return state.with(BlockCompressed.PRO_SIDERENDER, original.isSolid());
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

}
