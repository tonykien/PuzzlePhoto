package com.tonyk.translatephoto.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tonyk.translatephoto.R;

public class TopActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top);

		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(TopActivity.this, SelectPhotoActivity.class);
				startActivity(i);

			}
		});
	}
	
	public void onBtnHighScoreClick(View v) {
		Intent i = new Intent(TopActivity.this, RankTimeActivity.class);
		startActivity(i);
	}
}
