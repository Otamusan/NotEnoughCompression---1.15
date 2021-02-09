package otamusan.nec.block.tileentity.compressedfurnace;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CompressedFurnaceScreen extends ContainerScreen<CompressedFurnaceContainer> {
	private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation(
			"textures/gui/container/furnace.png");
	//AbstractFurnaceScreen<AbstractFurnaceContainer>
	//private static final ResourceLocation field_214089_l = new ResourceLocation("textures/gui/recipe_button.png");
	//public final AbstractRecipeBookGui field_214088_k;

	public CompressedFurnaceScreen(CompressedFurnaceContainer p_i51089_1_, PlayerInventory p_i51089_2_,
			ITextComponent p_i51089_3_) {
		this(p_i51089_1_, p_i51089_2_, p_i51089_3_, FURNACE_GUI_TEXTURES);
	}

	private boolean field_214090_m;
	private final ResourceLocation field_214091_n;

	public CompressedFurnaceScreen(CompressedFurnaceContainer p_i51104_1_,
			PlayerInventory p_i51104_3_,
			ITextComponent p_i51104_4_, ResourceLocation p_i51104_5_) {
		super(p_i51104_1_, p_i51104_3_, p_i51104_4_);
		this.field_214091_n = p_i51104_5_;
	}

	public void init() {
		super.init();
		this.field_214090_m = this.width < 379;
		//this.field_214088_k.func_201520_a(this.width, this.height, this.minecraft, this.field_214090_m, this.container);
		//this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize);
		/*this.addButton((new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, field_214089_l,
				(p_214087_1_) -> {
					this.field_214088_k.func_201518_a(this.field_214090_m);
					this.field_214088_k.toggleVisibility();
					this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width,
							this.xSize);
					((ImageButton) p_214087_1_).setPosition(this.guiLeft + 20, this.height / 2 - 49);
				})));*/
	}

	public void tick() {
		super.tick();
		//this.field_214088_k.tick();
	}

	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground();
		if (this.field_214090_m) {
			this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
		} else {
			super.render(p_render_1_, p_render_2_, p_render_3_);
		}

		this.renderHoveredToolTip(p_render_1_, p_render_2_);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = this.title.getFormattedText();
		this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F,
				(float) (this.ySize - 96 + 2), 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(this.field_214091_n);
		int i = this.guiLeft;
		int j = this.guiTop;
		this.blit(i, j, 0, 0, this.xSize, this.ySize);
		if (((CompressedFurnaceContainer) this.container).func_217061_l()) {
			int k = ((CompressedFurnaceContainer) this.container).getBurnLeftScaled();
			this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}

		int l = ((CompressedFurnaceContainer) this.container).getCookProgressionScaled();
		this.blit(i + 79, j + 34, 176, 14, l + 1, 16);
	}

	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {

		return this.field_214090_m ? true
				: super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}

	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		super.handleMouseClick(slotIn, slotId, mouseButton, type);
	}

	protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_,
			int p_195361_7_) {
		boolean flag = p_195361_1_ < (double) p_195361_5_ || p_195361_3_ < (double) p_195361_6_
				|| p_195361_1_ >= (double) (p_195361_5_ + this.xSize)
				|| p_195361_3_ >= (double) (p_195361_6_ + this.ySize);

		return flag;
	}

	@Override
	public void removed() {
		super.removed();
	}
}
