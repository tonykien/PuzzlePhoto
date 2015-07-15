package com.tonyk.puzzlephoto.activity;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.tonyk.puzzlephoto.Utils;
import com.tonyk.puzzlephoto.customview.MaskViewCustom;
import com.tonyk.translatephoto.R;

public class CropPhotoActivity extends Activity {

	private ImageView mIvPhotoView;
	private MaskViewCustom mMaskView;

	private Bitmap mBmpResized;
	private float mScaleRatio;

	private int mLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_photo);

		mIvPhotoView = (ImageView) findViewById(R.id.ivPhotoView);
		mMaskView = (MaskViewCustom) findViewById(R.id.maskView);

		mLevel = getIntent().getIntExtra(MainActivity.KEY_LEVEL, MainActivity.LEVEL_MEDIUM);
		if (mLevel == MainActivity.LEVEL_EASY) {
			mMaskView.setRectRatio(4f / 3);
		}
		String pathName = getIntent().getStringExtra(SelectPhotoActivity.KEY_PHOTO_PATH);
		Log.i("pathName", "pathName:" + pathName);
		mBmpResized = Utils.resizeBitmap(pathName, 1000);

		if (mBmpResized != null) {
			setLayoutImv();
		} else {
			Toast.makeText(this, R.string.msg_something_wrong, Toast.LENGTH_SHORT).show();
		}

	}

	public void setLayoutImv() {
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

	public void onBtnRotateClockClick(View v) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		mBmpResized = Bitmap.createBitmap(mBmpResized, 0, 0, mBmpResized.getWidth(),
				mBmpResized.getHeight(), matrix, true);
		setLayoutImv();
	}
	
	public void onBtnRotateAntiClockClick(View v) {
		Matrix matrix = new Matrix();
		matrix.postRotate(-90);
		mBmpResized = Bitmap.createBitmap(mBmpResized, 0, 0, mBmpResized.getWidth(),
				mBmpResized.getHeight(), matrix, true);
		setLayoutImv();
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
		intent.putExtra(MainActivity.KEY_PHOTO_BYTES, byteArray);
		intent.putExtra(MainActivity.KEY_LEVEL, mLevel);
		startActivity(intent);
		mBmpResized.recycle();
		cropBmp.recycle();
		finish();
	}

	public void onBtnCancelClick(View v) {
		finish();
	}

}
