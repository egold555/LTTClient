package org.golde.lttclientmeme.gui.click;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Stopwatch;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiCircleMenu extends GuiScreen
{

	private final float TIME_SCALE = 0.01f;
	//public static final ChiselsAndBitsMenu instance = new ChiselsAndBitsMenu();

	private float visibility = 0.0f;
	private Stopwatch lastChange = Stopwatch.createStarted();
	//public ChiselMode switchTo = null;
	//public ButtonAction doAction = null;
	public boolean actionUsed = false;

	private float clampVis(
			final float f )
	{
		return Math.max( 0.0f, Math.min( 1.0f, f ) );
	}

	public void raiseVisibility()
	{
		visibility = clampVis( visibility + lastChange.elapsed( TimeUnit.MILLISECONDS ) * TIME_SCALE );
		lastChange = Stopwatch.createStarted();
	}

	public void decreaseVisibility()
	{
		visibility = clampVis( visibility - lastChange.elapsed( TimeUnit.MILLISECONDS ) * TIME_SCALE );
		lastChange = Stopwatch.createStarted();
	}

	public boolean isVisible()
	{
		return visibility > 0.001;
	}

	public void configure(
			final int scaledWidth,
			final int scaledHeight )
	{
		mc = Minecraft.getMinecraft();
		fontRendererObj = mc.fontRendererObj;
		width = scaledWidth;
		height = scaledHeight;
	}

	private static class MenuButton
	{

		public double x1, x2;
		public double y1, y2;
		public boolean highlighted;

		//public final ButtonAction action;
		public TextureAtlasSprite icon;
		public String name;

		public MenuButton(
				final String name,
				//final ButtonAction action,
				final double x,
				final double y,
				final TextureAtlasSprite ico )
		{
			this.name = name;
			//this.action = action;
			x1 = x;
			x2 = x + 18;
			y1 = y;
			y2 = y + 18;
			icon = ico;
		}

	};

	static class MenuRegion
	{

		public final String mode;
		public double x1, x2;
		public double y1, y2;
		public boolean highlighted;

		public MenuRegion(
				final String mode )
		{
			this.mode = mode;
		}

	};

	@Override
	public void drawScreen(
			final int mouseX,
			final int mouseY,
			final float partialTicks )
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate( 0.0F, 0.0F, 200.0F );

		final int start = (int) ( visibility * 98 ) << 24;
		final int end = (int) ( visibility * 128 ) << 24;

		drawGradientRect( 0, 0, width, height, start, end );

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0 );
		GlStateManager.shadeModel( GL11.GL_SMOOTH );
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder renderBuffer = tessellator.getBuffer();

		renderBuffer.begin( GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR );

		final double vecX = mouseX - width / 2;
		final double vecY = mouseY - height / 2;
		double radians = Math.atan2( vecY, vecX );
		final double length = Math.sqrt( vecX * vecX + vecY * vecY );

		final double ring_inner_edge = 20;
		final double ring_outer_edge = 50;
		final double text_distnace = 65;
		final double quarterCircle = Math.PI / 2.0;

		if ( radians < -quarterCircle )
		{
			radians = radians + Math.PI * 2;
		}

		final double middle_x = width / 2;
		final double middle_y = height / 2;

		final ArrayList<MenuRegion> modes = new ArrayList<MenuRegion>();
		final ArrayList<MenuButton> btns = new ArrayList<MenuButton>();

		//btns.add( new MenuButton( "mod.chiselsandbits.other.undo", ButtonAction.UNDO, text_distnace, -20, ClientSide.undoIcon ) );
		//btns.add( new MenuButton( "mod.chiselsandbits.other.redo", ButtonAction.REDO, text_distnace, 4, ClientSide.redoIcon ) );

//		for ( final EnumTestMode mode : EnumTestMode.values() )
//		{
//			modes.add( new MenuRegion( mode ) );
//		}
		
		for(int i = 0; i < 8; i++) {
			modes.add(new MenuRegion("#" + i));
		}

		//switchTo = null;
		//doAction = null;

		if ( !modes.isEmpty() )
		{
			final int totalModes = modes.size();
			int currentMode = 0;
			final double fragment = Math.PI * 0.005;
			final double fragment2 = Math.PI * 0.0025;
			final double perObject = 2.0 * Math.PI / totalModes;

			for ( final MenuRegion mnuRgn : modes )
			{
				final double begin_rad = currentMode * perObject - quarterCircle;
				final double end_rad = ( currentMode + 1 ) * perObject - quarterCircle;

				mnuRgn.x1 = Math.cos( begin_rad );
				mnuRgn.x2 = Math.cos( end_rad );
				mnuRgn.y1 = Math.sin( begin_rad );
				mnuRgn.y2 = Math.sin( end_rad );

				final double x1m1 = Math.cos( begin_rad + fragment ) * ring_inner_edge;
				final double x2m1 = Math.cos( end_rad - fragment ) * ring_inner_edge;
				final double y1m1 = Math.sin( begin_rad + fragment ) * ring_inner_edge;
				final double y2m1 = Math.sin( end_rad - fragment ) * ring_inner_edge;

				final double x1m2 = Math.cos( begin_rad + fragment2 ) * ring_outer_edge;
				final double x2m2 = Math.cos( end_rad - fragment2 ) * ring_outer_edge;
				final double y1m2 = Math.sin( begin_rad + fragment2 ) * ring_outer_edge;
				final double y2m2 = Math.sin( end_rad - fragment2 ) * ring_outer_edge;

				final float a = 0.5f;
				float f = 0f;

				if ( begin_rad <= radians && radians <= end_rad && ring_inner_edge < length && length <= ring_outer_edge )
				{
					f = 1;
					mnuRgn.highlighted = true;
					//switchTo = mnuRgn.mode;
				}

				renderBuffer.pos( middle_x + x1m1, middle_y + y1m1, zLevel ).color( f, f, f, a ).endVertex();
				renderBuffer.pos( middle_x + x2m1, middle_y + y2m1, zLevel ).color( f, f, f, a ).endVertex();
				renderBuffer.pos( middle_x + x2m2, middle_y + y2m2, zLevel ).color( f, f, f, a ).endVertex();
				renderBuffer.pos( middle_x + x1m2, middle_y + y1m2, zLevel ).color( f, f, f, a ).endVertex();

				currentMode++;
			}
		}

		for ( final MenuButton btn : btns )
		{
			final float a = 0.5f;
			float f = 0f;

			if ( btn.x1 <= vecX && btn.x2 >= vecX && btn.y1 <= vecY && btn.y2 >= vecY )
			{
				f = 1;
				btn.highlighted = true;
				//doAction = btn.action;
			}

			renderBuffer.pos( middle_x + btn.x1, middle_y + btn.y1, zLevel ).color( f, f, f, a ).endVertex();
			renderBuffer.pos( middle_x + btn.x1, middle_y + btn.y2, zLevel ).color( f, f, f, a ).endVertex();
			renderBuffer.pos( middle_x + btn.x2, middle_y + btn.y2, zLevel ).color( f, f, f, a ).endVertex();
			renderBuffer.pos( middle_x + btn.x2, middle_y + btn.y1, zLevel ).color( f, f, f, a ).endVertex();
		}

		tessellator.draw();

		GlStateManager.shadeModel( GL11.GL_FLAT );

		GlStateManager.translate( 0.0F, 0.0F, 5.0F );
		GlStateManager.enableTexture2D();
		GlStateManager.color( 1, 1, 1, 1.0f );
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.bindTexture( Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId() );

		renderBuffer.begin( GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR );

//		for ( final MenuRegion mnuRgn : modes )
//		{
//			final double x = ( mnuRgn.x1 + mnuRgn.x2 ) * 0.5 * ( ring_outer_edge * 0.6 + 0.4 * ring_inner_edge );
//			final double y = ( mnuRgn.y1 + mnuRgn.y2 ) * 0.5 * ( ring_outer_edge * 0.6 + 0.4 * ring_inner_edge );
//
//			final SpriteIconPositioning sip = ClientSide.instance.getIconForMode( mnuRgn.mode );
//
//			final double scalex = 15 * sip.width * 0.5;
//			final double scaley = 15 * sip.height * 0.5;
//			final double x1 = x - scalex;
//			final double x2 = x + scalex;
//			final double y1 = y - scaley;
//			final double y2 = y + scaley;
//
//			final TextureAtlasSprite sprite = sip.sprite;
//
//			final float f = 1.0f;
//			final float a = 1.0f;
//
//			final double u1 = sip.left * 16.0;
//			final double u2 = ( sip.left + sip.width ) * 16.0;
//			final double v1 = sip.top * 16.0;
//			final double v2 = ( sip.top + sip.height ) * 16.0;
//
//			renderBuffer.pos( middle_x + x1, middle_y + y1, zLevel ).tex( sprite.getInterpolatedU( u1 ), sprite.getInterpolatedV( v1 ) ).color( f, f, f, a ).endVertex();
//			renderBuffer.pos( middle_x + x1, middle_y + y2, zLevel ).tex( sprite.getInterpolatedU( u1 ), sprite.getInterpolatedV( v2 ) ).color( f, f, f, a ).endVertex();
//			renderBuffer.pos( middle_x + x2, middle_y + y2, zLevel ).tex( sprite.getInterpolatedU( u2 ), sprite.getInterpolatedV( v2 ) ).color( f, f, f, a ).endVertex();
//			renderBuffer.pos( middle_x + x2, middle_y + y1, zLevel ).tex( sprite.getInterpolatedU( u2 ), sprite.getInterpolatedV( v1 ) ).color( f, f, f, a ).endVertex();
//		}

		for ( final MenuButton btn : btns )
		{
			final float f = 0.5f; //1.0F
			final float a = 1.0f;

			final double u1 = 0;
			final double u2 = 16;
			final double v1 = 0;
			final double v2 = 16;

			final TextureAtlasSprite sprite = btn.icon;

			final double btnx1 = btn.x1 + 1;
			final double btnx2 = btn.x2 - 1;
			final double btny1 = btn.y1 + 1;
			final double btny2 = btn.y2 - 1;

			renderBuffer.pos( middle_x + btnx1, middle_y + btny1, zLevel ).tex( sprite.getInterpolatedU( u1 ), sprite.getInterpolatedV( v1 ) ).color( f, f, f, a ).endVertex();
			renderBuffer.pos( middle_x + btnx1, middle_y + btny2, zLevel ).tex( sprite.getInterpolatedU( u1 ), sprite.getInterpolatedV( v2 ) ).color( f, f, f, a ).endVertex();
			renderBuffer.pos( middle_x + btnx2, middle_y + btny2, zLevel ).tex( sprite.getInterpolatedU( u2 ), sprite.getInterpolatedV( v2 ) ).color( f, f, f, a ).endVertex();
			renderBuffer.pos( middle_x + btnx2, middle_y + btny1, zLevel ).tex( sprite.getInterpolatedU( u2 ), sprite.getInterpolatedV( v1 ) ).color( f, f, f, a ).endVertex();
		}

		tessellator.draw();

		for ( final MenuRegion mnuRgn : modes )
		{
			if ( mnuRgn.highlighted )
			{
				final double x = ( mnuRgn.x1 + mnuRgn.x2 ) * 0.5;
				final double y = ( mnuRgn.y1 + mnuRgn.y2 ) * 0.5;

				int fixed_x = (int) ( x * text_distnace );
				final int fixed_y = (int) ( y * text_distnace );
				final String text = mnuRgn.mode;

				if ( x <= -0.2 )
				{
					fixed_x -= fontRendererObj.getStringWidth( text );
				}
				else if ( -0.2 <= x && x <= 0.2 )
				{
					fixed_x -= fontRendererObj.getStringWidth( text ) / 2;
				}

				fontRendererObj.drawStringWithShadow( text, (int) middle_x + fixed_x, (int) middle_y + fixed_y, 0xffffffff );
			}
		}

		for ( final MenuButton btn : btns )
		{
			if ( btn.highlighted )
			{
				fontRendererObj.drawString(btn.name, (int) ( middle_x + btn.x2 + 8 ), (int) ( middle_y + btn.y1 + 6 ), 0xffffffff );
			}
		}

		GlStateManager.popMatrix();
	}

}