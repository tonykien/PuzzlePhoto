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
import android.widget.Toast;

import com.tonyk.translatephoto.R;

public class SelectPhotoActivity extends Activity implements OnClickListener {

	private static int RESULT_LOAD_IMG = 1;
	private int mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_photo);

		ImageView ivPhuongHong = (ImageView) findViewById(R.id.ivPhuongHong);
		ImageView ivTulip = (ImageView) findViewById(R.id.ivTulip);
		ImageView ivCuteDog = (ImageView) findViewById(R.id.ivCuteDog);

		ivPhuongHong.setOnClickListener(this);
		ivTulip.setOnClickListener(this);
		ivCuteDog.setOnClickListener(this);

		mType = getIntent().getIntExtra("type", MainActivity.TYPE_MEDIUM);

	}

	@Override
	public void onClick(View v) {
		int drawableId = 0;
		switch (v.getId()) {
		case R.id.ivCuteDog:
			if (mType == MainActivity.TYPE_EASY) {
				drawableId = R.drawable.cutedog3x4;
			} else {
				drawableId = R.drawable.cutedog;
			}
			break;
		case R.id.ivPhuongHong:
			if (mType == MainActivity.TYPE_EASY) {
				drawableId = R.drawable.phuonghong3x4;
			} else {
				drawableId = R.drawable.phuonghong;
			}
			break;
		case R.id.ivTulip:
			if (mType == MainActivity.TYPE_EASY) {
				drawableId = R.drawable.tulip3x4;
			} else {
				drawableId = R.drawable.tulip;
			}
			break;
		}

		Intent i = new Intent(this, MainActivity.class);
		i.putExtra("type", mType);
		i.putExtra("drawable", drawableId);
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
//		try {
			// When an Image is picked
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
				// Get the Image from data

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				// Get the cursor
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
						null, null);
				// Move to first row
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				
				Intent i = new Intent(SelectPhotoActivity.this, CropPhotoActivity.class);
				i.putExtra("photo_path", filePath);
				i.putExtra("type", mType);
				startActivity(i);

			} else {
				Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
			}
//		} catch (Exception e) {
//			Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
//		}

	}
}
