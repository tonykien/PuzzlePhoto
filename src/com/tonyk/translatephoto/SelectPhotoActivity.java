package com.tonyk.translatephoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SelectPhotoActivity extends Activity implements OnClickListener {
	
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
		
		mType = getIntent().getIntExtra("type", 3);

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
		finish();
	}
}
