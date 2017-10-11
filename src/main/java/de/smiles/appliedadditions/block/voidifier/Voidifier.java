package de.smiles.appliedadditions.block.voidifier;

import de.smiles.appliedadditions.AppliedAdditions;
import de.smiles.appliedadditions.network.AAMessageHandler;
import de.smiles.appliedadditions.network.SyncTile;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Voidifier extends Block implements ITileEntityProvider
{

	public Voidifier()
	{
		super(Material.IRON);
		setUnlocalizedName("voidifier");
		setRegistryName("voidifier");
		setCreativeTab(CreativeTabs.REDSTONE);

		setLightOpacity(255);
		setLightLevel(0);
		setHardness(2.2F);
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing face, float x, float y, float z)
	{
		player.openGui(AppliedAdditions.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			AAMessageHandler.INSTANCE.sendToAll(new SyncTile(world.getTileEntity(pos)));
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileVoidifier();
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && tile instanceof TileVoidifier)
			((TileVoidifier) tile).onDestroyed();
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return createNewTileEntity(world, getMetaFromState(state));
	}
}
