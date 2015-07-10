package com.tonyk.translatephoto.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tonyk.translatephoto.R;

public class SelectPhotoActivity extends Activity implements OnClickListener {

	public final static String KEY_PHOTO_PATH = "photo_path";
	private static int RESULT_LOAD_IMG = 1;
	private int mLevel = MainActivity.LEVEL_EASY;
	private RadioGroup mRadioGrp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_photo);

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"510A6EBB684C5FE74FB127A57DF9580C").build();
		mAdView.loadAd(adRequest);

		mRadioGrp = (RadioGroup) findViewById(R.id.radioGrp);
		mRadioGrp.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radioEasy:
					mLevel = MainActivity.LEVEL_EASY;
					break;
				case R.id.radioMedium:
					mLevel = MainActivity.LEVEL_MEDIUM;
					break;
				case R.id.radioHard:
					mLevel = MainActivity.LEVEL_HARD;
					break;

				default:
					break;
				}

			}
		});

		ImageView ivPhuongHong = (ImageView) findViewById(R.id.ivPhuongHong);
		ImageView ivTulip = (ImageView) findViewById(R.id.ivTulip);
		ImageView ivCuteDog = (ImageView) findViewById(R.id.ivCuteDog);

		ivPhuongHong.setOnClickListener(this);
		ivTulip.setOnClickListener(this);
		ivCuteDog.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int drawableId = 0;
		switch (v.getId()) {
		case R.id.ivCuteDog:
			if (mLevel == MainActivity.LEVEL_EASY) {
				drawableId = R.drawable.cutedog3x4;
			} else {
				drawableId = R.drawable.cutedog;
			}
			break;
		case R.id.ivPhuongHong:
			if (mLevel == MainActivity.LEVEL_EASY) {
				drawableId = R.drawable.phuonghong3x4;
			} else {
				drawableId = R.drawable.phuonghong;
			}
			break;
		case R.id.ivTulip:
			if (mLevel == MainActivity.LEVEL_EASY) {
				drawableId = R.drawable.tulip3x4;
			} else {
				drawableId = R.drawable.tulip;
			}
			break;
		}

		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(MainActivity.KEY_LEVEL, mLevel);
		i.putExtra(MainActivity.KEY_PHOTO, drawableId);
		startActivity(i);
		// finish();
	}

	public void onBtnGalleryClick(View v) {
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// Start the Intent
		startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
				// Get the Image from data

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
						null, null);
				// Move to first row
				if (cursor.moveToFirst()) {
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					if (cursor.getColumnCount() > columnIndex) {
						String filePath = cursor.getString(columnIndex);
						cursor.close();
						if (filePath == null) {
							Toast.makeText(this, R.string.msg_error_photo, Toast.LENGTH_SHORT).show();
						} else {
							Intent i = new Intent(SelectPhotoActivity.this, CropPhotoActivity.class);
							i.putExtra(KEY_PHOTO_PATH, filePath);
							i.putExtra(MainActivity.KEY_LEVEL, mLevel);
							startActivity(i);
						}
					}
				} else {
					Toast.makeText(this, R.string.msg_error_photo, Toast.LENGTH_SHORT).show();
				}

			} else {
				// Toast.makeText(this, "You haven't picked Image",
				// Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, R.string.msg_something_wrong, Toast.LENGTH_SHORT).show();
		}

	}
}
