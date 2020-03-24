package otamusan.nec.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import otamusan.nec.block.tileentity.compressedfurnace.TileCompressedFurnace;

//FurnaceBlock
public class BlockCompressedFurnace extends BlockCompressed {
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileCompressedFurnace();
	}

	@Override
	public boolean isAvailable(Block item) {
		return item == Blocks.FURNACE;
	}

	@Override
	public String getCompressedName() {
		return "furnace";
	}

	protected void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof TileCompressedFurnace) {
			player.openContainer((INamedContainerProvider) tileentity);
		}

	}

	public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_,
			PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
		if (!p_225533_2_.isRemote) {
			this.interactWith(p_225533_2_, p_225533_3_, p_225533_4_);
		}
		return ActionResultType.SUCCESS;
	}

	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileCompressedFurnace) {
				InventoryHelper.dropInventoryItems(worldIn, pos, (AbstractFurnaceTileEntity) tileentity);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}
}
