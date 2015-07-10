package com.tonyk.translatephoto.activity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.tonyk.translatephoto.BitmapObject;
import com.tonyk.translatephoto.R;
import com.tonyk.translatephoto.Utils;
import com.tonyk.translatephoto.customview.PhotoViewCustom;

public class MainActivity extends Activity {

	public final static int BORDER_SIZE = 2;
	public final static int LEVEL_EASY = 1;
	public final static int LEVEL_MEDIUM = 2;
	public final static int LEVEL_HARD = 3;

	public final static String PREF_PUZZLE_PHOTO = "PrefPuzzlePhoto";
	public final static String KEY_BEST_TIME = "best_time";
	public final static String KEY_RANK_TIME = "rank_time";
	public final static String KEY_LEVEL = "level";
	public final static String KEY_PHOTO = "photo";
	public final static String KEY_PHOTO_BYTES = "photo_bytes";
	public final static String KEY_SAVE_COUNT = "save_count";

	public final static String TIME_FORMAT = "mm:ss";

	// private Matrix mMatrix;
	private PhotoViewCustom mPhotoview;
	private int mRow, mColumn;

	private Timer mTimer;
	private MyTimerTask mMyTimerTask;
	private TextView mTvTimeCounter, mTvBestTime;
	private long mTimeStart;

	private int mLevel;
	private int mDrawableId;

	private Button mBtnStart, mBtnRandom, mBtnReset;

	private MediaPlayer mBgSoundPlayer;
	private boolean mSoundOn = true;
	private boolean mMusicOn = true;

	private boolean mIsStarted = false;
	
	private InterstitialAd mInterstitialAd;
	private SharedPreferences mPuzzlePref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLevel = getIntent().getIntExtra(KEY_LEVEL, LEVEL_MEDIUM);
		mDrawableId = getIntent().getIntExtra(KEY_PHOTO, 0);

		mBgSoundPlayer = MediaPlayer.create(this, R.raw.background_music_cut);
		mBgSoundPlayer.setLooping(true);

		// admob
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		
		mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

		mTvBestTime = (TextView) findViewById(R.id.tvBestTime);
		mPuzzlePref = getSharedPreferences(PREF_PUZZLE_PHOTO,
				Context.MODE_PRIVATE);
		String bestTime = mPuzzlePref.getString(KEY_BEST_TIME + mLevel, "--:--");
		mTvBestTime.setText(bestTime);

		mPhotoview = (PhotoViewCustom) findViewById(R.id.photoview);
		mPhotoview.setContext(this);
		mTvTimeCounter = (TextView) findViewById(R.id.tvTimeCounter);
		ImageView ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

		mBtnRandom = (Button) findViewById(R.id.btnRandom);
		mBtnReset = (Button) findViewById(R.id.btnReset);
		mBtnStart = (Button) findViewById(R.id.btnStart);
		disableStartBtn();
		// mBtnReset.setEnabled(false);

		switch (mLevel) {
		case LEVEL_EASY:
			mRow = 4;
			mColumn = 3;
			mPhotoview.setRandomNumber(200);
			break;
		case LEVEL_MEDIUM:
			mRow = 4;
			mColumn = 4;
			mPhotoview.setRandomNumber(300);
			break;
		case LEVEL_HARD:
			mRow = 5;
			mColumn = 5;
			mPhotoview.setRandomNumber(400);
			break;

		default:
			break;
		}

		DisplayMetrics dm = getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		int sttBarHeight = Utils.getStatusBarHeight(this);

		Bitmap bmp;
		if (mDrawableId != 0) {
			bmp = BitmapFactory.decodeResource(getResources(), mDrawableId);
		} else {
			byte[] byteArray = getIntent().getByteArrayExtra(KEY_PHOTO_BYTES);
			bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		}

		int margin = getResources().getDimensionPixelSize(R.dimen.photoview_margin);
		int fitWidth = screenWidth - margin * 2 - BORDER_SIZE * 2 * mColumn;
		int fitHeight = (int) (screenHeight - sttBarHeight - margin * 2 - BORDER_SIZE * 2
				* (mRow + 1) - 150 * dm.density);
		float blockSize = (float) fitWidth / mColumn;
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
		ivPhoto.setImageBitmap(bmp);
		mPhotoview.setRow(mRow);
		mPhotoview.setColumn(mColumn);

		splitBitmap(bmp, mColumn, mRow);
		mPhotoview.setBmpSize(bmp.getWidth() / mColumn + BORDER_SIZE * 2);
		mPhotoview.invalidate();

	}

	// @Override
	// protected void onStart() {
	// if (mSoundPlayer != null && mSoundOn) {
	// mSoundPlayer.start();
	// }
	// super.onStart();
	// }
	//
	// @Override
	// protected void onStop() {
	// if (mSoundPlayer.isPlaying()) {
	// mSoundPlayer.pause();
	// }
	// super.onStop();
	// }

	@Override
	protected void onDestroy() {
		if (mBgSoundPlayer.isPlaying()) {
			mBgSoundPlayer.stop();
		}
		super.onDestroy();
	}

	private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                  .addTestDevice("510A6EBB684C5FE74FB127A57DF9580C")
                  .build();

        mInterstitialAd.loadAd(adRequest);
    }
	
	public void onBtnRandomClick(View v) {
		mPhotoview.disableSound();
		mPhotoview.random();
		if (mSoundOn) {
			mPhotoview.enableSound();
		}
		mBtnStart.setEnabled(true);
		mBtnReset.setEnabled(true);
	}

	public void onBtnResetClick(View v) {
		if (mTimer != null) mTimer.cancel();
		mTvTimeCounter.setText("00:00");
		mPhotoview.disableSound();
		mPhotoview.reset();
		if (mSoundOn) {
			mPhotoview.enableSound();
		}
		if (mBgSoundPlayer.isPlaying()) {
			mBgSoundPlayer.stop();
			try {
				mBgSoundPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mBtnRandom.setEnabled(true);
		disableStartBtn();
		mIsStarted = false;
	}

	public void onBtnStartClick(View v) {
		if (mSoundOn) {
			MediaPlayer mpStart = MediaPlayer.create(this, R.raw.start_bell);
			mpStart.start();
			mpStart.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if (mMusicOn) {
						mBgSoundPlayer.start();
					}

				}
			});
		} else if (mMusicOn) {
			mBgSoundPlayer.start();
		}

		mIsStarted = true;
		disableStartBtn();
		mBtnRandom.setEnabled(false);
		if (mSoundOn) {
			mPhotoview.enableSound();
		}

		if (mTimer != null) {
			mTimer.cancel();
		}

		mTimeStart = System.currentTimeMillis();

		mTimer = new Timer();
		mMyTimerTask = new MyTimerTask();

		mTimer.schedule(mMyTimerTask, 1000, 1000);

	}

	public void onBtnSoundClick(View v) {
		if (mSoundOn) {
			mSoundOn = false;
			mPhotoview.disableSound();
			((ImageButton) v).setImageResource(R.drawable.icon_sound_off);
		} else {
			mSoundOn = true;
			mPhotoview.enableSound();
			((ImageButton) v).setImageResource(R.drawable.icon_sound_on);
		}
	}

	public void onBtnMusicClick(View v) {
		if (mMusicOn) {
			mMusicOn = false;
			if (mBgSoundPlayer.isPlaying()) {
				mBgSoundPlayer.pause();
			}
			((ImageButton) v).setImageResource(R.drawable.icon_music_off);
		} else {
			mMusicOn = true;
			if (mIsStarted) {
				mBgSoundPlayer.start();
			}
			((ImageButton) v).setImageResource(R.drawable.icon_music_on);
		}
	}

	public void enableStartBtn() {
		if (!mIsStarted) {
			mBtnStart.setEnabled(true);
			// mBtnStart.setTextColor(Color.WHITE);
		}
	}

	public void disableStartBtn() {
		mBtnStart.setEnabled(false);
		// mBtnStart.setTextColor(Color.parseColor("#80ffffff"));
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
		if (!mIsStarted) {
			return;
		}
		mIsStarted = false;
		Log.i("puzzleDone", "puzzleDone");
		if (mTimer != null) {
			mTimer.cancel();
		}
		mBtnRandom.setEnabled(true);

		// check best time
		boolean isBestTime = false;
		SimpleDateFormat df = new SimpleDateFormat("mm:ss");
		String newTime = mTvTimeCounter.getText().toString();
		String oldBestTime = mPuzzlePref.getString(KEY_BEST_TIME + mLevel, "59:59-username")
				.substring(0, 5);
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
		
		// filter edittext (cannot input ',')
		InputFilter filter = new InputFilter() {
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
					int dstart, int dend) {
				for (int i = start; i < end; i++) {
					if (source.charAt(i) == ',') {
						return "";
					}
				}
				return null;
			}
		};
		etName.setFilters(new InputFilter[] { filter });

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
				String name = etName.getText().toString().trim();
				if (!name.isEmpty()) {
					addNewRecord(name);
					dialog.dismiss();
					
					int saveCount = mPuzzlePref.getInt(KEY_SAVE_COUNT, 0);
					saveCount += 1;
					if (saveCount < 20) {
						if ((saveCount % 5) == 0 && mInterstitialAd.isLoaded()) {
		                    mInterstitialAd.show();
		                }
					} else {
						if ((saveCount % 3) == 0 && mInterstitialAd.isLoaded()) {
		                    mInterstitialAd.show();
		                }
					}
				} else {
					// TODO name empty
					Toast.makeText(MainActivity.this, R.string.msg_please_input_name, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}

	private void addNewRecord(String name) {
		String newTime = mTvTimeCounter.getText().toString();
		if (!name.isEmpty()) {
			try {
				// Update best time
				SimpleDateFormat df = new SimpleDateFormat(TIME_FORMAT);
				String oldBestTime = mPuzzlePref.getString(KEY_BEST_TIME + mLevel,
						"59:59-username").substring(0, 5);
				if (df.parse(oldBestTime).getTime() > df.parse(newTime).getTime()) {
					mPuzzlePref.edit().putString(KEY_BEST_TIME + mLevel, newTime + "-" + name)
							.commit();

					// update textview best time
					mTvBestTime.setText(newTime + "-" + name);
				}

				// Update rank time
				// Set<String> setTopTime =
				// sharedPrefScore.getStringSet(KEY_RANK_TIME + mLevel, null);
				String rankTime = mPuzzlePref.getString(KEY_RANK_TIME + mLevel, "");
				if (!rankTime.isEmpty()) {
					String[] arrRank = rankTime.split(",");
					StringBuilder newRankTime = new StringBuilder();
					boolean addInRank = false;
					for (int i = 0; i < arrRank.length; i++) {
						if (!addInRank
								&& df.parse(arrRank[i].substring(0, 5)).getTime() > df.parse(
										newTime).getTime()) {
							String value = newTime + "-" + name;
							newRankTime.append(value).append(",");
							addInRank = true;
						}
						if (!addInRank || (addInRank && i < 9)) {
							newRankTime.append(arrRank[i]).append(",");
						}
					}
					if (!addInRank && arrRank.length < 10) {
						String value = newTime + "-" + name;
						newRankTime.append(value).append(",");
					}
					newRankTime.deleteCharAt(newRankTime.length() - 1);

					Editor editor = mPuzzlePref.edit();
					editor.putString(KEY_RANK_TIME + mLevel, newRankTime.toString());
					editor.commit();

					// ArrayList<String> listTopTime = new
					// ArrayList<String>(setTopTime);
					// for (String time : listTopTime) {
					// Log.i("puzzleDone", time + " - string");
					// if (df.parse(time.substring(0, 5)).getTime() >
					// df.parse(newTime).getTime()) {
					// listTopTime.add(listTopTime.indexOf(time), newTime + "-"
					// + name);
					// break;
					// }
					// }
					//
					// String longestTime = listTopTime.get(listTopTime.size() -
					// 1);
					// if (df.parse(newTime).getTime() >
					// df.parse(longestTime.substring(0, 5)).getTime()) {
					// listTopTime.add(newTime + "-" + name);
					// }
					//
					// if (listTopTime.size() > 10) {
					// listTopTime.remove(listTopTime.size() - 1);
					// }
					//
					// Editor editor = sharedPrefScore.edit();
					// editor.putStringSet(KEY_RANK_TIME + mLevel, new
					// HashSet<String>(listTopTime));
					// editor.commit();
				} else {
					// ArrayList<String> listTopTime = new ArrayList<String>();
					// listTopTime.add(newTime + "-" + name);
					String value = newTime + "-" + name;
					Editor editor = mPuzzlePref.edit();
					editor.putString(KEY_RANK_TIME + mLevel, value);
					editor.commit();
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			// Calendar calendar = Calendar.getInstance();
			long nowTime = System.currentTimeMillis();
			Date date = new Date();
			date.setTime(nowTime - mTimeStart);
			if (date.getTime() >= 60 * 60 * 1000) {
				loseGame();
				return;
			}

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
			final String strDate = simpleDateFormat.format(date);

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mTvTimeCounter.setText(strDate);
				}
			});
		}

	}

	public void loseGame() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		mBtnRandom.setEnabled(true);
		mBtnStart.setEnabled(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You lose!");
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
