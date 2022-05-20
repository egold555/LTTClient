package org.golde.lttclientmeme.gui.slide;

import java.util.concurrent.LinkedBlockingQueue;

public class SlidingImageManager {

	private static LinkedBlockingQueue<SlidingImage> pendingSlidingImages = new LinkedBlockingQueue<>();
	private static SlidingImage currentSlidingImage = null;

	public static void show(SlidingImage SlidingImage) {
		pendingSlidingImages.add(SlidingImage);
	}

	public static void update() {
		if (currentSlidingImage != null && !currentSlidingImage.isShown()) {
			currentSlidingImage = null;
		}

		if (currentSlidingImage == null && !pendingSlidingImages.isEmpty()) {
			currentSlidingImage = pendingSlidingImages.poll();
			currentSlidingImage.show();
		}

	}

	public static void render() {
		update();

		if (currentSlidingImage != null) {
			currentSlidingImage.render();
		}

	}
}
