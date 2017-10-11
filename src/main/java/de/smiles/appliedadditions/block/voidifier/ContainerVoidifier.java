package de.smiles.appliedadditions.block.voidifier;

import de.smiles.appliedadditions.network.AAMessageHandler;
import de.smiles.appliedadditions.network.FieldUpdatePacket;
import de.smiles.appliedadditions.network.SyncTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerVoidifier extends Container
{
	public TileVoidifier tile;

	public ContainerVoidifier(TileVoidifier tile, EntityPlayer player)
	{
		this.tile = tile;
		for(int i = 0; i < 3; i++)
			addSlotToContainer(new ConfigSlot(this, i, 14, i * 22 + 8));
		for(int i = 0; i < 3; i++)
			addSlotToContainer(new ConfigSlot(this, i + 3, 91, i * 22 + 8));

		// Player Inventory, Slot 9-35, Slot IDs 9-35
	    for (int y = 0; y < 3; ++y) {
	        for (int x = 0; x < 9; ++x) {
	            this.addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
	        }
	    }
	    // Player Inventory, Slot 0-8, Slot IDs 36-44
	    for (int x = 0; x < 9; ++x) {
	        this.addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142));
	    }
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tile.hasPermission(player);
	}
}
