package com.utility.ui.commonCtrl.flash;

import android.graphics.Canvas;

import com.utility.ui.IView;

public class RotateFlash extends Flash {
	
	private float mFromDegress;
	private float mTransDegress;
	
	public RotateFlash(IView view, float degress, float pivotX, float pivotY) {
		super(view);
		
		mFromDegress = mView.getRotation();
		mTransDegress = degress - mFromDegress;
		mView.setPivotX(pivotX);
		mView.setPivotY(pivotY);
	}

	@Override
	protected void doAfter(Canvas canvas) {
	}
	
	@Override
	protected void doBefore(Canvas canvas) {
		mView.setRotation(mFromDegress+mTransDegress*getInput());
	}
	
	@Override
	protected void doCancel(boolean force) {
	}
	
	@Override
	protected void doStart() {
	}
}
