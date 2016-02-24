package com.utility.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.utility.ui.commonCtrl.flash.Flash;

/**
 * 代替RelativeLayout
 */
@SuppressWarnings("deprecation")
public class MRelativeLayout extends RelativeLayout implements IView {

	private Flash mFlash;
	private boolean mHasMFocus = false;
	private boolean mIsMSelected = false;

	public MRelativeLayout(Context context) {
		super(context);
		
		init();
	}

	public MRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public MRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		init();
	}

	// view group
    public void addIView(IView child) {
        addView((View) child);
    }
	
    public void addIView(IView child, ViewGroup.LayoutParams params) {
        addView((View) child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, Util.convertIn(params));
    }
    
	public void removeIView(IView view) {
		removeView((View) view);
	}
	
    // layout params
	@Override
    public ViewGroup.LayoutParams getMLayoutParams() {
		return Util.convertOut(getLayoutParams());
	}
	
	@Override
	public int getMHeight() {
		return Util.convertOut(getHeight());
	}
	
	@Override
	public int getMWidth() {
		return Util.convertOut(getWidth());
	}
	
	@Override
    public void setMLayoutParams(ViewGroup.LayoutParams params) {
		setLayoutParams(Util.convertIn(params));
	}
	
	// padding
	@Override
	public int getMPaddingBottom() {
		return Util.convertOut(getPaddingBottom());
	}
	
	@Override
	public int getMPaddingLeft() {
		return Util.convertOut(getPaddingLeft());
	}
	
	@Override
	public int getMPaddingRight() {
		return Util.convertOut(getPaddingRight());
	}
	
	@Override
	public int getMPaddingTop() {
		return Util.convertOut(getPaddingTop());
	}

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
    	super.setPadding(
    			Util.convertIn(left), Util.convertIn(top), 
    			Util.convertIn(right), Util.convertIn(bottom));
    }

    @Override
    public void setMPadding(int left, int top, int right, int bottom) {
    	setPadding(
    			Util.convertIn(left), Util.convertIn(top), 
    			Util.convertIn(right), Util.convertIn(bottom));
    }

    // focus
    @Override
 	public boolean hasMFocus() {
    	return mHasMFocus;
    }
    
    @Override
 	public void setMFocus(boolean focused) {
    	mHasMFocus = focused;
 		OnFocusChangeListener l = getOnFocusChangeListener();
 		
 		if (null != l) {
 			l.onFocusChange(this, mHasMFocus);
 		}
 	}
    
	// selected
    @Override
    public boolean isMSelected() {
    	return mIsMSelected;
    }
    
    @Override
    public void setMSelected(boolean selected) {
    	mIsMSelected = selected;
    }
    
	// flash
	@Override
	protected void onDraw(Canvas canvas) {
		if (null != mFlash) {
			mFlash.beforeDraw(canvas);
		}
		
		super.onDraw(canvas);

		if (null != mFlash) {
			mFlash.afterDraw(canvas);
		}
	}
	
	@Override
	public void setFlash(Flash flash) {
		mFlash = flash;
	}
    
    //init
	private void init() {
		setMPadding(
        		getPaddingLeft(), getPaddingTop(), 
        		getPaddingRight(), getPaddingBottom());
	}

}
