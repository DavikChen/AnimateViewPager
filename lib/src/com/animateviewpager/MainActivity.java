package com.animateviewpager;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		AnimateViewPager pager = (AnimateViewPager) findViewById(R.id.pager);
		pager.setAdapter(new Adapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class Adapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = new ImageView(MainActivity.this);
			int color = position == 0 ? Color.RED : position == 1 ? Color.BLUE : Color.GREEN;
			iv.setImageDrawable(new ColorDrawable(color));
			container.addView(iv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return iv;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView((View) obj);
		}
		
		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public boolean isViewFromObject(View v, Object o) {
			return v == o;
		}
		
	}

}
