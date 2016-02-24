package com.utility.ui.commonCtrl.flash;

import com.utility.ui.IView;

import android.graphics.Canvas;



public class AlphaFlash extends Flash {
	
	private float mFromAlpha;
	private float mTransAlpha;
	
	protected AlphaFlash(IView view, float alpha) {
		super(view);
		
		mFromAlpha = view.getAlpha();
		mTransAlpha = alpha - mFromAlpha;
	}

	@Override
	protected void doAfter(Canvas canvas) {
	}
	
	@Override
	protected void doBefore(Canvas canvas) {
		mView.setAlpha(mFromAlpha + mTransAlpha*getInput());
	}
	
	@Override
	protected void doCancel(boolean force) {
	}
	
	@Override
	protected void doStart() {
	}
}
