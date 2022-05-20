package org.golde.lttclientmeme;

import java.beans.EventHandler;
import java.util.LinkedList;
import java.util.Queue;

import org.golde.lttclientmeme.event.EventManager;
import org.golde.lttclientmeme.event.EventTarget;
import org.golde.lttclientmeme.event.events.ClientTickEvent;
import org.golde.lttclientmeme.event.events.KeyboardPressEvent;
import org.golde.lttclientmeme.event.events.RenderEvent;
import org.golde.lttclientmeme.gui.click.GuiCircleMenu;
import org.golde.lttclientmeme.gui.slide.SlidingImage;
import org.golde.lttclientmeme.gui.slide.SlidingImageLTTShirt;
import org.golde.lttclientmeme.gui.slide.SlidingImageManager;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class LTTClient {

	private static LTTClient instance;
	private DiscordRP discord = new DiscordRP();
	
	public static LTTClient getInstance() {
		if(instance == null) {
			instance = new LTTClient();
		}
		return instance;
	}

	public void init() {
		EventManager.register(this);
		
	}

	public void start() {
		discord.start();
	}

	public void shutdown() {
		discord.shutdown();
	}
	
	@EventTarget
	public void onTick(ClientTickEvent e) {
		SlidingImageManager.update();
	}
	
	@EventTarget
	public void onRender(RenderEvent e) {
		SlidingImageManager.render();
		
		synchronized (OPENGL_LOCK) {
			if(openGlQueue.peek() != null) {
				openGlQueue.poll().run();
			}
		}
	}
	
	@EventTarget
	public void onKeyPress(KeyboardPressEvent e) {
		if(e.getKey() == Keyboard.KEY_P) {
			SlidingImageManager.show(new SlidingImageLTTShirt(1, 5));
		}
		else if(e.getKey() == Keyboard.KEY_T) {
			Minecraft.getMinecraft().func_193033_an().func_192988_a(new CustomToast("Custom Toast", "I am a custom toast!", new ItemStack(Blocks.BEDROCK)));
		}
		else if(e.getKey() == Keyboard.KEY_I) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiCircleMenu());
		}
	}

	public String getVersion() {
		return "1.0";
	}
	
	private Object OPENGL_LOCK = new Object();
	private Queue<Runnable> openGlQueue = new LinkedList<Runnable>();
	public void queueInOpenGLThread(Runnable runnable) {
		synchronized (OPENGL_LOCK) {
			openGlQueue.add(runnable);
		}
	}
}
