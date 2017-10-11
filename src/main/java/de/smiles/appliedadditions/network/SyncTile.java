package de.smiles.appliedadditions.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncTile implements IMessage
{
	BlockPos pos;
	NBTTagCompound nbt;

	public SyncTile(TileEntity te)
	{
		pos = te.getPos();
		nbt = te.getUpdateTag();
	}

	public SyncTile() {}

	@Override
	public void fromBytes(ByteBuf bb)
	{
		pos = BlockPos.fromLong(bb.readLong());
		nbt = ByteBufUtils.readTag(bb);
	}

	@Override
	public void toBytes(ByteBuf bb)
	{
		bb.writeLong(pos.toLong());
		ByteBufUtils.writeTag(bb, nbt);
	}

	public static class MessageHolder implements IMessageHandler<SyncTile,IMessage>
    {
		@Override
		public IMessage onMessage(SyncTile packet, MessageContext mc)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(packet.pos);
				tile.handleUpdateTag(packet.nbt);
				tile.markDirty();
			});
			return null;
		}
    }
}
