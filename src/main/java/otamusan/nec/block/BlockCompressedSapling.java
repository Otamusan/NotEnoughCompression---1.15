package otamusan.nec.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SaplingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.BlockSnapshot;
import otamusan.nec.block.tileentity.ITileCompressed;
import otamusan.nec.client.blockcompressed.CompressedData;
import otamusan.nec.config.ConfigCommon;
import otamusan.nec.item.ItemCompressed;

public class BlockCompressedSapling extends BlockCompressed implements IGrowable {
	//SaplingBlock

	public BlockCompressedSapling(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isAvailable(Block item) {
		return (item instanceof SaplingBlock) && ConfigCommon.vspecializeCompressedSapling;
	}

	@Override
	public String getCompressedName() {
		return "sapling";
	}

	@Override
	public void tick(BlockState blockState, ServerWorld world, BlockPos pos,
			Random rand) {
		super.tick(blockState, world, pos, rand);
		if (!world.isAreaLoaded(pos, 1))
			return;
		//if (world.getLight(pos.up()) >= 9
		//		&& rand.nextInt(7) * ItemCompressed.getTotal(BlockCompressed.getOriginalItem(world, pos)) == 0) {
		if (rand.nextInt((int) (7 * ItemCompressed.getTotal(BlockCompressed.getOriginalItem(world, pos)))) == 0
				&& ConfigCommon.vslowDownTreeGrowthByTick) {
			grow(world, pos, rand);
		}
	}

	public void grow(ServerWorld world, BlockPos pos, Random rand) {
		BlockState original = getBlockStateIn(world, pos);

		BlockState state = world.getBlockState(pos);

		ITileCompressed tileCompressed = (ITileCompressed) world.getTileEntity(pos);
		ItemStack stack = tileCompressed.getCompressedData().getStack();
		int time = ItemCompressed.getTime(stack);

		boolean isReplaced = original.get(SaplingBlock.STAGE) == 0;

		world.captureBlockSnapshots = true;
		((SaplingBlock) original.getBlock()).func_226942_a_(world, pos, original, rand);
		world.captureBlockSnapshots = false;

		ArrayList<BlockSnapshot> snapshots = (ArrayList<BlockSnapshot>) world.capturedBlockSnapshots.clone();
		world.capturedBlockSnapshots.clear();

		if (isReplaced) {
			world.setBlockState(pos, state);
			ITileCompressed rtile = (ITileCompressed) world.getTileEntity(pos);
			rtile.setCompresseData(new CompressedData(original.cycle(SaplingBlock.STAGE),
					stack));
		} else {
			for (BlockSnapshot snapshot : snapshots) {
				BlockState treestate = snapshot.getCurrentBlock();
				if (isCompressedBlock(treestate.getBlock()))
					continue;
				if (treestate.getBlock() == Blocks.DIRT)
					continue;
				ItemStack treeitem = ItemCompressed.createCompressed(
						treestate.getBlock().getItem(snapshot.getWorld(), snapshot.getPos(), treestate), time);

				setCompressedBlock(snapshot.getPos(), snapshot.getWorld(),
						new CompressedData(treestate, treeitem), true);

				updateClient(world, pos);
			}
		}
	}

	/*public void func_226942_a_(ServerWorld world, BlockPos pos, BlockState blockState,
			Random rand) {
		if (blockState.get(SaplingBlock.STAGE) == 0) {
			world.setBlockState(pos, blockState.cycle(SaplingBlock.STAGE), 4);
		} else {
			if (!net.minecraftforge.event.ForgeEventFactory.saplingGrowTree(world, rand, pos))
				return;
			this.tree.func_225545_a_(world, world.getChunkProvider().getChunkGenerator(), pos,
					blockState, rand);
		}
	}*/

	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {

		return true;
	}

	public static boolean canBigTreeSpawnAt(BlockState blockUnder, IBlockReader worldIn, BlockPos pos, int xOffset,
			int zOffset) {
		Block block = blockUnder.getBlock();
		return block == worldIn.getBlockState(pos.add(xOffset, 0, zOffset)).getBlock()
				&& block == worldIn.getBlockState(pos.add(xOffset + 1, 0, zOffset)).getBlock()
				&& block == worldIn.getBlockState(pos.add(xOffset, 0, zOffset + 1)).getBlock()
				&& block == worldIn.getBlockState(pos.add(xOffset + 1, 0, zOffset + 1)).getBlock();
	}

	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		if (ConfigCommon.vslowDownTreeGrowthByBoneMeal)
			return (double) worldIn.rand.nextFloat() < (0.45D
					/ ItemCompressed.getTotal(BlockCompressed.getOriginalItem(worldIn, pos)));
		return worldIn.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_,
			BlockState p_225535_4_) {
		this.grow(p_225535_1_, p_225535_3_, p_225535_2_);
	}

}
