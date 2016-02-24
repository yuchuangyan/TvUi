package com.utility.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import com.utility.ui.commonCtrl.flash.Flash;

/**
 * 代替TextView
 * <p>
 * 使用getMTextSize, setMTextSize 处理字体大小
 */
public class MTextView extends TextView implements IView {

	private Flash mFlash;
	private boolean mHasMFocus = false;
	private boolean mIsMSelected = false;

	public MTextView(Context context) {
		super(context);

		init();
	}

	public MTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public MTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
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

	// text size
	public float getMTextSize() {
		return Util.convertOut(getTextSize());
	}

    public void setMTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.convertIn(size));
    }

    public void setMTextSize(int unit, float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.convertIn(size));
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
    
    // init
    private void init() {
    	setMTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
        setMPadding(
        		getPaddingLeft(), getPaddingTop(), 
        		getPaddingRight(), getPaddingBottom());
    }

}
