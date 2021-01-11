package otamusan.nec.block.tileentity;

import otamusan.nec.client.blockcompressed.CompressedData;

public interface ITileCompressed {
	public void setCompresseData(CompressedData data);

	public CompressedData getCompressedData();

	public void setNatural(boolean isnatural);

	/*
	 * Whether or not it was naturally generated.
	 * Naturally-generated blocks prevent mining.
	 */
	public boolean isNatural();
}
