package com.utility.ui.commonCtrl.flash;

import android.graphics.Canvas;
import android.widget.AbsoluteLayout;

import com.utility.ui.IView;

@SuppressWarnings("deprecation")
public class TransFlash extends Flash {
	
	private boolean mWithScale;
	private int mFromX;
	private int mFromY;
	private int mTransX;
	private int mTransY;
	
	private int mFromWidth;
	private int mFromHeight;
	private int mTransWidth;
	private int mTransHeight;
	
	public TransFlash(IView view, int x, int y) {
		super(view);

		mWithScale = false;
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) mView.getMLayoutParams();
		mFromX = params.x;
		mFromY = params.y;
		mTransX = x - mFromX;
		mTransY = y - mFromY;
	}
	
	public TransFlash(IView view, int x, int y, int w, int h) {
		super(view);

		mWithScale = true;
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) mView.getMLayoutParams();
		
		mFromWidth = params.width;
		mFromHeight = params.height;
		mTransWidth = w- mFromWidth;
		mTransHeight = h- mFromHeight;
		
		mFromX = params.x + params.width/2;
		mFromY = params.y + params.height/2;
		mTransX = x + w/2 - mFromX;
		mTransY = y + h/2 - mFromY;
	}
	
	@Override
	protected void doAfter(Canvas canvas) {
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) mView.getMLayoutParams();
		float input = getInput();
		
		if (mWithScale) {
			params.width = (int) (mFromWidth + mTransWidth*input);
			params.height = (int) (mFromHeight + mTransHeight*input);
			params.x = (int) (mFromX + mTransX*input - params.width/2);
			params.y = (int) (mFromY + mTransY*input - params.height/2);
		} else {
			params.x = (int) (mFromX + mTransX*input);
			params.y = (int) (mFromY + mTransY*input);
		}

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
