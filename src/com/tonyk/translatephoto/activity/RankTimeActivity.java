package com.tonyk.translatephoto.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;

import com.tonyk.translatephoto.R;

public class RankTimeActivity extends ActionBarActivity {

	private static final int NUM_PAGES = 3;

	private ViewPager mPager;

	private PagerAdapter mPagerAdapter;

	private TabListener mTabListener = new TabListener() {

		@Override
		public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction arg1) {
			mPager.setCurrentItem(tab.getPosition());
			
		}

		@Override
		public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank_time);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setDisplayShowCustomEnabled(false);

		Tab tabEasy = actionBar.newTab().setText(R.string.easy_text);
		tabEasy.setTabListener(mTabListener);

		Tab tabMedium = actionBar.newTab().setText(R.string.medium_text);
		tabMedium.setTabListener(mTabListener);

		Tab tabHard = actionBar.newTab().setText(R.string.hard_text);
		tabHard.setTabListener(mTabListener);

		actionBar.addTab(tabEasy);
		actionBar.addTab(tabMedium);
		actionBar.addTab(tabHard);

		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			int level = 0;
			switch (position) {
			case 0:
				level = MainActivity.LEVEL_EASY;
				break;
			case 1:
				level = MainActivity.LEVEL_MEDIUM;
				break;
			case 2:
				level = MainActivity.LEVEL_HARD;
				break;
			}
			return new RankTimeFragment(level, RankTimeActivity.this);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}
}
