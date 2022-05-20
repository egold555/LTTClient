package org.golde.lttclientmeme.gui.slide;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class SlidingImageLTTShirt extends SlidingImage {

	public SlidingImageLTTShirt(int shirtNum, int length) {
		super(new ResourceLocation("ltt/linus/shirt" + shirtNum + ".png"), length);
	}

	public void render() {
		double offset = 0;
		int width = 280;
		long time = getTime();

		if (time < fadedIn) {
			offset = Math.tanh(time / (double) (fadedIn) * 3.0) * width;
		} 
		else if (time > fadeOut) {
			offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * 3.0) * width);
		} 
		else {
			offset = width;
		}

		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int guiWidth = scaledresolution.getScaledWidth();
		int guiHeight = scaledresolution.getScaledHeight();

		Minecraft.getMinecraft().getTextureManager().bindTexture(rl);

		GL11.glPushMatrix();
		GL11.glColor3d(1, 1, 1);
		GL11.glScaled(0.5, 0.5, 1);
		Gui.drawModalRectWithCustomSizedTexture((int)(1090 - offset), 100, 0, 0, 800, 800, 800, 800);
		GL11.glPopMatrix();

	}

}
