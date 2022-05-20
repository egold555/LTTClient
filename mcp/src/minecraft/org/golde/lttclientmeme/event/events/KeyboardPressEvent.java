package org.golde.lttclientmeme.event.events;

import org.golde.lttclientmeme.event.EventCancelable;

public class KeyboardPressEvent extends EventCancelable {

	private final int key;
	
	public KeyboardPressEvent(int key) {
		this.key = key;
	}
	
	public int getKey() {
		return key;
	}
	
}
