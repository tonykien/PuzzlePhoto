package com.tonyk.translatephoto.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tonyk.translatephoto.R;

public class HighScoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);
		
		ListView lvHighScore = (ListView) findViewById(R.id.lvScore);
		
		int level = getIntent().getIntExtra("type", MainActivity.LEVEL_MEDIUM);
		
		SharedPreferences sharedPrefScore = getSharedPreferences(MainActivity.PREF_PUZZLE_PHOTO, Context.MODE_PRIVATE);
		String scoreRank = sharedPrefScore.getString(MainActivity.KEY_RANK_TIME + level, "");
		if (!scoreRank.isEmpty()) {
			String[] arrRank = scoreRank.split(",");
			HSListAdapter adapter = new HSListAdapter(this, Arrays.asList(arrRank));
			lvHighScore.setAdapter(adapter);
			
		} else {
			Log.i("", "pref null");
		}
		
	}
	
	private class HSListAdapter extends BaseAdapter {
		
		private Context mContext;
		private List<String> mTimeList = new ArrayList<String>();
		
		public HSListAdapter(Context context, List<String> timeList) {
			mContext = context;
			mTimeList = timeList;
		}

		@Override
		public int getCount() {
			return mTimeList.size();
		}

		@Override
		public Object getItem(int position) {
			return mTimeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_high_score, null, false);
			
			if (convertView != null) {
				TextView tvRank = (TextView) convertView.findViewById(R.id.tvRankRow);
				TextView tvTime = (TextView) convertView.findViewById(R.id.tvTimeRow);
				TextView tvName = (TextView) convertView.findViewById(R.id.tvNameRow);
				
				String[] scoreParts = mTimeList.get(position).split("-");
				tvTime.setText(scoreParts[0]);
				tvName.setText(scoreParts[1]);
				tvRank.setText((position + 1) + "");
			}
			
			return convertView;
		}
		
	}
}
