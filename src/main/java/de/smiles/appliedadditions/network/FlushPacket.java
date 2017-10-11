package de.smiles.appliedadditions.network;

import de.smiles.appliedadditions.block.voidifier.ContainerVoidifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FlushPacket implements IMessage
{
	private int cid;

	public FlushPacket(int cid)
	{
		this.cid = cid;
	}
	
	public FlushPacket() {}

	@Override
	public void fromBytes(ByteBuf bb)
	{
		cid = bb.readInt();
	}

	@Override
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(cid);
	}

	public static class MessageHolder implements IMessageHandler<FlushPacket,IMessage>
    {
		@Override
		public IMessage onMessage(FlushPacket packet, MessageContext mc)
		{
			final EntityPlayerMP thePlayer = (EntityPlayerMP) mc.getServerHandler().player;
            thePlayer.getServer().addScheduledTask(new Runnable() {
                        @Override
                        public void run() 
                        {
							Container container = mc.getServerHandler().player.openContainer;
							if(container != null && container.windowId == packet.cid)
								if(container instanceof ContainerVoidifier)
									((ContainerVoidifier) container).tile.flush();
                        }
            	});
			return null;
		}
    }
}
