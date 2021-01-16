package otamusan.nec.minterface;

import net.minecraft.client.renderer.model.ItemCameraTransforms;

public interface ItemRendererInterface{
    public void setTransformType(ItemCameraTransforms.TransformType t);
    public ItemCameraTransforms.TransformType getType();
}