package com.animateviewpager;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class AnimateViewPager extends ViewPager {

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
		setOnPageChangeListener(mListener);
	}

	public AnimateViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnPageChangeListener(mListener);
	}

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
		for (int i = 0; i < getChildCount(); i++) {
			AnimateFrameLayout frame = (AnimateFrameLayout) getChildAt(i);
			frame.setState(IDLE);
			frame.setPercent(0);
		}
		invalidate();
	}

	private int currFirstItem;

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
	}

	private void render(int position, float positionOffset, int positionOffsetPixels) {
		currFirstItem = position;
		int curr = getCurrentItem();
		if (positionOffset != 0) {
			setEntering(position == curr ? position + 1 : position);
			setLeaving(position == curr ? position : position + 1);
		}
	}

	private View attachAnimateFrameLayout(View view) {
		if (view instanceof AnimateFrameLayout) {
			return view;
		} else {
			AnimateFrameLayout frame = new AnimateFrameLayout(view.getContext());
			if (view.getLayoutParams() != null)
				frame.setLayoutParams(view.getLayoutParams());
			frame.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return frame;
		}
	}

	private static final int ENTERING = 0;
	private static final int LEAVING = 1;
	private static final int IDLE = 2;

	private static final int LEFT = 0;
	private static final int RIGHT = 1;

	private class AnimateFrameLayout extends FrameLayout {

		private int state;
		private int side;
		private float percent;

		public AnimateFrameLayout(Context context) {
			super(context);
		}

		private void setState(int s) { state = s; }
		private void setSide(int s) { side = s; }
		private void setPercent(float p) { percent = p; }

		@Override
		protected void dispatchDraw(Canvas canvas) {
			CanvasTransformer curr = state == ENTERING ? mEnteringTransformer : 
				(state == LEAVING ? mLeavingTransformer : null);
			if (curr != null) {
				canvas.save();
				float p = percent;
				if ((state == ENTERING && side == LEFT) || (state == LEAVING && side == RIGHT)) {
					p = 1-percent;
				}
				curr.transformCanvas(canvas, p);
			}
			super.dispatchDraw(canvas);
			if (curr != null)
				canvas.restore();
		}
	}

}
