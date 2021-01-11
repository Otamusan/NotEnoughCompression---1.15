package otamusan.nec.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import otamusan.nec.config.ConfigCommon;
import otamusan.nec.item.ItemCompressed;

public class BlockCompressedCrops extends BlockCompressed implements IGrowable, IPlantable {
	//CropsBlock

	public BlockCompressedCrops(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isAvailable(Block item) {
		return item instanceof CropsBlock;
	}

	@Override
	public String getCompressedName() {
		return "crops";
	}

	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		//return !this.isMaxAge(state);
		return !this.isMaxAge(worldIn, pos, state);
	}

	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return ((CropsBlock) BlockCompressed.getOriginalState(worldIn, pos).getBlock()).canUseBonemeal(worldIn, rand,
				pos, getBlockStateIn(worldIn, pos));
	}

	public int getMaxAge(IBlockReader worldIn, BlockPos pos) {
		return ((CropsBlock) getOriginalState(worldIn, pos).getBlock()).getMaxAge();
	}

	protected int getAge(World world, BlockPos pos, BlockState state) {
		return getOriginalState(world, pos).get(CropsBlock.AGE);
		//state.get(this.getAgeProperty());
	}

	public BlockState withAge(World world, BlockPos pos, int age) {
		return getOriginalState(world, pos).getBlock().getDefaultState().with(CropsBlock.AGE, Integer.valueOf(age));
	}

	public boolean isMaxAge(IBlockReader worldIn, BlockPos pos, BlockState state) {
		//return state.get(this.getAgeProperty()) >= this.getMaxAge();
		return getOriginalState(worldIn, pos).get(CropsBlock.AGE) >= this.getMaxAge(worldIn, pos);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult p_225533_6_) {
		return super.onBlockActivated(state, worldIn, pos, player, handIn, p_225533_6_);
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		super.tick(state, worldIn, pos, rand);
		if (!worldIn.isAreaLoaded(pos, 1))
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light

		if (new Random()
				.nextInt((int) Math.pow(8, ItemCompressed.getTime(BlockCompressed.getOriginalItem(worldIn, pos)))) != 0
				&& ConfigCommon.CONFIG_COMMON.slowDownCropsGrowthByTick.get())
			return;
		//if (worldIn.getLightSubtracted(pos, 0) >= 9) {
		int i = this.getAge(worldIn, pos, state);
		if (i < this.getMaxAge(worldIn, pos)) {
			float f = getGrowthChance(this, worldIn, pos);
			if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,
					rand.nextInt((int) (25.0F / f) + 1) == 0)) {
				//world.setBlockState(pos, this.withAge(world, pos, i + 1), 2);
				setBlockStateIn(worldIn, pos, this.withAge(worldIn, pos, i + 1), true);
				net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
			}
		}
		//}
	}

	protected static float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockpos = pos.down();

		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				float f1 = 0.0F;
				BlockState blockstate = worldIn.getBlockState(blockpos.add(i, 0, j));
				if (blockstate.canSustainPlant(worldIn, blockpos.add(i, 0, j), net.minecraft.util.Direction.UP,
						(net.minecraftforge.common.IPlantable) blockIn)) {
					f1 = 1.0F;
					if (blockstate.isFertile(worldIn, blockpos.add(i, 0, j))) {
						f1 = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					f1 /= 4.0F;
				}

				f += f1;
			}
		}

		BlockPos blockpos1 = pos.north();
		BlockPos blockpos2 = pos.south();
		BlockPos blockpos3 = pos.west();
		BlockPos blockpos4 = pos.east();
		boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock()
				|| blockIn == worldIn.getBlockState(blockpos4).getBlock();
		boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock()
				|| blockIn == worldIn.getBlockState(blockpos2).getBlock();
		if (flag && flag1) {
			f /= 2.0F;
		} else {
			boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock()
					|| blockIn == worldIn.getBlockState(blockpos4.north()).getBlock()
					|| blockIn == worldIn.getBlockState(blockpos4.south()).getBlock()
					|| blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();
			if (flag2) {
				f /= 2.0F;
			}
		}

		return f;
	}

	public void grow(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_,
			BlockState p_225535_4_) {
		this.grow(p_225535_1_, p_225535_3_, p_225535_4_);
	}

	protected int getBonemealAgeIncrease(World worldIn) {
		return MathHelper.nextInt(worldIn.rand, 2, 5);
	}

	public void grow(World worldIn, BlockPos pos, BlockState state) {
		if (new Random()
				.nextInt((int) Math.pow(8, ItemCompressed.getTime(BlockCompressed.getOriginalItem(worldIn, pos)))) != 0
				&& ConfigCommon.CONFIG_COMMON.slowDownCropsGrowthByBoneMeal.get())
			return;

		int i = this.getAge(worldIn, pos, state) + this.getBonemealAgeIncrease(worldIn);
		int j = this.getMaxAge(worldIn, pos);
		if (i > j) {
			i = j;
		}
		setBlockStateIn(worldIn, pos, withAge(worldIn, pos, i), true);
	}

	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos);
	}
}
