package de.smiles.appliedadditions.block.voidifier;

import net.minecraft.item.ItemStack;

public interface IConfigSlotHost
{
	public abstract ItemStack getConfig(int id);

	public abstract void setConfig(int id, ItemStack itemStack);
}
