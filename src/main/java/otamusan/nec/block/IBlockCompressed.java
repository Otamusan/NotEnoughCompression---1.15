package otamusan.nec.block;

import java.util.ArrayList;

import net.minecraft.block.Block;

public interface IBlockCompressed {

	public boolean isAvailable(Block item);

	public ArrayList<IBlockCompressed> getChildren();

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

	IBlockCompressed getBlocktoIBlockCompressed();

}
