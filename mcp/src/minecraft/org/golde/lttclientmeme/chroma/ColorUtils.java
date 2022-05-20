package org.golde.lttclientmeme.chroma;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class ColorUtils {

	private ColorUtils() {}
	
	public static int getChromaColor() {
		return getChromaColor(0);
	}
	
	public static int getChromaColor(long l) {
		long time = System.currentTimeMillis() + l;
        return Color.HSBtoRGB(time % (int) 2000.0F / 2000.0F, 0.8F, 0.8F);
	}
	
	public static void drawChromaString(String string, int x, int y) {
		drawChromaString(string, x, y, false);
	}
	
	//Draws every character a different color
	public static void drawChromaString(String string, int x, int y, boolean shadow) {
		
		string = TextFormatting.getTextWithoutFormattingCodes(string);
		
        Minecraft mc = Minecraft.getMinecraft();

        int tmpXPos = x;
        for (char textChar : string.toCharArray()) {
            long time = System.currentTimeMillis() - (tmpXPos * 10 - y * 10);
            int hsv = Color.HSBtoRGB(time % (int) 2000.0F / 2000.0F, 0.8F, 0.8F);
            String charStr = String.valueOf(textChar);
            mc.fontRendererObj.drawString(charStr, tmpXPos, y, hsv, shadow);
            tmpXPos += mc.fontRendererObj.getCharWidth(textChar);
        }
    }
	
	public static float[] intToRGBAF(int color) {
		float red = (float)(color >> 16 & 255) / 255.0F;
		float blue = (float)(color >> 8 & 255) / 255.0F;
		float green = (float)(color & 255) / 255.0F;
		float alpha = (float)(color >> 24 & 255) / 255.0F;
		
		return new float[] {red, green, blue, alpha};
	}
	
	public static int[] intToRGBAI(int color) {
		int red = color >> 16 & 255;
        int blue = color >> 8 & 255;
        int green = color & 255;
        int alpha = color >> 24 & 255;
        
        return new int[] {red, green, blue, alpha};
	}
	
}
