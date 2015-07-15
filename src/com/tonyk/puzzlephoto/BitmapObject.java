package com.tonyk.puzzlephoto;

import android.graphics.Bitmap;
import android.graphics.Point;

public class BitmapObject {
	
	private Bitmap bmp;
	private Point position;
	private float x;
	private float y;
	private boolean canTranslate = false;
	private int currentIndex;
	private int oriIndex;
	
	public BitmapObject(Bitmap bmp, float x, float y) {
		this.bmp = bmp;
		this.x = x;
		this.y = y;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public boolean isCanTranslate() {
		return canTranslate;
	}

	public void setCanTranslate(boolean canTranslate) {
		this.canTranslate = canTranslate;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int index) {
		this.currentIndex = index;
	}

	public int getOriIndex() {
		return oriIndex;
	}

	public void setOriIndex(int oriIndex) {
		this.oriIndex = oriIndex;
	}
	
}
