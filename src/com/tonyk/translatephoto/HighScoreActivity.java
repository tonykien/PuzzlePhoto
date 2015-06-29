package com.tonyk.translatephoto;

import java.util.ArrayList;
import java.util.Set;

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

public class HighScoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);
		
		ListView lvHighScore = (ListView) findViewById(R.id.lvScore);
		
		int level = getIntent().getIntExtra("type", 3);
		
		SharedPreferences sharedPrefScore = getSharedPreferences(MainActivity.PREF_NAME_SCORE, Context.MODE_PRIVATE);
		Set<String> setScores = sharedPrefScore.getStringSet(MainActivity.KEY_RANK_TIME + level, null);
		if (setScores != null) {
			ArrayList<String> listScores = new ArrayList<String>(setScores);
			HSListAdapter adapter = new HSListAdapter(this, listScores);
			lvHighScore.setAdapter(adapter);
			
		} else {
			Log.i("", "pref null");
		}
		
	}
	
	private class HSListAdapter extends BaseAdapter {
		
		private Context mContext;
		private ArrayList<String> mTimeList = new ArrayList<String>();
		
		public HSListAdapter(Context context, ArrayList<String> timeList) {
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
