package de.smiles.appliedadditions;

import de.smiles.appliedadditions.block.voidifier.TileVoidifier;
import de.smiles.appliedadditions.block.voidifier.Voidifier;
import de.smiles.appliedadditions.network.AAMessageHandler;
import de.smiles.appliedadditions.network.FieldUpdatePacket;
import de.smiles.appliedadditions.network.FlushPacket;
import de.smiles.appliedadditions.network.GuiHandler;
import de.smiles.appliedadditions.network.SyncTile;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = AppliedAdditions.ID, version = AppliedAdditions.VERSION)
@Mod.EventBusSubscriber(modid = AppliedAdditions.ID)
public class AppliedAdditions
{
    public static final String ID = "applied-additions";
    public static final String VERSION = "v1.01";
    public static AppliedAdditions instance = null;

	private static Voidifier voidifier = null;
	private static Item voidifierItem = null;

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent modelRegistryEvent)
	{
		ModelLoader.setCustomModelResourceLocation(voidifierItem, 0, new ModelResourceLocation(voidifierItem.getRegistryName(), "inventory"));
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		GameRegistry.registerTileEntity(TileVoidifier.class, AppliedAdditions.ID + ":tile_voidifier");

		event.getRegistry().register(voidifier = new Voidifier());
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(voidifierItem  = new ItemBlock(voidifier).setRegistryName(voidifier.getRegistryName()));
	}

	@EventHandler
	public void init(FMLPreInitializationEvent e)
	{
		instance = this;
		AAMessageHandler.register(FieldUpdatePacket.MessageHolder.class, FieldUpdatePacket.class, Side.SERVER);
		AAMessageHandler.register(FlushPacket.MessageHolder.class, FlushPacket.class, Side.SERVER);
		AAMessageHandler.register(SyncTile.MessageHolder.class, SyncTile.class, Side.CLIENT);
	}

	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler.INSTANCE);
	}

	@EventHandler
	public void init(FMLPostInitializationEvent e)
	{
		
	}
}
