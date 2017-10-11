package de.smiles.appliedadditions.block.voidifier;

import de.smiles.appliedadditions.network.AAMessageHandler;
import de.smiles.appliedadditions.network.FieldUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ConfigSlot extends Slot
{
	ContainerVoidifier container;

	public ConfigSlot(ContainerVoidifier container, int id, int x, int y)
	{
		super(container.tile, id, x, y);
		this.container = container;
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
