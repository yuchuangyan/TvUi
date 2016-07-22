package com.frank.customtvui.widget;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.utility.ui.IView;
import com.utility.ui.MAbsoluteLayout;
import com.utility.ui.Util;
import com.utility.widget.ass.Anim;
import com.utility.widget.ass.IAdapter;
import com.utility.widget.ass.Recycler;
import com.utility.widget.ass.ViewWithShadowAttr;

@SuppressWarnings({ "unused", "deprecation" })
public abstract class MGroupView extends MAbsoluteLayout {
	
	/**
	 * 是否已获取到尺寸信息
	 */
	protected boolean mGainedViewportSize;
	/**
	 * 当前宽度，剪切域
	 */
	protected int mViewportWidth;
	/**
	 * 当前高度，剪切域，屏幕显示部分的高度
	 */
	protected int mViewportHeight;
	
	/**
	 * 数据，视图适配器
	 */
	protected IAdapter mAdapter;
	
	/**
	 * 元素容器的容器，放置非聚焦容器和聚焦容器
	 */
	protected MAbsoluteLayout mViewPanel;
	/**
	 * 非聚焦元素容器
	 */
	protected MAbsoluteLayout mViewNormalPanel;
	/**
	 * 聚焦元素容器
	 */
	protected MAbsoluteLayout mViewFocusedPanel;
	
	/**
	 * 是否使用全局焦点
	 */
	protected boolean mUseGlobalFocus;
	/**
	 * 焦点视图
	 */
	protected IView mViewFocus;
	/**
	 * GroupView与FocusView之间的水平相对位置差
	 */
	protected int mGroupOffsetX;
	/**
	 * GroupView与FocusView之间的垂直相对位置差
	 */
	protected int mGroupOffsetY;
	
	/**
	 * 焦点视图的信息
	 */
	protected ViewWithShadowAttr mAttrFocus;
	
	/**
	 * 被选中的元素索引
	 */
	protected int mSelected = INVALIDATE_INDEX;
	/**
	 * 被聚焦的元素索引
	 */
	protected int mFocused = INVALIDATE_INDEX;
	
	protected Set<Integer> mSetSkip = new HashSet<Integer>();
	
	/**
	 * 复用回收器
	 */
	protected Recycler<Integer, IView> mRecycler = new Recycler<Integer, IView>();
	
	/**
	 * 非法的索引
	 */
	public static final int INVALIDATE_INDEX = -1;
	
	/**
	 * true:支持最左边右移上移到上行行尾
	 */
	public boolean mMostLeftUp = false;
	
	/**
	 * true:支持最右边右移下移到下行行首
	 */
	public boolean mMostRightDown = true;
	
	/**
	 * true:聚焦显示聚焦view的时候不添加动画
	 */
	public boolean mNoAnimationInZero = false;
	
	public MGroupView(Context context) {
		super(context);
		
		init();
	}

	public MGroupView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public MGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
	}
	
	@Override
	public void setMFocus(boolean focused) {
		super.setMFocus(focused);

		if (null != mViewFocus) {
			if (mUseGlobalFocus) {
				if (focused) {
					onAttachFocusView();
				}

				IView itemView = getFocusedItemView();
				
				if (null != itemView) {
					focusItem(itemView, focused, getItemAttr(mFocused));
				}
			} else {
				IView itemView = getFocusedItemView();
				
				if (null != itemView) {
					focusItem(itemView, focused, getItemAttr(mFocused));
				}

				mViewFocus.setVisibility(focused ? VISIBLE : INVISIBLE);
			}
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mGainedViewportSize = true;
		mViewportWidth = Util.convertOut(w);
		mViewportHeight = Util.convertOut(h);
		
		onAttachItemView();
		onAttachFocusView();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (!hasMFocus()
				|| null==mAdapter
				|| mAdapter.getCount()<=0) {
			return false;
		}
		
		IView focusedView = getFocusedItemView();

		if (null!=focusedView && focusedView.dispatchKeyEvent(event)) {
			return true;
		}

		return onKeyCode(event);
	}
	
	/**
	 * 获取被选中的元素索引
	 * @return 被选中的元素索引
	 */
	public final int getFocusedIndex() {
		return mFocused;
	}
	
	/**
	 * 根据index获取ItemView
	 * @param index 索引
	 * @return 指定索引的ItemView，或null
	 */
	public final IView getItemViewByIndex(int index) {
		return mRecycler.getUsing(index);
	}

	public final void setAdapter(IAdapter adapter) {
		mAdapter = adapter;
		mRecycler.clear();
		mViewNormalPanel.removeAllViews();
		mViewFocusedPanel.removeAllViews();
		mFocused = mSelected = INVALIDATE_INDEX;
		
		onAttachItemView();
		onAttachFocusView();
	} 
	
	public void setAdapter(IAdapter adapter, int index) {
//		mNoAnimationInZero = true;
		mAdapter = adapter;
		mRecycler.clear();
		mViewNormalPanel.removeAllViews();
		mViewFocusedPanel.removeAllViews();

		mFocused = mSelected = index; 
		
		onAttachItemView();
		onAttachFocusView();
	}
	
	/**
	 * 设置内部聚焦框
	 * @param focusView 聚焦框
	 * @param attr 聚焦框的属性
	 */
	public final void setSingleFocusView(IView focusView, ViewWithShadowAttr attr) {
		if (null!=mViewFocus && !mUseGlobalFocus) {
			removeIView(mViewFocus);
		}

		mViewFocus = focusView;
		mAttrFocus = attr;
		mUseGlobalFocus = false;
		
		onAttachFocusView();
	}
	
	/**
	 * 设置全局聚焦框
	 * @param focusView 全局聚焦框
	 * @param attr 聚焦框属性
	 * @param offsetX GroupView与聚焦框所在容器的水平差值
	 * @param offsetY GroupView与聚焦框所在容器的垂直差值
	 */
	public final void setGlobalFocusView(IView focusView, ViewWithShadowAttr attr, 
			int offsetX, int offsetY) {
		if (null!=mViewFocus && !mUseGlobalFocus) {
			removeIView(mViewFocus);
		}
		
		mViewFocus = focusView;
		mAttrFocus = attr;
		mUseGlobalFocus = true;
		mGroupOffsetX = offsetX;
		mGroupOffsetY = offsetY;
		
		onAttachFocusView();
	}
	
	/**
	 * 设置元素视图的状态
	 * @param view
	 * @param focused
	 * @param itemAttr
	 */
	protected void focusItem(IView view, boolean focused, ViewWithShadowAttr itemAttr) {
		if (!itemAttr.hasFocusedScale) {
			return;
		}
		
		if (focused) {
			Anim.scaleFocused(view, 
					Util.convertIn(itemAttr.paddingLeft+itemAttr.scalePrivotX), 
					Util.convertIn(itemAttr.paddingTop+itemAttr.scalePrivotY));
		} else {
			Anim.scaleNormal(view);
		}
	}
	
	/**
	 * 设置跳过的索引集合，集合内索引不可被聚焦
	 * @param skipSet 索引集合
	 */
	public void setSkipSet(Set<Integer> skipSet) {
		mSetSkip.clear();
		mSetSkip.addAll(skipSet);
	}
	
	/**
	 * 设置支持最右边右移下移到下行行首
	 * @param mostRightDown
	 */
	public void setMostLeftUp(boolean mostLeftUp){
		mMostLeftUp = mostLeftUp;
	}
	
	/**
	 * 设置支持最右边右移下移到下行行首
	 * @param mostRightDown
	 */
	public void setMostRightDown(boolean mostRightDown){
		mMostRightDown = mostRightDown;
	}
	
	public abstract void setSelected(int index);
	
	public abstract void setSelected(int index, boolean ensureTop);
	
	protected abstract void animFocusView(IView view, LayoutParams params, boolean scaled);
	
	protected abstract void onAttachItemView();
	
	protected abstract void onAttachFocusView();
	
	protected abstract boolean onKeyCode(KeyEvent event);
	
	protected abstract IView getFocusedItemView();
	
	protected abstract ViewWithShadowAttr getItemAttr(int index);
	

	private void init() {
		mViewPanel = new MAbsoluteLayout(getContext());
		addIView(mViewPanel);
		mViewNormalPanel = new MAbsoluteLayout(getContext());
		mViewPanel.addIView(mViewNormalPanel, 
				new LayoutParams(LayoutParams.MATCH_PARENT, 
						LayoutParams.MATCH_PARENT, 
						0, 0));
		mViewFocusedPanel = new MAbsoluteLayout(getContext());
		mViewPanel.addIView(mViewFocusedPanel, 
				new LayoutParams(LayoutParams.MATCH_PARENT, 
						LayoutParams.MATCH_PARENT, 
						0, 0));
	}
}
