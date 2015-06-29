package com.tonyk.translatephoto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	private final static int BORDER_SIZE = 2;
	public final static int TYPE_EASY = 1;
	public final static int TYPE_MEDIUM = 2;
	public final static int TYPE_HARD = 3;

	public final static String PREF_NAME_SCORE = "PrefScore";
	public final static String KEY_BEST_TIME = "key_best_time";
	public final static String KEY_RANK_TIME = "key_rank_time";

	private Matrix mMatrix;
	private PhotoViewCustom mPhotoview;
	private int mRow, mColumn;

	private Timer mTimer;
	private MyTimerTask mMyTimerTask;
	private TextView mTvTimeCounter;
	private long mTimeStart;

	private int mLevel;
	private int mDrawableId;

	private Button mBtnStart, mBtnRandom, mBtnReset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		mPhotoview = (PhotoViewCustom) findViewById(R.id.photoview);
		mPhotoview.setContext(this);
		mTvTimeCounter = (TextView) findViewById(R.id.tvTimeCounter);
		ImageView ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
		RelativeLayout rlTop = (RelativeLayout) findViewById(R.id.rlTop);
		mBtnRandom = (Button) findViewById(R.id.btnRandom);
		mBtnReset = (Button) findViewById(R.id.btnReset);
		mBtnStart = (Button) findViewById(R.id.btnStart);
		mBtnStart.setEnabled(false);
		mBtnReset.setEnabled(false);

		mLevel = getIntent().getIntExtra("type", 3);
		mDrawableId = getIntent().getIntExtra("drawable", 0);
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int sttBarHeight = getStatusBarHeight();

		int margin = getResources().getDimensionPixelSize(R.dimen.photoview_margin);
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), mDrawableId);
		int fitWidth;
		int fitHeight;
		float blockSize;

		switch (mLevel) {
		case TYPE_EASY:
			mRow = 4;
			mColumn = 3;
			break;
		case TYPE_MEDIUM:
			mRow = 4;
			mColumn = 4;
			break;
		case TYPE_HARD:
			mRow = 5;
			mColumn = 5;
			break;

		default:
			break;
		}

		fitWidth = screenWidth - margin * 2 - BORDER_SIZE * 2 * mColumn;
		fitHeight = (int) (screenHeight - sttBarHeight - margin * 2 - BORDER_SIZE * 2 * (mRow + 1) - 150 * dm.density);
		blockSize = (float) fitWidth / mColumn;
		if (blockSize > (float) fitHeight / (mRow + 1)) {
			blockSize = (float) fitHeight / (mRow + 1);

			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.BELOW, R.id.rlTop);
			params.addRule(RelativeLayout.ABOVE, R.id.adView);
			int leftMargin = (int) ((screenWidth - (blockSize + BORDER_SIZE * 2) * mColumn) / 2);
			params.setMargins(leftMargin, margin, margin, margin);
			mPhotoview.setLayoutParams(params);
		} else {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.BELOW, R.id.rlTop);
			params.addRule(RelativeLayout.ABOVE, R.id.adView);
			int topMargin = (int) ((screenHeight - sttBarHeight - 150 * dm.density - (blockSize + BORDER_SIZE * 2)
					* (mRow + 1)) / 2);
			params.setMargins(margin, topMargin, margin, margin);
			mPhotoview.setLayoutParams(params);
		}
		bmp = Bitmap.createScaledBitmap(bmp, (int) (blockSize * mColumn), (int) (blockSize * mRow),
				true);
		ivPhoto.setImageResource(mDrawableId);
		mPhotoview.setRow(mRow);
		mPhotoview.setColumn(mColumn);

		splitBitmap(bmp, mColumn, mRow);
		mPhotoview.setBmpSize(bmp.getWidth() / mColumn + BORDER_SIZE * 2);
		mPhotoview.invalidate();

	}

	public void onBtnRandomClick(View v) {
		mPhotoview.random();
		mBtnStart.setEnabled(true);
		mBtnReset.setEnabled(true);
	}

	public void onBtnResetClick(View v) {
		mPhotoview.reset();
	}

	public void onBtnStartClick(View v) {
		if (mTimer != null) {
			mTimer.cancel();
		}

		mTimeStart = System.currentTimeMillis();

		mTimer = new Timer();
		mMyTimerTask = new MyTimerTask();

		mTimer.schedule(mMyTimerTask, 1000, 1000);
	}

	public void splitBitmap(Bitmap bitmap, int xCount, int yCount) {
		// Allocate a two dimensional array to hold the individual images.
		int width, height;
		// Divide the original bitmap width by the desired vertical column count
		width = bitmap.getWidth() / xCount;
		// Divide the original bitmap height by the desired horizontal row count
		height = bitmap.getHeight() / yCount;

		Bitmap bmp;
		// Loop the array and create bitmaps for each coordinate
		for (int x = 0; x < xCount; x++) {
			for (int y = 0; y < yCount; y++) {
				// Create the sliced bitmap
				bmp = Bitmap.createBitmap(bitmap, x * width, y * height, width, height);
				bmp = addWhiteBorder(bmp, BORDER_SIZE);
				BitmapObject bObj = new BitmapObject(bmp, x * (width + BORDER_SIZE * 2), (y + 1)
						* (height + BORDER_SIZE * 2));
				bObj.setOriIndex(y * mPhotoview.getColumn() + x + 1);
				bObj.setCurrentIndex(bObj.getOriIndex());
				if (bObj.getCurrentIndex() == 1) {
					bObj.setCanTranslate(true);
				}
				mPhotoview.addBmpObj(bObj);
			}
		}
		// Return the array
	}

	private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
		Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight()
				+ borderSize * 2, bmp.getConfig());
		Canvas canvas = new Canvas(bmpWithBorder);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bmp, borderSize, borderSize, null);
		return bmpWithBorder;
	}

	public void puzzleDone() {
		Log.i("puzzleDone", "puzzleDone");
		if (mTimer != null) mTimer.cancel();

		// check best time
		boolean isBestTime = false;
		SharedPreferences sharedPrefScore = getSharedPreferences(PREF_NAME_SCORE, Context.MODE_PRIVATE);
		SimpleDateFormat df = new SimpleDateFormat("mm:ss");
		String newTime = mTvTimeCounter.getText().toString();
		String oldBestTime = sharedPrefScore.getString(KEY_BEST_TIME + mLevel,
				"59:59-username").substring(0, 5);
		try {
			if (df.parse(oldBestTime).getTime() > df.parse(newTime).getTime()) {
				isBestTime = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Builder builder = new AlertDialog.Builder(this);
		// String msg =
		// String.format(getResources().getString(R.string.msg_your_time),
		// mTvTimeCounter.getText());
		// builder.setMessage(msg);
		// builder.setPositiveButton("OK", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		//
		// }
		// });
		// AlertDialog dialog = builder.show();
		//
		// TextView messageView = (TextView)
		// dialog.findViewById(android.R.id.message);
		// messageView.setGravity(Gravity.CENTER);

		showDialogSaveRecord(isBestTime);
	}

	private void showDialogSaveRecord(boolean isBestTime) {
		final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_save_score);

		dialog.show();

		TextView tvTimer = (TextView) dialog.findViewById(R.id.tvTime);
		final EditText etName = (EditText) dialog.findViewById(R.id.etName);
		
		if (isBestTime) {
			TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
			tvTitle.setText(getResources().getString(R.string.congratulate));
		}

		tvTimer.setText(String.format(getResources().getString(R.string.msg_your_time),
				mTvTimeCounter.getText()));
		Button cancelBtn = (Button) dialog.findViewById(R.id.btnDialogCancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}

		});

		Button saveBtn = (Button) dialog.findViewById(R.id.btnDialogSave);
		saveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				addNewRecord(etName);
				dialog.dismiss();
			}
		});
	}

	private void addNewRecord(EditText etName) {
		String name = etName.getText().toString().trim();
		String newTime = mTvTimeCounter.getText().toString();
		if (!name.isEmpty()) {
			try {
				SharedPreferences sharedPrefScore = getSharedPreferences(PREF_NAME_SCORE,
						Context.MODE_PRIVATE);

				// Update best time
				SimpleDateFormat df = new SimpleDateFormat("mm:ss");
				String oldBestTime = sharedPrefScore.getString(KEY_BEST_TIME + mLevel,
						"59:59-username").substring(0, 5);
				if (df.parse(oldBestTime).getTime() > df.parse(newTime).getTime()) {
					sharedPrefScore.edit().putString(KEY_BEST_TIME + mLevel, newTime + "-" + name)
							.commit();
				}

				// Update rank time
				Set<String> setTopTime = sharedPrefScore.getStringSet(KEY_RANK_TIME + mLevel, null);
				if (setTopTime != null) {
					ArrayList<String> listTopTime = new ArrayList<String>(setTopTime);
					for (String time : listTopTime) {
						if (df.parse(time.substring(0, 5)).getTime() > df.parse(newTime).getTime()) {
							listTopTime.add(listTopTime.indexOf(time), newTime + "-" + name);
							break;
						}
						Log.i("puzzleDone", time + " - string");
					}
					
					String longestTime = listTopTime.get(listTopTime.size() - 1);
					if (df.parse(newTime).getTime() > df.parse(longestTime.substring(0, 5)).getTime()) {
						listTopTime.add(newTime + "-" + name);
					}
					
					if (listTopTime.size() > 10) {
						listTopTime.remove(listTopTime.size() - 1);
					}
					
					Editor editor = sharedPrefScore.edit();
					editor.putStringSet(KEY_RANK_TIME + mLevel, new HashSet<String>(listTopTime));
					editor.commit();
				} else {
					ArrayList<String> listTopTime = new ArrayList<String>();
					listTopTime.add(newTime + "-" + name);
					
					Editor editor = sharedPrefScore.edit();
					editor.putStringSet(KEY_RANK_TIME + mLevel, new HashSet<String>(listTopTime));
					editor.commit();
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			// Calendar calendar = Calendar.getInstance();
			long nowTime = System.currentTimeMillis();
			Date date = new Date();
			date.setTime(nowTime - mTimeStart);

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
			final String strDate = simpleDateFormat.format(date);

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mTvTimeCounter.setText(strDate);
				}
			});
		}

	}

}
