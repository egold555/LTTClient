package org.golde.lttclientmeme.gui;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3ub;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.SharedDrawable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;

public class SplashProgress {

	private static SplashProgress instance;

	public static SplashProgress getInstance() {
		if(instance == null) {
			instance = new SplashProgress();
		}
		return instance;
	}


	private static Thread thread;
	private final Lock lock = new ReentrantLock(true);
	private static Drawable d;
	static final Semaphore mutex = new Semaphore(1);
	private static int backgroundColor = 0xFFFFFF;

	private static final int FPS = 60;

	Timer timer = new Timer(FPS);

	private boolean isPlaying = false;

	private static final int MAX_FRAMES = 607;


	private ResourceLocation[] frames = new ResourceLocation[MAX_FRAMES + 1];

	int currentFrame = 0;

	public void startAnimation() {

		isPlaying = true;

		for(int i = 0; i < MAX_FRAMES; i++) {
			System.out.println("Loading: " + i);
			frames[i] = new ResourceLocation("ltt/splash/frames/" + (i + 1) + ".png");
		}

		try
		{
			d = new SharedDrawable(Display.getDrawable());
			Display.getDrawable().releaseContext();
			d.makeCurrent();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}

		//Call this ASAP if splash is enabled so that threading doesn't cause issues later
		getMaxTextureSize();

		//Thread mainThread = Thread.currentThread();
		thread = new Thread(new Runnable()
		{

			public void run()
			{
				setGL();
				while(isPlaying)
				{

					nextFrame();

					// We use mutex to indicate safely to the main thread that we're taking the display global lock
					// So the main thread can skip processing messages while we're updating.
					// There are system setups where this call can pause for a while, because the GL implementation
					// is trying to impose a framerate or other thing is occurring. Without the mutex, the main
					// thread would delay waiting for the same global display lock
					mutex.acquireUninterruptibly();
					Display.update();
					// As soon as we're done, we release the mutex. The other thread can now ping the processmessages
					// call as often as it wants until we get get back here again
					mutex.release();
					Display.sync(100);
				}
				clearGL();
			}


		});
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread t, Throwable e)
			{
				e.printStackTrace();
			}
		});
		thread.start();
	}



	private static int max_texture_size = -1;
	private static int getMaxTextureSize()
	{
		if (max_texture_size != -1) return max_texture_size;
		for (int i = 0x4000; i > 0; i >>= 1)
		{
			GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
			if (GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH) != 0)
			{
				max_texture_size = i;
				return i;
			}
		}
		return -1;
	}

	private void setColor(int color)
	{
		glColor3ub((byte)((color >> 16) & 0xFF), (byte)((color >> 8) & 0xFF), (byte)(color & 0xFF));
	}

	private void setGL()
	{
		lock.lock();
		try
		{
			Display.getDrawable().makeCurrent();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		glClearColor((float)((backgroundColor >> 16) & 0xFF) / 0xFF, (float)((backgroundColor >> 8) & 0xFF) / 0xFF, (float)(backgroundColor & 0xFF) / 0xFF, 1);
		glDisable(GL_LIGHTING);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void clearGL()
	{
		Minecraft mc = Minecraft.getMinecraft();
		mc.displayWidth = Display.getWidth();
		mc.displayHeight = Display.getHeight();
		//mc.resize(mc.displayWidth, mc.displayHeight);
		glClearColor(1, 1, 1, 1);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, .1f);
		try
		{
			Display.getDrawable().releaseContext();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			lock.unlock();
		}
	}

	private void nextFrame() {

		glClear(GL_COLOR_BUFFER_BIT);
		setColor(backgroundColor);

		System.out.println("Updating frame: " + currentFrame);
		
		drawSplash(frames[currentFrame]);
		currentFrame++;

		if(currentFrame > MAX_FRAMES) {
			currentFrame = MAX_FRAMES;
			this.isPlaying = false;
		}
	}

	public boolean hangUntilIntroIsDone() {
		return isPlaying;
	}

	private void drawSplash(ResourceLocation texture) {

		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		int scaleFactor = scaledResolution.getScaleFactor();

		Framebuffer framebuffer = new Framebuffer(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor, true);
		framebuffer.bindFramebuffer(false);

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D,  (double)scaledResolution.getScaledWidth(), (double)scaledResolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepth();
		GlStateManager.enableTexture2D();

		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		GlStateManager.resetColor();
		GlStateManager.color(1.0F,  1.0F, 1.0F, 1.0F);

		Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 1920, 1080);

		framebuffer.unbindFramebuffer();
		framebuffer.framebufferRender(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor);

		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);

		Minecraft.getMinecraft().updateDisplay();

	}

	private void resetTextureState() {
		GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = -1;
	}






}
