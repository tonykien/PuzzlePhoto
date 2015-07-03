package com.tonyk.translatephoto.activity;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.tonyk.translatephoto.R;
import com.tonyk.translatephoto.Utils;
import com.tonyk.translatephoto.customview.MaskViewCustom;

public class CropPhotoActivity extends Activity {

	private ImageView mIvPhotoView;
	private MaskViewCustom mMaskView;

	private Bitmap mBmpResized;
	private float mScaleRatio;
	
	private int mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_photo);

		mIvPhotoView = (ImageView) findViewById(R.id.ivPhotoView);
		mMaskView = (MaskViewCustom) findViewById(R.id.maskView);

		mType = getIntent().getIntExtra("type", MainActivity.TYPE_MEDIUM);
		if (mType == MainActivity.TYPE_EASY) {
			mMaskView.setRectRatio(4f/3);
		}
		String pathName = getIntent().getStringExtra("photo_path");
		mBmpResized = Utils.resizeBitmap(pathName, 1000);

		int widthView = getResources().getDisplayMetrics().widthPixels;
		int heightView = getResources().getDisplayMetrics().heightPixels
				- Utils.getStatusBarHeight(this)
				- getResources().getDimensionPixelSize(R.dimen.top_bar_height_crop_screen);
		mScaleRatio = (float) mBmpResized.getWidth() / widthView;
		if (mScaleRatio < (float) mBmpResized.getHeight() / heightView) {
			mScaleRatio = (float) mBmpResized.getHeight() / heightView;
		}

		LayoutParams params = new LayoutParams((int) (mBmpResized.getWidth() / mScaleRatio),
				(int) (mBmpResized.getHeight() / mScaleRatio));
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mIvPhotoView.setLayoutParams(params);
		mIvPhotoView.setImageBitmap(mBmpResized);

	}

	public void onBtnDoneClick(View v) {
		int x = (int) (mMaskView.getXTop() * mScaleRatio);
		int y = (int) (mMaskView.getYTop() * mScaleRatio);
		int width = (int) (mMaskView.getRectWidth() * mScaleRatio);
		int height = (int) (mMaskView.getRectHeight() * mScaleRatio);
		Bitmap cropBmp = Bitmap.createBitmap(mBmpResized, x, y, width, height);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		cropBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("photo_bytes", byteArray);
		intent.putExtra("type", mType);
		startActivity(intent);
		mBmpResized.recycle();
		cropBmp.recycle();
		finish();
		
	}

}
