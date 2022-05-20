package org.golde.lttclientmeme.gui.slide;

import net.minecraft.util.ResourceLocation;

public abstract class SlidingImage {

	protected final ResourceLocation rl;
	protected long start;

	protected long fadedIn;
	protected long fadeOut;
	protected long end;


	public SlidingImage(ResourceLocation rl, int length) {
		this.rl = rl;
		this.fadedIn = 200 * length;
		this. fadeOut = fadedIn + 500 * length;
		this.end = fadeOut + fadedIn;
	}

	public void show() {
		start = System.currentTimeMillis();
	}

	public boolean isShown() {
		return getTime() <= end;
	}

	protected long getTime() {
		return System.currentTimeMillis() - start;
	}

	public abstract void render();

}
