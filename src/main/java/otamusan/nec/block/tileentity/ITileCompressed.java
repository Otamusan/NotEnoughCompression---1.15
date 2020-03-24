package otamusan.nec.block.tileentity;

import otamusan.nec.client.blockcompressed.CompressedData;

public interface ITileCompressed {
	public void setCompresseData(CompressedData data);

	public CompressedData getCompressedData();

	public void setNatural(boolean isnatural);

	public boolean isNatural();
}
