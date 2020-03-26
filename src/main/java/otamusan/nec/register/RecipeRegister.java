package otamusan.nec.register;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

	@SubscribeEvent
	public static void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e) {
		e.getRegistry().register(new SpecialRecipeSerializer<>(CompressionRecipe::new)
				.setRegistryName(Lib.RECIPE_COMPRESSION));
		e.getRegistry().register(new SpecialRecipeSerializer<>(DecompressionRecipe::new)
				.setRegistryName(Lib.RECIPE_DECOMPRESSION));

		replacedShapeless.setRegistryName(ReplacedShapelessRecipe.Serializer.NAME);
		replacedShaped.setRegistryName(ReplacedShapedRecipe.Serializer.NAME);

		e.getRegistry().register(replacedShapeless);
		e.getRegistry().register(replacedShaped);
		e.getRegistry().register(new SpecialRecipeSerializer<>(CompressedCraftingRecipe::new)
				.setRegistryName(Lib.RECIPE_COMPRESSEDCRAFTING));

	}
}
