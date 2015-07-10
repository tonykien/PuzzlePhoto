package com.tonyk.translatephoto.customview;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tonyk.translatephoto.BitmapObject;
import com.tonyk.translatephoto.R;
import com.tonyk.translatephoto.activity.MainActivity;

public class PhotoViewCustom extends View implements View.OnTouchListener {

	private final static String TAG = "PhotoViewCustom";
	
	private Context mContext;
	private ArrayList<BitmapObject> mListBmp = new ArrayList<BitmapObject>();
	private Paint mPaint = new Paint();
	private Paint mPaintWhite = new Paint();
	private Paint mPaintEmpty = new Paint();
	private float mEmptyX, mEmptyY;
	private int mEmptyIndex;
	private int mBmpSize;
	private int mWrongCount;
	private int mRandomNumber = 200;
	private int mRow, mColumn;
	private ArrayList<Integer> mListCanTranslateIndex = new ArrayList<Integer>();
	
	private SoundPool mSoundPool;
	private int mSoundId;
	private boolean mSoundOn = true;
	private Path mBorderPath = new Path();
	private boolean mDrawedBorderFrame = false;

	public PhotoViewCustom(Context context) {
		super(context);
		this.setOnTouchListener(this);
		mPaintWhite.setColor(Color.WHITE);
	}

	public PhotoViewCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
		mPaintWhite.setColor(Color.WHITE);
	}
	
	public void setContext(Context context) {
		mContext = context;
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundId = mSoundPool.load(mContext, R.raw.click, 1);
		mPaintEmpty.setColor(mContext.getResources().getColor(R.color.app_color));
		mPaintEmpty.setStrokeWidth(3);
		mPaintEmpty.setStyle(Paint.Style.STROKE);
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
	
	public void enableSound() {
		mSoundOn = true;
	}
	
	public void disableSound() {
		mSoundOn = false;
	}
	
	public void setRandomNumber(int randNum) {
		mRandomNumber = randNum;
	}
	
	private void drawBorderFrame(Canvas canvas) {
		Log.i("drawBorderFrame", "drawBorderFrame");
		mBorderPath.moveTo(mEmptyX - MainActivity.BORDER_SIZE, mEmptyY - MainActivity.BORDER_SIZE);
		mBorderPath.lineTo(mEmptyX + MainActivity.BORDER_SIZE + mBmpSize, mEmptyY - MainActivity.BORDER_SIZE);
		mBorderPath.lineTo(mEmptyX + MainActivity.BORDER_SIZE + mBmpSize, mEmptyY - MainActivity.BORDER_SIZE + mBmpSize);
		mBorderPath.lineTo(mEmptyX + MainActivity.BORDER_SIZE + mBmpSize * mColumn, mEmptyY - MainActivity.BORDER_SIZE + mBmpSize);
		mBorderPath.lineTo(mEmptyX + MainActivity.BORDER_SIZE + mBmpSize * mColumn, mEmptyY + MainActivity.BORDER_SIZE + mBmpSize * (mRow + 1));
		mBorderPath.lineTo(mEmptyX - MainActivity.BORDER_SIZE, mEmptyY + MainActivity.BORDER_SIZE + mBmpSize * (mRow + 1));
		mBorderPath.lineTo(mEmptyX - MainActivity.BORDER_SIZE, mEmptyY - MainActivity.BORDER_SIZE);
		
		mPaintWhite.setStrokeWidth(MainActivity.BORDER_SIZE);
		mPaintWhite.setStyle(Paint.Style.STROKE);
		canvas.drawPath(mBorderPath, mPaintWhite);
		// mPaintWhite.setStyle(Style.FILL);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw border frame
//		if (!mDrawedBorderFrame) {
//			drawBorderFrame(canvas);
//			mDrawedBorderFrame = true;
//		}
		for (BitmapObject bObj : mListBmp) {
			canvas.drawBitmap(bObj.getBmp(), bObj.getX(), bObj.getY(), mPaint);
		}
		canvas.drawRect(mEmptyX, mEmptyY, mEmptyX + mBmpSize, mEmptyY + mBmpSize, mPaintWhite);
		canvas.drawRect(mEmptyX + MainActivity.BORDER_SIZE * 2, mEmptyY + MainActivity.BORDER_SIZE * 2,
				mEmptyX + mBmpSize - MainActivity.BORDER_SIZE * 2, mEmptyY + mBmpSize - MainActivity.BORDER_SIZE * 2, mPaintEmpty);
		// mEmptyPath.reset();
		// mEmptyPath.moveTo(mEmptyX + MainActivity.BORDER_SIZE * 2, mEmptyY +
		// MainActivity.BORDER_SIZE * 2);
		// mEmptyPath.lineTo(mEmptyX + mBmpSize - MainActivity.BORDER_SIZE * 2,
		// mEmptyY + MainActivity.BORDER_SIZE * 2);
		// mEmptyPath.lineTo(mEmptyX + mBmpSize - MainActivity.BORDER_SIZE * 2,
		// mEmptyY + mBmpSize - MainActivity.BORDER_SIZE * 2);
		// mEmptyPath.lineTo(mEmptyX + MainActivity.BORDER_SIZE * 2, mEmptyY +
		// mBmpSize - MainActivity.BORDER_SIZE * 2);
		// mEmptyPath.lineTo(mEmptyX + MainActivity.BORDER_SIZE * 2, mEmptyY +
		// MainActivity.BORDER_SIZE * 2);
		// canvas.drawPath(mEmptyPath, mPaintEmpty);
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
					// playSoundEffect(SoundEffectConstants.CLICK);
					if (mSoundOn) {
						mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
					}
					
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
						((MainActivity) mContext).disableStartBtn();
						// Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
						((MainActivity) mContext).puzzleDone();
					} else {
						((MainActivity) mContext).enableStartBtn();
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
		for (int i = 0; i < mRandomNumber; i++) {
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
