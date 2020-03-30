package otamusan.nec.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import otamusan.nec.block.tileentity.ITileCompressed;
import otamusan.nec.client.blockcompressed.CompressedData;
import otamusan.nec.item.ItemCompressed;

public interface IBlockCompressed {

	public boolean isAvailable(Block item);

	public ArrayList<IBlockCompressed> getChildren();

	public static BlockState getBlockStateIn(World world, BlockPos pos) {
		if (!(world.getTileEntity(pos) instanceof ITileCompressed))
			throw new Error("not compressedtile");
		ITileCompressed tileCompressed = (ITileCompressed) world.getTileEntity(pos);
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
		lightCheck(world, pos);
	}

	public static void lightCheck(IWorld worldIn, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			worldIn.getChunkProvider().getLightManager().checkBlock(pos.offset(direction));
		}
	}

	public static boolean setCompressedBlock(BlockPos pos, IWorld worldIn, CompressedData data, boolean isNatural) {
		BlockState compressedstate = IBlockCompressed.getState(data.getState());
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

	public default void addChildren(IBlockCompressed iBlockCompressed) {
		getChildren().add(iBlockCompressed);
		iBlockCompressed.setParent(this);
	}

	public default Block getCompressedBlock(Block original) {
		for (IBlockCompressed iBlockCompressed : getChildren()) {
			if (iBlockCompressed.isAvailable(original)) {
				return iBlockCompressed.getCompressedBlock(original);
			}
		}
		return (Block) this;
	}

	public default ArrayList<Block> getCompressedBlocks() {
		ArrayList<Block> blocks = new ArrayList<>();
		blocks.add((Block) this);
		for (IBlockCompressed iBlockCompressed : getChildren()) {
			blocks.addAll(iBlockCompressed.getCompressedBlocks());
		}
		return blocks;
	}

	public default ArrayList<IBlockCompressed> getCompressedBlockCompressed() {
		ArrayList<IBlockCompressed> blocks = new ArrayList<>();
		blocks.add(this);
		for (IBlockCompressed iBlockCompressed : getChildren()) {
			blocks.addAll(iBlockCompressed.getCompressedBlockCompressed());
		}
		return blocks;
	}

	public String getCompressedName();

	public IBlockCompressed getParent();

	public void setParent(IBlockCompressed iBlockCompressed);

	public default String getNameTreed() {

		if (getParent() == null) {
			return "_" + getCompressedName();
		}

		return getParent().getNameTreed() + "_" + getCompressedName();
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

	IBlockCompressed getBlocktoIBlockCompressed();

}
