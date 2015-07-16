package com.tonyk.puzzlephoto.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tonyk.puzzlephoto.R;

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

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.exit_app_title);
		builder.setMessage(R.string.msg_rate_app);
		builder.setPositiveButton(R.string.rate_text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Uri uri = Uri.parse("market://details?id=" + getPackageName());
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://play.google.com/store/apps/details?id="
									+ getPackageName())));
				}

				dialog.dismiss();
				finish();

			}
		});

		builder.setNeutralButton(R.string.exit_text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				finish();
			}
		});

		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();

		Button rateBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		rateBtn.setTextColor(getResources().getColor(R.color.app_color));
	}
}
