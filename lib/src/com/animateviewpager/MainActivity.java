package com.animateviewpager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
	

	private class AnimateFrameLayout extends FrameLayout {

		private int state;
		private int side;

		public AnimateFrameLayout(Context context) {
			super(context);
		}

		private void setState(int s) { state = s; }
		private void setSide(int s) { side = s; }

		@Override
		public boolean onInterceptTouchEvent(MotionEvent e) {
			return false;
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			super.dispatchDraw(canvas);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
		}

	}

	
	private class Adapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TextView tv = new TextView(MainActivity.this);
			tv.setText("Page " + position);
			tv.setTextColor(Color.WHITE);
			tv.setGravity(Gravity.CENTER);
			int color = position == 0 ? Color.RED : position == 1 ? Color.BLUE : Color.BLACK;
			tv.setBackgroundColor(color);
//			container.addView(tv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			AnimateFrameLayout f = new AnimateFrameLayout(MainActivity.this);
			f.setBackgroundColor(Color.RED);
			f.addView(tv);
			container.addView(f, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return f;
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
			return ((ViewGroup)v).getChildAt(0) == o;
		}
		
	}

}
