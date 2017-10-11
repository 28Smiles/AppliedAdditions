package de.smiles.appliedadditions.block.voidifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.authlib.GameProfile;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import de.smiles.appliedadditions.network.AAMessageHandler;
import de.smiles.appliedadditions.network.SyncTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;

public class TileVoidifier extends TileEntity implements IActionHost, IGridBlock, IGridHost, IActionSource, ITickable, IInventory
{
	NonNullList<ItemStack> config = NonNullList.withSize(6, ItemStack.EMPTY);
	int[] limits = new int[]{ -1, -1, -1, -1, -1, -1 };

	private IGridNode node = null;
	private NBTTagCompound data = null;
	private boolean ready = false;

	private final IMEMonitorHandlerReceiver listener = new IMEMonitorHandlerReceiver<IAEItemStack>() {
		@Override
		public boolean isValid(Object arg0)
		{
			return true; //TODO
		}

		@Override
		public void onListUpdate()
		{
			
		}

		@Override
		public void postChange(IBaseMonitor<IAEItemStack> monitor, Iterable<IAEItemStack> iter, IActionSource source)
		{
			IStorageGrid grid = getActionableNode().getGrid().getCache(IStorageGrid.class);
			IMEMonitor<IAEItemStack> store = grid.getItemInventory();
			for(IAEItemStack is : iter)
			{
				IAEItemStack stack = store.getStorageList().findPrecise(is);
				if(stack != null)
					for(int i = 0; i < config.size(); i++)
						if(config.get(i).getItem() == stack.getItem() && config.get(i).getItemDamage() == stack.getItemDamage())
							if(limits[i] >= 0 && stack.getStackSize() > limits[i])
							{
								long dec = stack.getStackSize() - limits[i];
								store.extractItems(stack.copy().setStackSize(dec), Actionable.MODULATE, TileVoidifier.this);
								stack.decStackSize(dec);
							}
			}
		}
	};

	@Override
	public void update()
	{
		if(Math.random() > 0.8)
			world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), Math.random() * 0.1 - 0.05D, 0.01D, Math.random() * 0.1 - 0.05D);
		if(!ready)
		{
			flush();
			ready = true;
		}
	}

	@Override
	public IGridNode getActionableNode()
	{
		return getGridNode(null);
	}

	@Override
	public EnumSet<EnumFacing> getConnectableSides() 
	{
		return EnumSet.allOf(EnumFacing.class);
	}

	@Override
	public EnumSet<GridFlags> getFlags()
	{
		return EnumSet.of(GridFlags.DENSE_CAPACITY, GridFlags.REQUIRE_CHANNEL);
	}

	@Override
	public AEColor getGridColor()
	{
		return AEColor.TRANSPARENT;
	}

	@Override
	public double getIdlePowerUsage()
	{
		return 10;
	}

	@Override
	public DimensionalCoord getLocation()
	{
		return new DimensionalCoord(world, pos);
	}

	@Override
	public IGridHost getMachine()
	{
		return this;
	}

	@Override
	public ItemStack getMachineRepresentation()
	{
		return ItemStack.EMPTY; //TODO
	}

	@Override
	public void gridChanged()
	{
		IStorageGrid storage = getActionableNode().getGrid().getCache(IStorageGrid.class);
		IMEMonitor<IAEItemStack> store = storage.getItemInventory();
		store.addListener(listener, null);
	}

	@Override
	public boolean isWorldAccessible()
	{
		return true;
	}

	@Override
	public void onGridNotification(GridNotification gridNot)
	{
		
	}

	@Override
	public void setNetworkStatus(IGrid grid, int i)
	{
		
	}

	@Override
	public AECableType getCableConnectionType(AEPartLocation loc)
	{
		return AECableType.SMART;
	}

	@Override
	public void securityBreak()
	{
		
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		NBTTagCompound tag = super.getUpdateTag();
		NBTTagIntArray ar = new NBTTagIntArray(limits);
		tag.setTag("limits", ar);
		return tag;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		super.handleUpdateTag(tag);
		NBTTagIntArray ar = (NBTTagIntArray) tag.getTag("limits");
		limits = ar.getIntArray();

		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui != null && gui instanceof GuiVoidifier)
				((GuiVoidifier) gui).update();
		}
	}

	public void onDestroyed()
	{
		IGridNode node = getActionableNode();
		if(node == null) return;
		IStorageGrid grid = node.getGrid().getCache(IStorageGrid.class);
		grid.getItemInventory().removeListener(listener);
		node.destroy();
	}

	@Override
	public IGridNode getGridNode(AEPartLocation loc)
	{
		if(this.node == null && !world.isRemote)
		{
			this.node = AEApi.instance().createGridNode(this);
			this.node.updateState();
		}

		return this.node;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		this.data = tag;
		if( this.node != null && this.data != null )
		{
			this.node.loadFromNBT( "proxy", this.data );
			this.data = null;
		}
		NBTTagIntArray ar = (NBTTagIntArray) tag.getTag("limits");
		limits = ar.getIntArray();
		ItemStackHelper.loadAllItems(tag, config);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		if( this.node != null )
		{
			this.node.saveToNBT( "proxy", tag );
		}
		NBTTagIntArray ar = new NBTTagIntArray(limits);
		tag.setTag("limits", ar);
		ItemStackHelper.saveAllItems(tag, config);
		return tag;
	}

	@Override
	public <T> Optional<T> context(Class<T> arg0)
	{
		return Optional.empty();
	}

	@Override
	public Optional<IActionHost> machine()
	{
		return Optional.of(this);
	}

	@Override
	public Optional<EntityPlayer> player()
	{
		return Optional.empty();
	}

	public boolean hasPermission(EntityPlayer player)
	{
		IGridNode gridnote = getActionableNode();
		if(gridnote == null)
			return false;
		ISecurityGrid security = gridnote.getGrid().getCache(ISecurityGrid.class);
		return security.hasPermission(player, SecurityPermissions.EXTRACT);
	}

	@Override
	public String getName()
	{
		return "Voidifier";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public void clear()
	{
		config.clear();
	}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public ItemStack decrStackSize(int id, int amount)
	{
		return ItemStackHelper.getAndSplit(config, id, amount);
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public int getSizeInventory()
	{
		return config.size();
	}

	@Override
	public ItemStack getStackInSlot(int id)
	{
		return config.get(id);
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int id, ItemStack stack)
	{
		return true;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public ItemStack removeStackFromSlot(int id)
	{
		return ItemStackHelper.getAndRemove(config, id);
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public void setInventorySlotContents(int id, ItemStack stack)
	{
		config.set(id, stack);
	}

	public void setLimit(int id, int value)
	{
		limits[id] = value;
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			AAMessageHandler.INSTANCE.sendToAll(new SyncTile(this));
	}

	public void flush()
	{
		IGridNode gridnote = getActionableNode();
		if(gridnote == null)
			return;
		
		IStorageGrid storage = gridnote.getGrid().getCache(IStorageGrid.class);
		IMEMonitor<IAEItemStack> store = storage.getItemInventory();
		storage.getItemInventory().getStorageList().iterator().forEachRemaining(new Consumer<IAEItemStack>() {

			@Override
			public void accept(IAEItemStack stack)
			{
				if(stack != null)
					for(int i = 0; i < config.size(); i++)
						if(config.get(i).getItem() == stack.getItem() && config.get(i).getItemDamage() == stack.getItemDamage())
							if(limits[i] >= 0 && stack.getStackSize() > limits[i])
							{
								long dec = stack.getStackSize() - limits[i];
								store.extractItems(stack.copy().setStackSize(dec), Actionable.MODULATE, TileVoidifier.this);
							}
			}
		});
	}
}
