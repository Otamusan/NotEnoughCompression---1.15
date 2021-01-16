package otamusan.nec.register;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import otamusan.nec.common.Lib;
import otamusan.nec.recipe.CompressedCraftingRecipe;
import otamusan.nec.recipe.CompressionRecipe;
import otamusan.nec.recipe.DecompressionRecipe;
import otamusan.nec.recipe.ReplacedShapedRecipe;
import otamusan.nec.recipe.ReplacedShapelessRecipe;
import otamusan.nec.recipe.ReplacedShapelessRecipe.Serializer;

@Mod.EventBusSubscriber(modid = Lib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegister {
	public RecipeRegister() {
	}

	public static Serializer replacedShapeless = new ReplacedShapelessRecipe.Serializer();
	public static otamusan.nec.recipe.ReplacedShapedRecipe.Serializer replacedShaped = new ReplacedShapedRecipe.Serializer();
	public static SpecialRecipeSerializer compression = new SpecialRecipeSerializer<>(CompressionRecipe::new);
	public static SpecialRecipeSerializer decompression = new SpecialRecipeSerializer<>(DecompressionRecipe::new);
	public static SpecialRecipeSerializer compressedcrafting = new SpecialRecipeSerializer<>(CompressedCraftingRecipe::new);

	@SubscribeEvent
	public static void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e) {
		compression.setRegistryName(Lib.RECIPE_COMPRESSION);
		e.getRegistry().register(compression);
		decompression.setRegistryName(Lib.RECIPE_DECOMPRESSION);
		e.getRegistry().register(decompression);

		replacedShapeless.setRegistryName(ReplacedShapelessRecipe.Serializer.NAME);
		replacedShaped.setRegistryName(ReplacedShapedRecipe.Serializer.NAME);
		e.getRegistry().register(replacedShapeless);
		e.getRegistry().register(replacedShaped);

		compressedcrafting.setRegistryName(Lib.RECIPE_COMPRESSEDCRAFTING);
		e.getRegistry().register(compressedcrafting);
	}

	@SubscribeEvent
	public static void onRecipeUpdated(RecipesUpdatedEvent e) {

	}
}
