package otamusan.nec.item;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemCompressed {
	public boolean isAvailable(Item item);

	public ArrayList<IItemCompressed> getChildren();

	public default void addChildren(IItemCompressed iItemCompressed) {
		getChildren().add(iItemCompressed);
		iItemCompressed.setParent(this);
	}

	public default ItemStack onCompress(ItemStack original, ItemStack compressed) {
		return compressed;
	}

	public default Item getItem(Item original) {
		for (IItemCompressed iItemCompressed : getChildren()) {
			if (iItemCompressed.isAvailable(original)) {
				return iItemCompressed.getItem(original);
			}
		}
		return (Item) this;
	}

	public default ArrayList<Item> getCompressedItems() {
		ArrayList<Item> items = new ArrayList<>();
		items.add((Item) this);
		for (IItemCompressed iItemCompressed : getChildren()) {
			items.addAll(iItemCompressed.getCompressedItems());
		}
		return items;
	}

	public default ArrayList<IItemCompressed> getCompressedItemCompressed() {
		ArrayList<IItemCompressed> items = new ArrayList<>();
		items.add(this);
		for (IItemCompressed iItemCompressed : getChildren()) {
			items.addAll(iItemCompressed.getCompressedItemCompressed());
		}
		return items;
	}

	public String getCompressedName();

	public IItemCompressed getParent();

	public void setParent(IItemCompressed iItemCompressed);

	public default String getNameTreed() {

		if (getParent() == null) {
			return "_" + getCompressedName();
		}

		return getParent().getNameTreed() + "_" + getCompressedName();
	}

}
