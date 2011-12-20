package org.uit.thesis.View;

import android.graphics.drawable.Drawable;

public class ActionItem {
	private Drawable icon;
	private String title;

    public ActionItem(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
    }
 	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public Drawable getIcon() {
		return this.icon;
	}
}