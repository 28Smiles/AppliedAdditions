package de.smiles.appliedadditions.network;

import de.smiles.appliedadditions.block.voidifier.ContainerVoidifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FieldUpdatePacket implements IMessage
{
	private int cid, id, value;

	public FieldUpdatePacket(int cid, int id, int value)
	{
		this.cid = cid;
		this.id = id;
		this.value = value;
	}

	public FieldUpdatePacket() {}

	@Override
	public void fromBytes(ByteBuf bb)
	{
		cid = bb.readInt();
		id = bb.readInt();
		value = bb.readInt();
	}

	@Override
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(cid);
		bb.writeInt(id);
		bb.writeInt(value);
	}

	public static class MessageHolder implements IMessageHandler<FieldUpdatePacket,IMessage>
    {
		@Override
		public IMessage onMessage(FieldUpdatePacket packet, MessageContext mc)
		{
			final EntityPlayerMP thePlayer = (EntityPlayerMP) mc.getServerHandler().player;
            thePlayer.getServer().addScheduledTask(new Runnable() {
                        @Override
                        public void run() 
                        {
							Container container = mc.getServerHandler().player.openContainer;
							if(container != null && container.windowId == packet.cid)
								if(container instanceof ContainerVoidifier)
									((ContainerVoidifier) container).tile.setLimit(packet.id, packet.value);
                        }
            	});
			return null;
		}
    }
}
