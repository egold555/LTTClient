package org.golde.lttclientmeme;

import java.util.List;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class CustomToast implements IToast {

	private boolean field_194168_d = false;
	
	private final String name;
	private final String desc;
	private final ItemStack item;

	public CustomToast(String name, String desc, ItemStack item)
	{
		this.name = name;
		this.desc = desc;
		this.item = item;
	}

	//draw
	public IToast.Visibility func_193653_a(GuiToast p_193653_1_, long p_193653_2_)
	{
		p_193653_1_.func_192989_b().getTextureManager().bindTexture(field_193654_a);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		p_193653_1_.drawTexturedModalRect(0, 0, 0, 0, 160, 32);

		List<String> list = p_193653_1_.func_192989_b().fontRendererObj.listFormattedStringToWidth(desc, 125);
		int i = 16776960;

		if (list.size() == 1)
		{
			p_193653_1_.func_192989_b().fontRendererObj.drawString(name, 30, 7, i | -16777216);
			p_193653_1_.func_192989_b().fontRendererObj.drawString(desc, 30, 18, -1);
		}
		else
		{
			int j = 1500;
			float f = 300.0F;

			if (p_193653_2_ < 1500L)
			{
				int k = MathHelper.floor(MathHelper.clamp((float)(1500L - p_193653_2_) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
				p_193653_1_.func_192989_b().fontRendererObj.drawString(name, 30, 11, i | k);
			}
			else
			{
				int i1 = MathHelper.floor(MathHelper.clamp((float)(p_193653_2_ - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
				int l = 16 - list.size() * p_193653_1_.func_192989_b().fontRendererObj.FONT_HEIGHT / 2;

				for (String s : list)
				{
					p_193653_1_.func_192989_b().fontRendererObj.drawString(s, 30, l, 16777215 | i1);
					l += p_193653_1_.func_192989_b().fontRendererObj.FONT_HEIGHT;
				}
			}
		}

		if (!this.field_194168_d && p_193653_2_ > 0L)
		{
			this.field_194168_d = true;
		}

		RenderHelper.enableGUIStandardItemLighting();
		p_193653_1_.func_192989_b().getRenderItem().renderItemAndEffectIntoGUI((EntityLivingBase)null, item, 8, 8);
		return p_193653_2_ >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
	}

}
