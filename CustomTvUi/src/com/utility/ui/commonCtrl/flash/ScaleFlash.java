package com.utility.ui.commonCtrl.flash;

import android.graphics.Canvas;
import android.widget.AbsoluteLayout;

import com.utility.ui.IView;

@SuppressWarnings("deprecation")
public class ScaleFlash extends Flash {
	
	private int mFromX;
	private int mFromY;
	private int mTransX;
	private int mTransY;

	private int mFromWidth;
	private int mFromHeight;
	private int mTransWidth;
	private int mTransHeight;
	
	public ScaleFlash(IView view, int width, int height) {
		super(view);
		
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) mView.getMLayoutParams();
		mFromWidth = params.width;
		mFromHeight = params.height;
		mTransWidth = width - mFromWidth;
		mTransHeight = height - mFromHeight;
		
		mFromX = params.x;
		mFromY = params.y;
		mTransX = -mTransWidth/2;
		mTransY = -mTransHeight/2;
	}

	@Override
	protected void doAfter(Canvas canvas) {
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) mView.getMLayoutParams();
		float input = getInput();
		params.x = (int) (mFromX + mTransX*input);
		params.y = (int) (mFromY + mTransY*input);
		params.width = (int) (mFromWidth + mTransWidth*input);
		params.height = (int) (mFromHeight + mTransHeight*input);
		mView.setMLayoutParams(params);
	}
	
	@Override
	protected void doBefore(Canvas canvas) {
		
	}
	
	@Override
	protected void doCancel(boolean force) {
		
	}
	
	@Override
	protected void doStart() {
		
	}
}
