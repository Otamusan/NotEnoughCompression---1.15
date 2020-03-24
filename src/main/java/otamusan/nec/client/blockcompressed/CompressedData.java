package otamusan.nec.client.blockcompressed;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

//For model
public class CompressedData {
	public BlockState state;
	//If BlockState cannot function properly
	public ItemStack stack;

	public CompressedData() {
		this.state = Blocks.STONE.getDefaultState();
		this.stack = ItemStack.EMPTY;
	}

	public CompressedData(BlockState state, ItemStack stack) {
		this.state = state;
		this.stack = stack.copy();
		this.stack.setCount(1);
	}

	public CompressedData(CompoundNBT itemnbt, int stateint) {
		this.stack = ItemStack.read(itemnbt);
		stack.setCount(1);
		this.state = Block.getStateById(stateint);
	}

	public CompoundNBT getStackNbt() {
		CompoundNBT nbt = new CompoundNBT();
		stack.write(nbt);
		return nbt;
	}

	public int getStateInt() {
		return state.getBlock().getStateId(state);
	}

	public void setStack(ItemStack stack) {
		this.stack = stack.copy();
		stack.setCount(1);
	}

	public void setState(BlockState state) {
		this.state = state;
	}

	public ItemStack getStack() {
		return stack.copy();
	}

	public BlockState getState() {
		return state;
	}

	public CompressedData copy() {
		return new CompressedData(state, stack.copy());
	}

}
