package com.tonyk.translatephoto.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tonyk.translatephoto.R;

public class RankTimeFragment extends Fragment {

	private int mLevel;
	private Context mContext;
	
	public RankTimeFragment() {
		
	}

	public RankTimeFragment(int level, Context context) {
		mLevel = level;
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_high_score, container,
				false);

		ListView lvHighScore = (ListView) rootView.findViewById(R.id.lvScore);

		SharedPreferences sharedPrefScore = mContext.getSharedPreferences(MainActivity.PREF_PUZZLE_PHOTO,
				Context.MODE_PRIVATE);
		String scoreRank = sharedPrefScore.getString(MainActivity.KEY_RANK_TIME + mLevel, "");
		if (!scoreRank.isEmpty()) {
			String[] arrRank = scoreRank.split(",");
			HSListAdapter adapter = new HSListAdapter(Arrays.asList(arrRank));
			lvHighScore.setAdapter(adapter);

		} else {
			Log.i("", "pref null");
		}

		return rootView;
	}

	private class HSListAdapter extends BaseAdapter {

		private List<String> mTimeList = new ArrayList<String>();

		public HSListAdapter(List<String> timeList) {
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

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_high_score, parent, false);

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
