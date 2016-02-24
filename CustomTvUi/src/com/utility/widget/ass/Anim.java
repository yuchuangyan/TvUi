package com.utility.widget.ass;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsoluteLayout;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.utility.ui.IView;
import com.utility.ui.commonCtrl.flash.IFlashListener;
import com.utility.ui.commonCtrl.flash.TransFlash;

@SuppressWarnings("deprecation")
public class Anim {
	
	public static final float FOCUSED_SCALE = 1.16f;
	public static final float NORMAL_SCALE = 1.0f;
	
	public static final long DURATION = 200;
	private static long mDuration = 200;
	
	public static final Interpolator MOVE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
	public static final Interpolator SCALE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
	
	public static void scaleFocused(IView view) {
		ViewPropertyAnimator.animate((View) view)
		.scaleX(FOCUSED_SCALE)
		.scaleY(FOCUSED_SCALE)
		.setInterpolator(SCALE_INTERPOLATOR)
		.setDuration(DURATION)
		.start();
	}

	public static void scaleFocused(IView view, double pivotX, double pivotY) {
		ViewHelper.setPivotX((View) view, (float) pivotX);
		ViewHelper.setPivotY((View) view, (float) pivotY);

		ViewPropertyAnimator.animate((View) view)
		.scaleX(FOCUSED_SCALE)
		.scaleY(FOCUSED_SCALE)
		.setInterpolator(SCALE_INTERPOLATOR)
		.setDuration(DURATION)
		.start();
	}
	
	public static void scaleNormal(IView view) {
		ViewPropertyAnimator.animate((View) view)
		.scaleX(NORMAL_SCALE)
		.scaleY(NORMAL_SCALE)
		.setInterpolator(SCALE_INTERPOLATOR)
		.setDuration(DURATION)
		.start();
	}
	
	public static void trans(IView view, AbsoluteLayout.LayoutParams params) {
		trans(view, params.x, params.y);
	}
	
	public static void trans(IView view, int x, int y) {
		TransFlash transFlash = new TransFlash(view, x, y);
		transFlash.setDuration(mDuration);
		transFlash.start();
	}
	
	public static void trans(IView view, int x, int y, IFlashListener callBack) {
		TransFlash transFlash = new TransFlash(view, x, y);
		transFlash.setDuration(mDuration);
		transFlash.setFlashListener(callBack);
		transFlash.start();
	}

	public static void transAndScale(IView view, AbsoluteLayout.LayoutParams params) {
		transAndScale(view, params.x, params.y, params.width, params.height);
	}

	public static void transAndScale(IView view, int x, int y, int w, int h) {
		TransFlash transFlash = new TransFlash(view, x, y, w, h);
		transFlash.setDuration(mDuration);
		transFlash.start();
	}
	
	public static void setDuration(int duration){
		mDuration = duration;
	}

}
