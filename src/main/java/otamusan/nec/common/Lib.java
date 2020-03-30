package otamusan.nec.common;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class Lib {
	public static final String MODID = "notenoughcompression";
	public static final String MODNAME = "NotEnoughCompression";

	public static final String ITEM_NBT_TIME = MODID + "_time";
	public static final String ITEM_NBT_ORIGINAL = MODID + "_original";

	public static final String TILE_NBT_STATE = MODID + "_originalstate";
	public static final String TILE_NBT_ITEM = MODID + "_originalitem";
	public static final String TILE_NBT_ISNATURUAL = MODID + "_isnarural";

	public static final ResourceLocation ITEM_COMPRESSED = new ResourceLocation(MODID, "itemcompressed");
	public static final ResourceLocation ITEM_BLOCKCOMPRESSED = new ResourceLocation(MODID, "itemblockcompressed");
	public static final ResourceLocation ITEM_COMPRESSEDTOOL = new ResourceLocation(MODID, "itemcompressedtool");
	public static final ResourceLocation ITEM_COMPRESSEDSWORD = new ResourceLocation(MODID, "itemcompressedsword");

	public static final ResourceLocation BLOCK_COMPRESSED = new ResourceLocation(MODID, "blockcompressed");
	public static final ResourceLocation BLOCK_COMPRESSEDFURNACE = new ResourceLocation(MODID,
			"blockcompressedfurnace");
	public static final ResourceLocation BLOCK_COMPRESSEDBREWINGSTAND = new ResourceLocation(MODID,
			"blockcompressedbrewingstand");
	public static final ResourceLocation BLOCK_COMPRESSEDSAPLING = new ResourceLocation(MODID,
			"blockcompressedsapling");

	public static final ModelResourceLocation ITEM_COMPRESSED_MODEL = new ModelResourceLocation(
			MODID + ":itemcompressedmodel", "inventory");
	public static final ModelResourceLocation ITEM_BLOCKCOMPRESSED_MODEL = new ModelResourceLocation(
			MODID + ":itemblockcompressedmodel", "inventory");
	public static final ModelResourceLocation ITEM_COMPRESSEDTOOL_MODEL = new ModelResourceLocation(
			MODID + ":itemcompressedtoolmodel", "inventory");
	public static final ModelResourceLocation ITEM_COMPRESSEDSWORD_MODEL = new ModelResourceLocation(
			MODID + ":itemcompressedswordmodel", "inventory");
	public static final ModelResourceLocation BLOCK_COMPRESSED_MODEL = new ModelResourceLocation(
			MODID + ":blockcompressed");
	public final static ResourceLocation RECIPE_COMPRESSION = new ResourceLocation(MODID, "compression");
	public final static ResourceLocation RECIPE_DECOMPRESSION = new ResourceLocation(MODID, "decompression");
	public final static ResourceLocation RECIPE_COMPRESSEDCRAFTING = new ResourceLocation(MODID, "compressedcrafting");

	public final static ResourceLocation TILETYPE_COMPRESSEDBLOCK = new ResourceLocation(MODID, "compressedblock");
	public final static ResourceLocation TILETYPE_COMPRESSEDBLOCKFURNACE = new ResourceLocation(MODID,
			"compressedblockfurnace");
	public final static ResourceLocation TILETYPE_COMPRESSEDBLOCKBREWINGSTAND = new ResourceLocation(MODID,
			"compressedblockbrewingstand");

	public final static ResourceLocation CONTAINERTYPE_FURNACE = new ResourceLocation(MODID, "compressedfurnace");
	public final static ResourceLocation CONTAINERTYPE_BREWINGSTAND = new ResourceLocation(MODID,
			"compressedbrewingstand");

	public final static ResourceLocation FEATURE_COMPRESSEDCHUNK = new ResourceLocation(MODID, "compressedchunk");

	public final static String BSPROPERTY_SIDERENDER = MODID + "_siderender";
	public final static ResourceLocation CAPPLAYER_ISFIRST = new ResourceLocation(MODID, "isfirst");
}
