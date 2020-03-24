package otamusan.nec.block.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import otamusan.nec.client.blockcompressed.CompressedData;
import otamusan.nec.client.blockcompressed.ModelCompressedBlock;
import otamusan.nec.common.Lib;
import otamusan.nec.register.BlockRegister;

public class TileCompressedBlock extends TileEntity implements ITileCompressed {

	public TileCompressedBlock() {
		super(BlockRegister.TILECOMPRESSEDTYPE);
	}

	public CompressedData data = new CompressedData();

	public boolean isNatural = false;

	public void setCompresseData(CompressedData data) {
		this.data = data;
	}

	public CompressedData getCompressedData() {
		return data;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		nbt.put(Lib.TILE_NBT_ITEM, data.getStackNbt());

		nbt.putInt(Lib.TILE_NBT_STATE, data.getStateInt());

		nbt.putBoolean(Lib.TILE_NBT_ISNATURUAL, isNatural);

		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt) {
		super.read(nbt);
		if (nbt.contains(Lib.TILE_NBT_ITEM) && nbt.contains(Lib.TILE_NBT_STATE)) {
			this.data = new CompressedData(nbt.getCompound(Lib.TILE_NBT_ITEM), nbt.getInt(Lib.TILE_NBT_STATE));
		}

		if (nbt.contains(Lib.TILE_NBT_ISNATURUAL)) {
			this.isNatural = nbt.getBoolean(Lib.TILE_NBT_ISNATURUAL);
		}
	}

	@Override
	public IModelData getModelData() {
		IModelData modelData = new ModelDataMap.Builder().withProperty(ModelCompressedBlock.COMPRESSED_PROPERTY)
				.build();
		modelData.setData(ModelCompressedBlock.COMPRESSED_PROPERTY, this.data);
		return modelData;
	}

	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
	}

	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		this.read(tag);
	}

	public void setNatural(boolean isnatural) {
		this.isNatural = isnatural;
	}

	public boolean isNatural() {
		return isNatural;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		read(pkt.getNbtCompound());
	}
}
