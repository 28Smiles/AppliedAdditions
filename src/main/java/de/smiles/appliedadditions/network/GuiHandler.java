package de.smiles.appliedadditions.network;

import de.smiles.appliedadditions.block.voidifier.ContainerVoidifier;
import de.smiles.appliedadditions.block.voidifier.GuiVoidifier;
import de.smiles.appliedadditions.block.voidifier.TileVoidifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	public static final GuiHandler INSTANCE = new GuiHandler();

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		return new GuiVoidifier(new ContainerVoidifier((TileVoidifier) tile, player));
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(id == 0)
			return new ContainerVoidifier((TileVoidifier) tile, player);
		return null;
	}

}
