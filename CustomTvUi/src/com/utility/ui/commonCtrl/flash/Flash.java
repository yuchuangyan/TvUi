package com.utility.ui.commonCtrl.flash;

import android.graphics.Canvas;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.utility.ui.IView;

public abstract class Flash {
	
	protected IView mView;
	protected long mDuration = -1;
	protected Interpolator mInterpolator;
	protected IFlashListener mListener;
	
	private static final int DEFAULT_DURATION = 200;
	
	private long mStart;
	private boolean mFlashing = false;
	
	protected Flash(IView view) {
		mView = view;
		mView.setFlash(this);
	}
	
	protected abstract void doAfter(Canvas canvas);
	
	protected abstract void doBefore(Canvas canvas);
	
	protected abstract void doCancel(boolean force);
	
	protected abstract void doStart();
	
	protected float getInput() {
		return ((float) getPass()) / ((float) mDuration);
	}
	
	protected long getPass() {
		long pass = System.currentTimeMillis() - mStart;
		
		return mDuration<pass ? mDuration : pass;
	}
	
	private boolean passed() {
		return mDuration <= (System.currentTimeMillis()-mStart);
	}
	
	public void afterDraw(Canvas canvas) {
		if (!mFlashing) {
			return;
		}
		
		canvas.restore();
		doAfter(canvas);
		
		if (passed()) {
			mFlashing = false;
			
			if (null != mListener) {
				mListener.onFinish();
			}
		} else {
			mView.postInvalidate();
		}
	}
	
	public void beforeDraw(Canvas canvas) {
		if (!mFlashing) {
			return;
		}
		
		canvas.save();
		doBefore(canvas);
	}
	
	public boolean getFlashing(){
	    return mFlashing;
	}
	
	public void cancel(boolean force) {
		mFlashing = false;
		doCancel(force);
		
		if (null != mListener) {
			mListener.onCancel(force);
		}
	}
	
	public void start() {
		mStart = System.currentTimeMillis();
		mFlashing = true;
		
		if (null == mInterpolator) {
			mInterpolator = new AccelerateDecelerateInterpolator();
		}
		
		if (mDuration < 0) {
			mDuration = DEFAULT_DURATION;
		}
		
		doStart();
		
		if (null != mListener) {
			mListener.onStart();
		}
		
		mView.postInvalidate();
	}
	
	public void setDuration(long duration) {
		mDuration = duration;
	}
	
	public void setFlashListener(IFlashListener listener) {
		mListener = listener;
	}
	
	public void setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

}
