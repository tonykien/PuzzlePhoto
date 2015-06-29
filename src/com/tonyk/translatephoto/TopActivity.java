package com.tonyk.translatephoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;

public class TopActivity extends Activity {

	private RadioGroup mRadioGrp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top);

		mRadioGrp = (RadioGroup) findViewById(R.id.radioGrp);
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int radioId = mRadioGrp.getCheckedRadioButtonId();
				int type = 3;
				switch (radioId) {
				case R.id.radioEasy:
					type = 1;
					break;
				case R.id.radioMedium:
					type = 2;
					break;
				case R.id.radioHard:
					type = 3;
					break;

				default:
					break;
				}
				
				Intent i = new Intent(TopActivity.this, SelectPhotoActivity.class);
				i.putExtra("type", type);
				startActivity(i);

			}
		});
	}
	
	public void onBtnHighScoreClick(View v) {
		int radioId = mRadioGrp.getCheckedRadioButtonId();
		int type = 3;
		switch (radioId) {
		case R.id.radioEasy:
			type = 1;
			break;
		case R.id.radioMedium:
			type = 2;
			break;
		case R.id.radioHard:
			type = 3;
			break;

		default:
			break;
		}
		
		Intent i = new Intent(TopActivity.this, HighScoreActivity.class);
		i.putExtra("type", type);
		startActivity(i);
	}
}
