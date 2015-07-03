package com.tonyk.translatephoto.customview;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Toast;

import com.tonyk.translatephoto.BitmapObject;
import com.tonyk.translatephoto.activity.MainActivity;

public class PhotoViewCustom extends View implements View.OnTouchListener {

	private final static String TAG = "PhotoViewCustom";
	
	private Context mContext;
	private ArrayList<BitmapObject> mListBmp = new ArrayList<BitmapObject>();
	private Paint mPaint = new Paint();
	private Paint mPaintWhite = new Paint();
	private float mEmptyX, mEmptyY;
	private int mEmptyIndex;
	private int mBmpSize;
	private int mWrongCount;
	private int mRow, mColumn;
	private ArrayList<Integer> mListCanTranslateIndex = new ArrayList<Integer>();
	
	private MediaPlayer mp;

	public PhotoViewCustom(Context context) {
		super(context);
		this.setOnTouchListener(this);
		mPaintWhite.setColor(Color.WHITE);
		
		// mp = MediaPlayer.create(this, R.raw.soho);
	}

	public PhotoViewCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
		mPaintWhite.setColor(Color.WHITE);
		
		// mp = MediaPlayer.create(this, R.raw.soho);
	}
	
	public void setContext(Context context) {
		mContext = context;
	}

	public void setBmpSize(int bmpSize) {
		mBmpSize = bmpSize;
	}

	public void addBmpObj(BitmapObject bObj) {
		mListBmp.add(bObj);
	}
	
	public void setRow(int row) {
		mRow = row;
	}
	
	public int getRow() {
		return mRow;
	}
	
	public void setColumn(int column) {
		mColumn = column;
	}
	
	public int getColumn() {
		return mColumn;
	}

//	public void setBitmap(Bitmap b, float left, float top) {
//		mBmp = b;
//		mLeft = left;
//		mTop = top;
//	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (BitmapObject bObj : mListBmp) {
			canvas.drawBitmap(bObj.getBmp(), bObj.getX(), bObj.getY(), mPaint);
		}
		canvas.drawRect(mEmptyX, mEmptyY, mEmptyX + mBmpSize, mEmptyY + mBmpSize, mPaintWhite);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, "onTouch");
		// get index of touch
		int index = -1;
		float x = event.getX();
		float y = event.getY();
		
		if (x > mColumn * mBmpSize || y > (mRow + 1) * mBmpSize) {
			return false;
		}

		if ((int) (y / mBmpSize) == 0) {
			if ((int) (x / mBmpSize) == 0) {
				index = 0;
			}
		} else {
			index = ((int) (y / mBmpSize) - 1) * mColumn + (int) (x / mBmpSize) + 1;
		}

		if (index == mEmptyIndex || index < 0 || index > mRow * mColumn) {
			return false;
		}

		// get bitmapObject
		BitmapObject touchedObj = null;
		for (BitmapObject bObj : mListBmp) {
			if (index == bObj.getCurrentIndex()) {
				touchedObj = bObj;
				// translate
				if (touchedObj.isCanTranslate()) {
					// play sound
					playSoundEffect(SoundEffectConstants.CLICK);
					
					float tmpX = touchedObj.getX();
					float tmpY = touchedObj.getY();
					touchedObj.setX(mEmptyX);
					touchedObj.setY(mEmptyY);
					mEmptyX = tmpX;
					mEmptyY = tmpY;

					touchedObj.setCurrentIndex(mEmptyIndex);
					mEmptyIndex = index;
					
					// check correct position
					if (touchedObj.getCurrentIndex() == touchedObj.getOriIndex()) {
						mWrongCount--;
					} else if (index == touchedObj.getOriIndex()){
						mWrongCount++;
					}
					Log.i(TAG, "wrong count:" + mWrongCount);
					updateCanTranslateObj(mEmptyIndex);
					
					invalidate();
					
					if (mWrongCount == 0) {
						Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
						((MainActivity) mContext).puzzleDone();
					}
				}
				break;
			}
		}

		return false;
	}

	public void updateCanTranslateObj(int emptyIndex) {
		mListCanTranslateIndex.clear();

		if (emptyIndex == 0) {
			for (BitmapObject bObj : mListBmp) {
				if (bObj.getCurrentIndex() == 1) {
					bObj.setCanTranslate(true);
				} else {
					bObj.setCanTranslate(false);
				}
			}
		} else if ((emptyIndex % mColumn) == 1) {
			if (emptyIndex == 1) {
				for (BitmapObject bObj : mListBmp) {
					int index = bObj.getCurrentIndex();
					if (index == 0 || index == emptyIndex + 1
							|| index == emptyIndex + mColumn) {
						bObj.setCanTranslate(true);
						if (index != 0) {
							mListCanTranslateIndex.add(Integer.valueOf(index));
						}
					} else {
						bObj.setCanTranslate(false);
					}
				}
			} else {
				for (BitmapObject bObj : mListBmp) {
					int index = bObj.getCurrentIndex();
					if (index == emptyIndex + 1 || index == emptyIndex + mColumn
							|| index == emptyIndex - mColumn) {
						bObj.setCanTranslate(true);
						mListCanTranslateIndex.add(Integer.valueOf(index));
					} else {
						bObj.setCanTranslate(false);
					}
				}
			}
		} else if ((emptyIndex % mColumn) == 0) {
			for (BitmapObject bObj : mListBmp) {
				int index = bObj.getCurrentIndex();
				if (index == emptyIndex - 1 || index == emptyIndex + mColumn
						|| (index == emptyIndex - mColumn && index != 0)) {
					bObj.setCanTranslate(true);
					mListCanTranslateIndex.add(Integer.valueOf(index));
				} else {
					bObj.setCanTranslate(false);
				}
			}
		} else {
			for (BitmapObject bObj : mListBmp) {
				int index = bObj.getCurrentIndex();
				if (index == emptyIndex - 1 || index == emptyIndex + 1
						|| index == emptyIndex + mColumn
						|| index == emptyIndex - mColumn) {
					bObj.setCanTranslate(true);
					mListCanTranslateIndex.add(Integer.valueOf(index));
				} else {
					bObj.setCanTranslate(false);
				}
			}
		}
	}

	public void random() {
		/** touch index 0 */
		// Obtain MotionEvent object
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis() + 100;
		float x = 0.0f;
		float y = 0.0f + mBmpSize;
		// List of meta states found here:
		// developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
		int metaState = 0;
		MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x,
				y, metaState);
		// Dispatch touch event to view
		this.dispatchTouchEvent(motionEvent);
		
		/** touch index random # 0 */
		for (int i = 0; i < 500; i++) {
			int randomIdx = mListCanTranslateIndex.get(new Random().nextInt(mListCanTranslateIndex
					.size()));
			
			downTime = SystemClock.uptimeMillis();
			eventTime = SystemClock.uptimeMillis() + 100;
			if ((randomIdx % mColumn) == 0) {
				x = (mColumn - 1) * mBmpSize;
			} else {
				x = ((randomIdx % mColumn) - 1) * mBmpSize;
			}
			y = (((randomIdx - 1) / mColumn) + 1) * mBmpSize;
			metaState = 0;
			motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x,
					y, metaState);
			this.dispatchTouchEvent(motionEvent);
		}
		
		/** make empty index = 1 */
		while(mEmptyIndex != 1) {
			int touchIdx = mEmptyIndex - mColumn;
			if (touchIdx <= 0) {
				touchIdx = mEmptyIndex - 1;
			}
			
			downTime = SystemClock.uptimeMillis();
			eventTime = SystemClock.uptimeMillis() + 100;
			if ((touchIdx % mColumn) == 0) {
				x = (mColumn - 1) * mBmpSize;
			} else {
				x = ((touchIdx % mColumn) - 1) * mBmpSize;
			}
			y = (((touchIdx - 1) / mColumn) + 1) * mBmpSize;
			metaState = 0;
			motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x,
					y, metaState);
			this.dispatchTouchEvent(motionEvent);
		}
		
		/** make empty index = 0 */
		downTime = SystemClock.uptimeMillis();
		eventTime = SystemClock.uptimeMillis() + 100;
		x = 0;
		y = 0;
		metaState = 0;
		motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x,
				y, metaState);
		this.dispatchTouchEvent(motionEvent);
	}
	
	public void reset() {
		for (BitmapObject bObj : mListBmp) {
			int oriIndex = bObj.getOriIndex();
			bObj.setCurrentIndex(oriIndex);
			bObj.setX(((oriIndex - 1) % mColumn) * mBmpSize);
			bObj.setY(((oriIndex - 1) / mColumn + 1) * mBmpSize);
			if (oriIndex == 1) {
				bObj.setCanTranslate(true);
			} else {
				bObj.setCanTranslate(false);
			}
		}
		
		mEmptyIndex = 0;
		mEmptyX = 0;
		mEmptyY = 0;
		mWrongCount = 0;
		
		invalidate();
	}
}
