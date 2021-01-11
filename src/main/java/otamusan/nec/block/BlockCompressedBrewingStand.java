package otamusan.nec.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import otamusan.nec.block.tileentity.compressedbrewingstand.TileCompressedBrewingStand;

public class BlockCompressedBrewingStand extends BlockCompressed {
	public BlockCompressedBrewingStand(Properties properties) {
		super(properties);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileCompressedBrewingStand();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	//BrewingStandBlock

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		double d0 = (double) pos.getX() + 0.4D + (double) rand.nextFloat() * 0.2D;
		double d1 = (double) pos.getY() + 0.7D + (double) rand.nextFloat() * 0.3D;
		double d2 = (double) pos.getZ() + 0.4D + (double) rand.nextFloat() * 0.2D;
		worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public boolean isAvailable(Block item) {
		return item == Blocks.BREWING_STAND;
	}

	@Override
	public String getCompressedName() {
		return "brewingstand";
	}

	protected void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileCompressedBrewingStand) {
			player.openContainer((INamedContainerProvider) tileentity);
		}

	}

	@Override
	public ActionResultType onBlockActivated(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_,
			PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
		if (!p_225533_2_.isRemote) {
			this.interactWith(p_225533_2_, p_225533_3_, p_225533_4_);
		}
		return ActionResultType.SUCCESS;
	}

	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileCompressedBrewingStand) {
				InventoryHelper.dropInventoryItems(worldIn, pos, (TileCompressedBrewingStand) tileentity);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}
}
