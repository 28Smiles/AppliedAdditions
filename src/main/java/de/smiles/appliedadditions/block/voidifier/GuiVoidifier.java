package de.smiles.appliedadditions.block.voidifier;

import java.io.IOException;

import de.smiles.appliedadditions.AppliedAdditions;
import de.smiles.appliedadditions.network.AAMessageHandler;
import de.smiles.appliedadditions.network.FieldUpdatePacket;
import de.smiles.appliedadditions.network.FlushPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiVoidifier extends GuiContainer
{
	private static ResourceLocation basic_bg = new ResourceLocation(AppliedAdditions.ID, "textures/gui/voidifier.png");
	public GuiTextField[] textfield;
	private ContainerVoidifier container;

	public GuiVoidifier(ContainerVoidifier container)
	{
		super(container);
		this.container = container;
	}

	public void update()
	{
		for(int i = 0; i < textfield.length; i++)
			textfield[i].setText("" + (container.tile.limits[i] >= 0 ? container.tile.limits[i] : ""));
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int cx = (this.width - this.xSize) / 2;
	    int cy = (this.height - this.ySize) / 2;

	    textfield = new GuiTextField[6];

	    textfield[0] = new GuiTextField(0, fontRenderer, 34 + cx, 9 + cy, 50, 14);
	    textfield[1] = new GuiTextField(1, fontRenderer, 34 + cx, 31 + cy, 50, 14);
	    textfield[2] = new GuiTextField(2, fontRenderer, 34 + cx, 53 + cy, 50, 14);
	    textfield[3] = new GuiTextField(3, fontRenderer, 111 + cx, 9 + cy, 50, 14);
	    textfield[4] = new GuiTextField(4, fontRenderer, 111 + cx, 31 + cy, 50, 14);
	    textfield[5] = new GuiTextField(5, fontRenderer, 111 + cx, 53 + cy, 50, 14);

		for(int i = 0; i < textfield.length; i++)
		{
			textfield[i].setMaxStringLength(8);
			textfield[i].setText("" + (container.tile.limits[i] >= 0 ? container.tile.limits[i] : ""));
			textfield[i].setFocused(false);
			textfield[i].setVisible(true);
			textfield[i].setTextColor(16777215);
		}
	}

	@Override
	protected void keyTyped(char c, int j) throws IOException
	{
		super.keyTyped(c, j);
		for(int i = 0; i < textfield.length; i++)
			textfield[i].textboxKeyTyped(c, j);
		if(c == 'O')
			AAMessageHandler.INSTANCE.sendToServer(new FlushPacket(container.windowId));
	}

	@Override
	protected void mouseClicked(int mx, int my, int mid) throws IOException
	{
		super.mouseClicked(mx, my, mid);
		int unf = -1, unf2 = -1;
		for(int i = 0; i < textfield.length; i++)
			if(textfield[i].isFocused())
			{
				textfield[i].setFocused(false);
				unf = i;
			}
		for(int i = 0; i < textfield.length; i++)
			if(textfield[i].x <= mx && textfield[i].y <= my && textfield[i].x + textfield[i].width >= mx && textfield[i].y + textfield[i].height >= my)
			{
				textfield[i].setFocused(true);
				unf2 = i;
			}

		if(unf != -1 && unf != unf2)
		{
			String text = textfield[unf].getText();
			try {
				int parsed = Integer.parseInt(text);
				AAMessageHandler.INSTANCE.sendToServer(new FieldUpdatePacket(container.windowId, unf, parsed));
			} catch(NumberFormatException e)
			{
				AAMessageHandler.INSTANCE.sendToServer(new FieldUpdatePacket(container.windowId, unf, -1));
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float seed, int mx, int my)
	{
		this.mc.getTextureManager().bindTexture(basic_bg);
		GlStateManager.color(1, 1, 1, 1);
		int cx = (this.width - this.xSize) / 2;
	    int cy = (this.height - this.ySize) / 2;
	    drawTexturedModalRect(cx, cy, 0, 0, this.xSize, this.ySize);

	    for(int i = 0; i < textfield.length; i++)
	    	textfield[i].drawTextBox();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mx, int my)
	{
		this.fontRenderer.drawString("Inventory" , 8, this.ySize - 96 + 3, 4210752 ); //TODO
	}
}
