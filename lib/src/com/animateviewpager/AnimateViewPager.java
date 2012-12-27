package com.animateviewpager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class AnimateViewPager extends ViewPager {

	private static final String TAG = "AnimateViewPager";

	private CanvasTransformer mEnteringTransformer;
	private CanvasTransformer mLeavingTransformer;
	private OnPageChangeListener mExternalListener;

	private OnPageChangeListener mListener = new OnPageChangeListener() {
		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE)
				setIdle();
			if (mExternalListener != null)
				mExternalListener.onPageScrollStateChanged(state);
		}
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			render(position, positionOffset, positionOffsetPixels);
			if (mExternalListener != null)
				mExternalListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
		@Override
		public void onPageSelected(int position) {
			if (mExternalListener != null)
				mExternalListener.onPageSelected(position);
		}
	};

	public AnimateViewPager(Context context) {
		super(context);
		super.setOnPageChangeListener(mListener);
	}

	public AnimateViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnPageChangeListener(mListener);
	}

	@Override
	public void setOnPageChangeListener(final OnPageChangeListener listener) {
		mExternalListener = listener;
	}

	@Override
	public void addView(View child) {
		super.addView(attachAnimateFrameLayout(child));
	}

	@Override
	public void addView(View child, int index) {
		super.addView(attachAnimateFrameLayout(child), index);
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		super.addView(attachAnimateFrameLayout(child), params);
	}

	@Override
	public void addView(View child, int width, int height) {
		super.addView(attachAnimateFrameLayout(child), width, height);	
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(attachAnimateFrameLayout(child), index, params);
	}

	public void setEnteringTransformer(CanvasTransformer transformer) {
		mEnteringTransformer = transformer;
	}

	public void setLeavingTransformer(CanvasTransformer transformer) {
		mLeavingTransformer = transformer;
	}

	private void setIdle() {
		currPercent = 0.0f;
		for (int i = 0; i < getChildCount(); i++) {
			AnimateFrameLayout frame = (AnimateFrameLayout) getChildAt(i);
			frame.setState(IDLE);
		}
		invalidate();
	}

	private int currFirstItem;
	private float currPercent;

	private void setEntering(int position) {
		setStateAndSide(position, ENTERING);
	}

	private void setLeaving(int position) {
		setStateAndSide(position, LEAVING);
	}

	private void setStateAndSide(int position, int state) {
		AnimateFrameLayout frame = (AnimateFrameLayout) getChildAt(position);
		frame.setSide(currFirstItem == position ? LEFT : RIGHT);
		frame.setState(state);
		frame.invalidate();
	}

	private void render(int position, float positionOffset, int positionOffsetPixels) {
		currFirstItem = position;
		currPercent = positionOffset;
		Log.v(TAG, currPercent + "");
		int curr = getCurrentItem();
		if (positionOffset != 0) {
			setEntering(position == curr ? position + 1 : position);
			setLeaving(position == curr ? position : position + 1);
		}
		invalidate();
	}

	private View attachAnimateFrameLayout(View view) {
		if (!(view instanceof AnimateFrameLayout)) {
			AnimateFrameLayout frame = new AnimateFrameLayout(getContext());
			frame.setLayoutParams(view.getLayoutParams() != null ? view.getLayoutParams() :
				new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			frame.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			frame.setBackgroundColor(Color.BLACK);
			return frame;
		}
		return view;
	}

	private static final int ENTERING = 0;
	private static final int LEAVING = 1;
	private static final int IDLE = 2;

	private static final int LEFT = 0;
	private static final int RIGHT = 1;

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
			CanvasTransformer curr = state == ENTERING ? mEnteringTransformer : 
				(state == LEAVING ? mLeavingTransformer : null);
			if (curr != null) {
				canvas.save();
				float p = side == LEFT ? currPercent : 1.0f-currPercent;
				if (state == ENTERING)
					p = 1-p;
				curr.transformCanvas(canvas, p);
			}
			super.dispatchDraw(canvas);
			if (curr != null)
				canvas.restore();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
		}

	}

}
