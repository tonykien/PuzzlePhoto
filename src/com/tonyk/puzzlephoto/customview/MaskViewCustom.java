package com.tonyk.puzzlephoto.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MaskViewCustom extends View implements View.OnTouchListener {

	private float BORDER_SIZE = 3;
	private static final float ONSIDE_BORDER = 30;
	private float RECT_RATIO = 1f;

	private Paint paint;
	private Paint transparentPaint;
	private Bitmap bmp;
	private Canvas temp;
	private float cx, cy;
	private float preCx, preCy;
	private float maskCenterX, maskCenterY;
	boolean inside = false;
	boolean onside = false;

	private float matrixScale = 1f;

	public float DEFAULT_WIDTH;
	public float DEFAULT_HEIGHT;
	private float scale = 1f;

	public MaskViewCustom(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		transparentPaint = new Paint();
		transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
		transparentPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		transparentPaint.setAntiAlias(true);

		paint = new Paint();
		paint.setColor(Color.GREEN);

		setOnTouchListener(this);
		
		BORDER_SIZE = (int) (BORDER_SIZE * getResources().getDisplayMetrics().density);
	}

	public MaskViewCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		transparentPaint = new Paint();
		transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
		transparentPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		transparentPaint.setAntiAlias(true);

		paint = new Paint();
		paint.setColor(Color.GREEN);

		setOnTouchListener(this);
		
		BORDER_SIZE = (int) (BORDER_SIZE * getResources().getDisplayMetrics().density);
	}

	public void setBitmap(Bitmap bmp) {
		this.bmp = bmp;
		temp = new Canvas(bmp);
	}
	
	/**
	 * height / width ratio
	 * @param ratio
	 */
	public void setRectRatio(float ratio) {
		RECT_RATIO = ratio;
	}

	public float getXTop() {
		return cx - BORDER_SIZE / 2;
	}

	public float getYTop() {
		return cy - BORDER_SIZE / 2;
	}

	public float getRectWidth() {
		return DEFAULT_WIDTH * scale + BORDER_SIZE / 2;
	}
	
	public float getRectHeight() {
		return DEFAULT_HEIGHT * scale + BORDER_SIZE / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		temp.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		temp.drawColor(Color.argb(150, 0, 0, 0));
		temp.drawRect(cx - BORDER_SIZE, cy - BORDER_SIZE, cx + DEFAULT_WIDTH * scale + BORDER_SIZE,
				cy + DEFAULT_HEIGHT * scale + BORDER_SIZE, paint);
		temp.drawRect(cx, cy, cx + DEFAULT_WIDTH * scale, cy + DEFAULT_HEIGHT * scale,
				transparentPaint);

		// draw 4 corner
		temp.drawRect(cx - BORDER_SIZE * 2, cy - BORDER_SIZE * 2, cx + BORDER_SIZE, cy
				+ BORDER_SIZE, paint);
		temp.drawRect(cx + DEFAULT_WIDTH * scale - BORDER_SIZE, cy + DEFAULT_HEIGHT * scale
				- BORDER_SIZE, cx + DEFAULT_WIDTH * scale + BORDER_SIZE * 2, cy + DEFAULT_HEIGHT
				* scale + BORDER_SIZE * 2, paint);
		temp.drawRect(cx - BORDER_SIZE * 2, cy + DEFAULT_HEIGHT * scale - BORDER_SIZE, cx
				+ BORDER_SIZE, cy + DEFAULT_HEIGHT * scale + BORDER_SIZE * 2, paint);
		temp.drawRect(cx + DEFAULT_WIDTH * scale - BORDER_SIZE, cy - BORDER_SIZE * 2, cx
				+ DEFAULT_WIDTH * scale + BORDER_SIZE * 2, cy + BORDER_SIZE, paint);

		canvas.drawBitmap(bmp, 0, 0, null);

		super.onDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i("onSizeChanged", "W:" + w + " - H:" + h);
		int smallSide = getWidth();
		if (smallSide > getHeight()) {
			smallSide = getHeight();
		}

		DEFAULT_WIDTH = smallSide / ((int) RECT_RATIO + 1);
		DEFAULT_HEIGHT = smallSide / ((int) RECT_RATIO + 1) * RECT_RATIO;
		cx = getWidth() / 2 - DEFAULT_WIDTH / 2;
		cy = getHeight() / 2 - DEFAULT_HEIGHT / 2;
		maskCenterX = cx + DEFAULT_WIDTH / 2;
		maskCenterY = cy + DEFAULT_HEIGHT / 2;
		scale = 1f;
		Log.i("onSizeChanged", "w-" + w);
		bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		// bmp.eraseColor(Color.TRANSPARENT);
		temp = new Canvas(bmp);
	}

	float x = 0, y = 0;
	float xDown = 0, yDown = 0;
	double dis1, dis2;

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		preCx = cx;
		preCy = cy;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			xDown = x;
			yDown = y;
			inside = insideRect(event.getX(), event.getY());
			onside = onsideRect(event.getX(), event.getY());
			dis1 = Math.sqrt(Math.pow(x - maskCenterX, 2) + Math.pow(y - maskCenterY, 2));
			// Log.i("onTouch", "onside:" + onside + " - " + x + " - " + y);
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.i("onTouch", "move");
			if (inside) {
				cx = cx + event.getX() - x;
				cy = cy + event.getY() - y;
				checkOutOfView();
				// maskCenterX = cx + (float) mask.getWidth() * maskScale / 2;
				// maskCenterY = cy + (float) mask.getHeight() * maskScale / 2;
				maskCenterX = cx + DEFAULT_WIDTH * scale / 2;
				maskCenterY = cy + DEFAULT_HEIGHT * scale / 2;

				// Log.i("onTouch", " - " + cx + " - " + cy);
				x = event.getX();
				y = event.getY();
				invalidate();
			} else if (onside) {

				x = event.getX();
				y = event.getY();
				dis2 = Math.sqrt(Math.pow(x - maskCenterX, 2) + Math.pow(y - maskCenterY, 2));
				cx = (float) (cx - (dis2 - dis1) * Math.cos(Math.atan(RECT_RATIO)));
				cy = (float) (cy - (dis2 - dis1) * Math.cos(Math.atan(RECT_RATIO)));
				// if(isOutOfView()) break;

				matrixScale = (float) (dis2 / dis1);

				scale *= matrixScale;
				checkOutOfViewZoom();
				dis1 = dis2;
				invalidate();

			}
			break;
		case MotionEvent.ACTION_UP:
			if (inside) {

			} else if (onside) {

			}
			break;

		default:
			break;
		}
		return true;
	}

	private void checkOutOfView() {
		if (cx <= BORDER_SIZE / 2 && cx + DEFAULT_WIDTH * scale >= getWidth() - BORDER_SIZE / 2) {
			cx = BORDER_SIZE / 2;
			scale = (getWidth() - BORDER_SIZE) / DEFAULT_WIDTH;
		} else if (cx <= BORDER_SIZE / 2) {
			cx = BORDER_SIZE / 2;
		} else if (cx + DEFAULT_WIDTH * scale >= getWidth() - BORDER_SIZE / 2) {
			cx = getWidth() - DEFAULT_WIDTH * scale - BORDER_SIZE / 2;
		}

		if (cy <= BORDER_SIZE / 2 && cy + DEFAULT_HEIGHT * scale >= getHeight() - BORDER_SIZE / 2) {
			cy = BORDER_SIZE / 2;
			scale = (getHeight() - BORDER_SIZE) / DEFAULT_HEIGHT;
		} else if (cy <= BORDER_SIZE / 2) {
			cy = BORDER_SIZE / 2;
		} else if (cy + DEFAULT_HEIGHT * scale >= getHeight() - BORDER_SIZE / 2) {
			cy = getHeight() - DEFAULT_HEIGHT * scale - BORDER_SIZE / 2;
		}
	}

	private void checkOutOfViewZoom() {
		if (cx <= BORDER_SIZE / 2 && cx + DEFAULT_WIDTH * scale >= getWidth() - BORDER_SIZE / 2) {
			cx = BORDER_SIZE / 2;
			cy = preCy;
			scale = (getWidth() - BORDER_SIZE) / DEFAULT_WIDTH;
		} else if (cx <= BORDER_SIZE / 2) {
			cx = BORDER_SIZE / 2;
			// scale = scale * (matrixScale + 1) / (2 * matrixScale);
		} else if (cx + DEFAULT_WIDTH * scale >= getWidth() - BORDER_SIZE / 2) {
			cx = getWidth() - DEFAULT_WIDTH * scale - BORDER_SIZE / 2;
			// scale = scale * (matrixScale + 1) / (2 * matrixScale);
		}

		if (cy <= BORDER_SIZE / 2 && cy + DEFAULT_HEIGHT * scale >= getHeight() - BORDER_SIZE / 2) {
			cy = BORDER_SIZE / 2;
			cx = preCx;
			scale = (getHeight() - BORDER_SIZE) / DEFAULT_HEIGHT;
		} else if (cy <= BORDER_SIZE / 2) {
			cy = BORDER_SIZE / 2;
			// scale = scale * (matrixScale + 1) / (2 * matrixScale);
		} else if (cy + DEFAULT_HEIGHT * scale >= getHeight() - BORDER_SIZE / 2) {
			cy = getHeight() - DEFAULT_HEIGHT * scale - BORDER_SIZE / 2;
			// scale = scale * (matrixScale + 1) / (2 * matrixScale);
		}
	}

	private boolean insideRect(float x, float y) {
		return (x > cx + ONSIDE_BORDER) && (x < cx + DEFAULT_WIDTH * scale - ONSIDE_BORDER)
				&& (y > cy + ONSIDE_BORDER) && (y < cy + DEFAULT_HEIGHT * scale - ONSIDE_BORDER);
	}

	private boolean onsideRect(float x, float y) {
		return (Math.abs(x - cx) <= ONSIDE_BORDER)
				|| (Math.abs(x - cx - DEFAULT_WIDTH * scale) <= ONSIDE_BORDER)
				|| (Math.abs(y - cy) <= ONSIDE_BORDER)
				|| (Math.abs(y - cy - DEFAULT_HEIGHT * scale) <= ONSIDE_BORDER);
	}

}
