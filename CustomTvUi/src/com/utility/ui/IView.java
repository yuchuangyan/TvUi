package com.utility.ui;

import com.utility.ui.commonCtrl.flash.Flash;

import android.view.KeyEvent;
import android.view.ViewGroup;


/**
 * 用于UI适配，UI导航的接口
 * <p>
 * 优先使用此接口中的方法
 * <p>
 * 对于.9图，或者wrapcontent依赖图的控件，使用Util.getCompatibleDrawable获取适配的资源图
 * <p>
 * MTextView 使用 getMTextSize, setMTextSize 处理字体大小
 */
public interface IView {
	
	// layout params
	/**
	 * 获取布局参数，已匹配1080p
	 * @return 被转换至1080p的布局参数
	 */
	ViewGroup.LayoutParams getMLayoutParams();
	
	/**
	 * 获取高度，已匹配1080p
	 * @return 被转换至1080p的高度
	 */
	int getMHeight();
	
	/**
	 * 获取宽度，已匹配1080p
	 * @return 被转换至1080p的宽度
	 */
	int getMWidth();
	
	/**
	 * 设置布局参数，使用1080p的参数
	 * @param params 按1080p设置的参数，内部将自动被转换应用到具体设备
	 */
	void setMLayoutParams(ViewGroup.LayoutParams params);
	
	// padding
	/**
	 * 获取下侧padding，已匹配1080p
	 * @return 被转换至1080p的下侧padding
	 */
	int getMPaddingBottom();

	/**
	 * 获取左侧padding，已匹配1080p
	 * @return 被转换至1080p的左侧padding
	 */
	int getMPaddingLeft();

	/**
	 * 获取右侧padding，已匹配1080p
	 * @return 被转换至1080p的右侧padding
	 */
	int getMPaddingRight();

	/**
	 * 获取上侧padding，已匹配1080p
	 * @return 被转换至1080p的上侧padding
	 */
	int getMPaddingTop();
	
	/**
	 * 按1080p的参数设置padding，内部将自动被应用到具体的设备
	 * @param left 左侧
	 * @param top 上侧
	 * @param right 右侧
	 * @param bottom 下侧
	 */
	void setMPadding(int left, int top, int right, int bottom);

	// focus
	/**
	 * 是否有焦点
	 * @return
	 */
	boolean hasMFocus();
	
	/**
	 * 设置聚焦状态
	 * @param focused true有聚焦，false未聚焦
	 */
	void setMFocus(boolean focused);

	// selected
	/**
	 * 是否被选中
	 * @return
	 */
	boolean isMSelected();
	
	/**
	 * 设置选中状态
	 * @param selected true被选中，false未选中
	 */
	void setMSelected(boolean selected);
	
	/** 
	 * 设置动画
	 * @param flash 动画
	 */
	void setFlash(Flash flash);
	
	// native contain
	boolean dispatchKeyEvent(KeyEvent event);
	
	int getVisibility ();
	void setVisibility (int visibility);

	void invalidate();
	void postInvalidate();
	
	float getAlpha();
	void setAlpha(float alpha);
	
	float getCameraDistance();
	void setCameraDistance(float distance);
	
	float getPivotX();
	void setPivotX(float pivotX);

	float getPivotY();
	void setPivotY(float pivotY);
	
	float getRotation();
	void setRotation(float rotation);
	
	float getRotationX();
	void setRotationX(float rotationX);
	
	float getRotationY();
	void setRotationY(float rotationY);
	
}
