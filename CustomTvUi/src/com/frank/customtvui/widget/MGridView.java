package com.frank.customtvui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.utility.ui.IView;
import com.utility.ui.Util;
import com.utility.widget.ass.Anim;
import com.utility.widget.ass.GridViewAttr;
import com.utility.widget.ass.IAdapter;
import com.utility.widget.ass.ViewWithShadowAttr;

@SuppressLint("NewApi")
@SuppressWarnings({ "unused", "deprecation" })
public class MGridView extends MGroupView {
	
	public static interface OnFocusChangeListener {

		/**
		 * 焦点变化
		 * @param index 被选中的索引
		 * @param row 行索引，从0开始
		 * @param column 列索引，从0开始
		 */
		void onFocusChanged(int index, int row, int column);

	}
	
	public static interface OnKeyLongPressListener {
		
		/**
		 * 长按焦点变化
		 * @param event
		 */
		void OnKeyLongPress(int index, int row, int column,KeyEvent event);
	}
	
	/**
	 * GridView的属性
	 */
	private GridViewAttr mAttrGrid;
	/**
	 * 子元素的属性
	 */
	private ViewWithShadowAttr mAttrItem;
	
	/**
	 * Panel的高度
	 */
	private int mPanelHeight;
	
	/**
	 * Panel与GridView的垂直方向差值
	 */
	protected int mPanelOffsetY;
	
	/**
	 * 将获得焦点的元素索引
	 */
	private int mTargetFocus = INVALIDATE_INDEX;
	
	/**
	 * 上次导航按键时间
	 */
	private long mLastKeyTimeMillis;
	
	/**
	 * 焦点变化监听
	 */
	private OnFocusChangeListener mListenerOnFocusChanged;
	
	/**
	 * 长按焦点变化监听
	 */
	private OnKeyLongPressListener mListenerOnKeyLongPress;

	/**
	 * 动画结束再刷新数据
	 */
	private boolean mAnimStart;
	
	/**
	 * 聚焦框是否在顶部
	 */
	private boolean mTopFocus = true;
	
	public MGridView(Context context) {
		super(context);
		
		init();
	}

	public MGridView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public MGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
	}

	/**
	 * 设置GridView的属性
	 * @param gridViewAttr GridView的属性
	 * @param itemViewAttr	ItemView的属性
	 */
	public void setGridViewAttr(GridViewAttr gridViewAttr, 
			ViewWithShadowAttr itemViewAttr) {
		mAttrGrid = gridViewAttr;
		mAttrItem = itemViewAttr;
		
		onAttachItemView();
	}
	
	/**
	 * 设置焦点变化监听
	 * @param listener 焦点变化监听
	 */
	public void setOnFocusChangedListener(OnFocusChangeListener listener) {
		mListenerOnFocusChanged = listener;
	}
	
	/**
	 * 设置长按焦点变化监听
	 * @param listener
	 */
	public void setOnKeyLongPressListener(OnKeyLongPressListener listener){
		mListenerOnKeyLongPress = listener;
	}
	
	@Override
	public void setAdapter(IAdapter adapter, int index) {
	    if(index < adapter.getCount() && index >= 0){
            mTargetFocus = index;
        }else{
            mTargetFocus = 0;
        }
	    super.setAdapter(adapter,index);
    }
	
	@Override
	public void setSelected(int index) {
		mFocused = mSelected = index;

		if (mGainedViewportSize 
				&& null!=mAdapter && index<mAdapter.getCount() 
				&& null!=mAttrGrid && null!=mAttrItem) {
			mRecycler.recycleUsing();
			mViewNormalPanel.removeAllViews();
			mViewFocusedPanel.removeAllViews();

			int count = mAdapter.getCount();
			
			// 计算 mContentHeight
			int rows = count / mAttrGrid.columns;
			
			if (0 != count%mAttrGrid.columns) {
				++rows;
			}
			
			mPanelHeight = 
					mAttrGrid.paddingTop
					+rows*(mAttrItem.height+mAttrGrid.rowGap)-mAttrGrid.rowGap
					+mAttrGrid.paddingBottom;
			// 完成计算 mContentHeight
			
			LayoutParams itemParams = getItemViewLayoutParams(mFocused, 
					mAttrGrid, mAttrItem);
			
//			LayoutParams currentContentLayoutParams = (LayoutParams) mViewContent.getMLayoutParams();
//			int prevContentOffsetY = currentContentLayoutParams.y;
			mPanelOffsetY = generateOffset(itemParams, mAttrItem, mPanelOffsetY, 
					true, mPanelHeight, mViewportHeight, mAttrGrid);
			
//			if (prevContentOffsetY != mContentOffsetY) {
//				Anim.trans(mViewContent, 0, mContentOffsetY);
//			}
			
			// 设置 mViewContent
			mViewPanel.setMLayoutParams(
					new LayoutParams(
							mViewportWidth, 
							mPanelHeight, 
							0, 
							mPanelOffsetY));

			// 添加 itemView
			addItemView(mPanelOffsetY);
			

			if (null==mViewFocus || !hasMFocus()) {
				return;
			}

			LayoutParams focusParams = getFocusViewLayoutParams(
					itemParams, 
					mAttrGrid, mAttrItem, mAttrFocus, 
					mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY);
			animFocusView(mViewFocus, focusParams, true);
		}
	}
	
	public void setTopFocus(boolean focus){
		mTopFocus = focus;
	}
	
	public boolean getTopFocus(){
		return mTopFocus;  
		
	}
	
	public int getOffsetY(){
		return mPanelOffsetY;
	}
	
	public void setOffsetAdapter(IAdapter adapter,int offset,int index){
		
		setAdapter(adapter);
		
		mFocused = mSelected = index;

		if (mGainedViewportSize 
				&& null!=mAdapter && index<mAdapter.getCount() 
				&& null!=mAttrGrid && null!=mAttrItem) {
			mRecycler.recycleUsing();
			mViewNormalPanel.removeAllViews();
			mViewFocusedPanel.removeAllViews();

			int count = mAdapter.getCount();
			
			// 计算 mContentHeight
			int rows = count / mAttrGrid.columns;
			
			if (0 != count%mAttrGrid.columns) {
				++rows;
			}
			
			mPanelHeight = 
					mAttrGrid.paddingTop
					+rows*(mAttrItem.height+mAttrGrid.rowGap)-mAttrGrid.rowGap
					+mAttrGrid.paddingBottom;
			// 完成计算 mContentHeight
			
			LayoutParams itemParams = getItemViewLayoutParams(mFocused, 
					mAttrGrid, mAttrItem);
			
//			LayoutParams currentContentLayoutParams = (LayoutParams) mViewContent.getMLayoutParams();
//			int prevContentOffsetY = currentContentLayoutParams.y;
			mPanelOffsetY = offset;
			
//			if (prevContentOffsetY != mContentOffsetY) {
//				Anim.trans(mViewContent, 0, mContentOffsetY);
//			}
			
			// 设置 mViewContent
			mViewPanel.setMLayoutParams(
					new LayoutParams(
							mViewportWidth, 
							mPanelHeight, 
							0, 
							mPanelOffsetY));

			// 添加 itemView
			addItemView(mPanelOffsetY);
			

			if (null==mViewFocus || !hasMFocus()) {
				return;
			}

			LayoutParams focusParams = getFocusViewLayoutParams(
					itemParams, 
					mAttrGrid, mAttrItem, mAttrFocus, 
					mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY);
			animFocusView(mViewFocus, focusParams, true);
		}
	}
	
	public void setItemFocus(int index){
		int start = getFirstIndex(mPanelOffsetY, mAttrGrid, mAttrItem);
		int end = getLastIndex(mPanelOffsetY, mViewportHeight, mAttrGrid, mAttrItem);
		start = 0<=start ? start : 0;
		end = end<mAdapter.getCount() ? end : mAdapter.getCount();
		IView itemView;
		
		for (int i=start; i<end; ++i) {
			if (mRecycler.isUsing(i)) {
				itemView = mRecycler.getUsing(i);
				if (i == index) {
					focusItem(itemView, true, mAttrItem);
				} else {
					focusItem(itemView, false, mAttrItem);
				}
			}
		}
	}
	
	public void setMoveFocus(int keyCode , int moveIndex){
		
		int target = INVALIDATE_INDEX;
		boolean ensureTop = false;
		
		for(int i =0;i<moveIndex;i++ ){

			switch (keyCode) {
			
				case KeyEvent.KEYCODE_DPAD_UP:
					if (0 <= (mFocused-mAttrGrid.columns)) {
						target = mFocused - mAttrGrid.columns;
						ensureTop = true;
						
						while (mSetSkip.contains(target)) {
							--target;
						}
						
						if (target < 0) {
							target = INVALIDATE_INDEX;
						}
					break;
					}
				case KeyEvent.KEYCODE_DPAD_DOWN :
					if (0 < (mAdapter.getCount()-(mFocused+mAttrGrid.columns))) {
						target = mFocused + mAttrGrid.columns;
						
						while (mSetSkip.contains(target)) {
							++target;
						}
						
						target = target<mAdapter.getCount() ? target : INVALIDATE_INDEX;
					}else if(0 < mAdapter.getCount()%mAttrGrid.columns && (mFocused+mAdapter.getCount()%mAttrGrid.columns) < mAdapter.getCount()){
						target = mAdapter.getCount() -1;
						
						while (mSetSkip.contains(target)) {
							++target;
						}
						
						target = target<mAdapter.getCount() ? target : INVALIDATE_INDEX;
					}
					
					break;
				
			     }
				if (INVALIDATE_INDEX != target) {
//					if (System.currentTimeMillis()-mLastKeyTimeMillis < Anim.DURATION) {
//						return;
//					}

					mLastKeyTimeMillis = System.currentTimeMillis();
					IView itemView = mRecycler.getUsing(mFocused);
					if (null != itemView) {
						LayoutParams prevParams = (LayoutParams) itemView.getMLayoutParams();
						mViewNormalPanel.removeIView(itemView);
						mViewFocusedPanel.removeIView(itemView);
						mViewNormalPanel.addIView(itemView, prevParams);
						focusItem(itemView, false, mAttrItem);
						Anim.scaleNormal(itemView);
					}

					mFocused = target;
					itemView = mRecycler.getUsing(target);
					if (null != itemView) {
						mTargetFocus = INVALIDATE_INDEX;
						LayoutParams prevParams = (LayoutParams) itemView.getMLayoutParams();
						mViewNormalPanel.removeIView(itemView);
						mViewFocusedPanel.removeIView(itemView);
						mViewFocusedPanel.addIView(itemView, prevParams);
						focusItem(itemView, true, mAttrItem);
					} else {
						mTargetFocus = target;
					}
					
					LayoutParams itemParams = getItemViewLayoutParams(mFocused, 
							mAttrGrid, mAttrItem);
					
					LayoutParams currentContentLayoutParams = (LayoutParams) mViewPanel.getMLayoutParams();
					int prevContentOffsetY = currentContentLayoutParams.y;
					mPanelOffsetY = ensureOffset(itemParams, mAttrItem, mPanelOffsetY, 
							ensureTop, mPanelHeight, mViewportHeight, mAttrGrid);
					
					if (prevContentOffsetY != mPanelOffsetY) {
						Anim.trans(mViewPanel, 0, mPanelOffsetY);
					}

					LayoutParams focusParams = getFocusViewLayoutParams(
							itemParams, 
							mAttrGrid, mAttrItem, mAttrFocus, 
							mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY);
					animFocusView(mViewFocus, focusParams, true);
					
				}
		  }
		
	}
	
	
	@Override
	protected void onAttachItemView() {
		if (mGainedViewportSize 
				&& null!=mAdapter && 0<mAdapter.getCount()
				&& null!=mAttrGrid && null!=mAttrItem) {
			if (INVALIDATE_INDEX!=mFocused && mFocused<mAdapter.getCount()) {
			} else {
				mFocused = mSelected = 0;
			}

			int count = mAdapter.getCount();
			
			// 计算 mContentHeight
			int rows = count / mAttrGrid.columns;
			
			if (0 != count%mAttrGrid.columns) {
				++rows;
			}
			
			mPanelHeight = 
					mAttrGrid.paddingTop
					+rows*(mAttrItem.height+mAttrGrid.rowGap)-mAttrGrid.rowGap
					+mAttrGrid.paddingBottom;
			// 完成计算 mContentHeight
			
			LayoutParams itemParams = getItemViewLayoutParams(mFocused, 
					mAttrGrid, mAttrItem);
			
//			LayoutParams currentContentLayoutParams = (LayoutParams) mViewContent.getMLayoutParams();
//			int prevContentOffsetY = currentContentLayoutParams.y;
			mPanelOffsetY = ensureOffset(itemParams, mAttrItem, mPanelOffsetY, 
					true, mPanelHeight, mViewportHeight, mAttrGrid);
			
//			if (prevContentOffsetY != mContentOffsetY) {
//				Anim.trans(mViewContent, 0, mContentOffsetY);
//			}
			
			// 设置 mViewContent
			mViewPanel.setMLayoutParams(
					new LayoutParams(
							mViewportWidth, 
							mPanelHeight, 
							0, 
							mPanelOffsetY));

			// 添加 itemView
			addItemView(mPanelOffsetY);
		}
		
		invalidate();
	}

	@Override
	protected void onAttachFocusView() {
		if (mGainedViewportSize 
				&& null!=mViewFocus && null!=mAttrFocus
				&& null!=mAdapter && 0<mAdapter.getCount()
				&& null!=mAttrGrid && null!=mAttrItem) {
			if (mUseGlobalFocus) {
				if (hasMFocus()) {
					if(/*mFocused == 0 && */mNoAnimationInZero){
//						设置mViewFocus属性的时候去掉动画
						mViewFocus.setMLayoutParams(getFocusViewLayoutParams(
								getItemViewLayoutParams(mFocused, 
										mAttrGrid, mAttrItem), 
										mAttrGrid, mAttrItem, mAttrFocus, 
										mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY));
						
						mNoAnimationInZero = false;
					}else
					{
						
						animFocusView(mViewFocus, getFocusViewLayoutParams(
								getItemViewLayoutParams(mFocused, 
										mAttrGrid, mAttrItem), 
										mAttrGrid, mAttrItem, mAttrFocus, 
										mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY), 
										true);
					}
				}
			} else {
				addIView(mViewFocus, getFocusViewLayoutParams(
						getItemViewLayoutParams(mFocused, 
								mAttrGrid, mAttrItem), 
						mAttrGrid, mAttrItem, mAttrFocus, 
						mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY));
				
				mViewFocus.setVisibility(hasMFocus() ? VISIBLE : INVISIBLE);
			}
		}
	}
	
	@Override
	protected boolean onKeyCode(KeyEvent event) {
		int action = event.getAction();
		
		if (KeyEvent.ACTION_DOWN != action) {
			return false;
		}
		
		int target = INVALIDATE_INDEX;
		boolean ensureTop = false;

		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(mMostLeftUp){
				target = mFocused - 1;
				
				while (mSetSkip.contains(target)) {
					--target;
				}
				
				if (target < 0) {
					target = INVALIDATE_INDEX;
				}
			}else if (0 != (mFocused%mAttrGrid.columns)) {
				target = mFocused - 1;
				
				while (mSetSkip.contains(target)) {
					--target;
				}
				
				if (target < 0) {
					target = INVALIDATE_INDEX;
				}
			}

			break;

		case KeyEvent.KEYCODE_DPAD_UP :
			if (0 <= (mFocused-mAttrGrid.columns)) {
				target = mFocused - mAttrGrid.columns;
				ensureTop = true;
				mTopFocus = true;
				
				while (mSetSkip.contains(target)) {
					--target;
				}
				
				if (target < 0) {
					target = INVALIDATE_INDEX;
				}
			}

			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT :
			if(mMostRightDown){
				target = mFocused + 1;
				
				while (mSetSkip.contains(target)) {
					++target;
				}
				
				target = target<mAdapter.getCount() ? target : INVALIDATE_INDEX;
			}else if ((mAttrGrid.columns-1) != (mFocused%mAttrGrid.columns)) {
				target = mFocused + 1;
				
				while (mSetSkip.contains(target)) {
					++target;
				}
				
				target = target<mAdapter.getCount() ? target : INVALIDATE_INDEX;
			}

			break;

		case KeyEvent.KEYCODE_DPAD_DOWN :
			if (0 < (mAdapter.getCount()-(mFocused+mAttrGrid.columns))) {
				target = mFocused + mAttrGrid.columns;
				
				while (mSetSkip.contains(target)) {
					++target;
				}
				
				target = target<mAdapter.getCount() ? target : INVALIDATE_INDEX;
			}else if(0 < mAdapter.getCount()%mAttrGrid.columns && (mFocused+mAdapter.getCount()%mAttrGrid.columns) < mAdapter.getCount()){
				target = mAdapter.getCount() -1;
				
				while (mSetSkip.contains(target)) {
					++target;
				}
				
				target = target<mAdapter.getCount() ? target : INVALIDATE_INDEX;
			}
			mTopFocus = false;
			break;
		}
		
		if (INVALIDATE_INDEX != target) {
//			if (System.currentTimeMillis()-mLastKeyTimeMillis < Anim.DURATION) {
//				return true;
//			}
//			mLastKeyTimeMillis = System.currentTimeMillis();
			
			IView itemView = mRecycler.getUsing(mFocused);
			if (null != itemView) {
				LayoutParams prevParams = (LayoutParams) itemView.getMLayoutParams();
				mViewNormalPanel.removeIView(itemView);
				mViewFocusedPanel.removeIView(itemView);
				mViewNormalPanel.addIView(itemView, prevParams);
				focusItem(itemView, false, mAttrItem);
//				Anim.scaleNormal(itemView);
			}

			mFocused = target;
			itemView = mRecycler.getUsing(target);
			if (null != itemView) {
				
				mTargetFocus = INVALIDATE_INDEX;
				LayoutParams prevParams = (LayoutParams) itemView.getMLayoutParams();
				mViewNormalPanel.removeIView(itemView);
				mViewFocusedPanel.removeIView(itemView);
				mViewFocusedPanel.addIView(itemView, prevParams);
				focusItem(itemView, true, mAttrItem);
			} else {
				mTargetFocus = target;
				OnKeyFocus(true, event);
			}
			
			LayoutParams itemParams = getItemViewLayoutParams(mFocused, mAttrGrid, mAttrItem);
			
			LayoutParams currentContentLayoutParams = (LayoutParams) mViewPanel.getMLayoutParams();
			int prevContentOffsetY = currentContentLayoutParams.y;
			mPanelOffsetY = ensureOffset(itemParams, mAttrItem, mPanelOffsetY, 
					ensureTop, mPanelHeight, mViewportHeight, mAttrGrid);
			
			if (prevContentOffsetY != mPanelOffsetY) {
				Anim.trans(mViewPanel, 0, mPanelOffsetY);
			}

			LayoutParams focusParams = getFocusViewLayoutParams(
					itemParams, 
					mAttrGrid, mAttrItem, mAttrFocus, 
					mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY);
			animFocusView(mViewFocus, focusParams, true);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void animFocusView(IView view, LayoutParams params, boolean scaled) {
		if (scaled) {
			Anim.transAndScale(view, params);
		} else {
			Anim.trans(view, params);
		}
	}

	@Override
	protected IView getFocusedItemView() {
		return mRecycler.getUsing(mFocused);
	}

	@Override
	protected ViewWithShadowAttr getItemAttr(int index) {
		return mAttrItem;
	}

	@Override
	protected void focusItem(IView view, boolean focused, ViewWithShadowAttr itemAttr) {
		super.focusItem(view, focused, itemAttr);
		
		view.setMFocus(focused);

		if (focused && null!=mListenerOnFocusChanged) {
			mListenerOnFocusChanged.onFocusChanged(mFocused, mFocused/mAttrGrid.columns, mFocused%mAttrGrid.columns);
		}
	}
	
	/**
	 * 长按事件监听触发
	 * @param focused
	 * @param event
	 */
	private void OnKeyFocus(boolean focused, KeyEvent event){
		if (focused /*&& event.getRepeatCount() > 0 */&& null != mListenerOnKeyLongPress && mAttrGrid != null) {
			mListenerOnKeyLongPress.OnKeyLongPress(mFocused, mFocused/mAttrGrid.columns, mFocused%mAttrGrid.columns, event);
		}
	}

	/**
	 * add ItemView
	 * @param offsetY vertical offset between Panel and GridView
	 */
	protected void addItemView(int offsetY) {
		int start = getFirstIndex(offsetY, mAttrGrid, mAttrItem);
		int end = getLastIndex(offsetY, mViewportHeight, mAttrGrid, mAttrItem);
		
		start = 0<=start ? start : 0;
		end = end<mAdapter.getCount() ? end : mAdapter.getCount();
		IView itemView = null;
		
		
		for (int i=start; i<end; ++i) {
			if (mRecycler.isUsing(i)) {
				continue;
			}
			
			itemView = mAdapter.getView(i, mRecycler.getFree());
			mRecycler.putUsing(i, itemView);
			
			if (i == mTargetFocus && hasMFocus()) {
				mViewFocusedPanel.addIView(itemView, getItemViewLayoutParams(i, mAttrGrid, mAttrItem));
//				mTargetFocus = INVALIDATE_INDEX;
				focusItem(itemView, true, mAttrItem);
			} else {
				mViewNormalPanel.addIView(itemView, getItemViewLayoutParams(i, mAttrGrid, mAttrItem));
				focusItem(itemView, false, mAttrItem);
			}
		}
	} 

	/**
	 * remove ItemView
	 * @param offsetY vertical offset between Panel and GridView
	 */
	protected void removeItemView(int offsetY) {
		int start = getFirstIndex(offsetY, mAttrGrid, mAttrItem);
		int end = getLastIndex(offsetY, mViewportHeight, mAttrGrid, mAttrItem);
		
		start = 0<=start ? start : 0;
		int count = mAdapter.getCount();
		end = end<count ? end : count;
		IView itemView;
		
		for (Integer index : mRecycler.getUsingKeySet()) {
			if (index<start || end<index) {
				if (mRecycler.isUsing(index)) {
					mViewNormalPanel.removeIView(mRecycler.getUsing(index));
					mViewFocusedPanel.removeIView(mRecycler.getUsing(index));
					mRecycler.changeFree(index);
				}
			}
		}
	}
	
	private void init() {
		mViewPanel.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (mGainedViewportSize && null!=mAdapter && 0<mAdapter.getCount()) {
					int offsetY = Util.convertOut(top);
					removeItemView(offsetY);
					addItemView(offsetY);
				}
			}
		});
	}

	/**
	 * get FocusView LayoutParams
	 * @param itemParams ItemView LayoutParams
	 * @param gridAttr GridView Attribute
	 * @param itemAttr ItemView Attribute
	 * @param focusAttr FocusView Attribute
	 * @param globalFocusView whether using global FocusView
	 * @param offsetX horizontal offset between GridView and global FocusView's Parent
	 * @param offsetY vertical offset between GridView and global FocusView's Parent
	 * @param panelOffsetY vertical offset between Panel and GridView
	 * @return FocusView LayoutParams
	 */
	private static LayoutParams getFocusViewLayoutParams(
			LayoutParams itemParams, 
			GridViewAttr gridAttr, ViewWithShadowAttr itemAttr, 
			ViewWithShadowAttr focusAttr, 
			boolean globalFocusView, int offsetX, int offsetY, int panelOffsetY) {
		LayoutParams rslt = new LayoutParams(itemParams.width, itemParams.height, 
				itemParams.x, itemParams.y);
		if (itemAttr.hasFocusedScale) {
			rslt.width = (int) (itemAttr.width*itemAttr.scaleX 
					+ focusAttr.paddingLeft + focusAttr.paddingRight) 
					+ 0;
			rslt.height = (int) (itemAttr.height*itemAttr.scaleY 
					+ focusAttr.paddingTop + focusAttr.paddingBottom) 
					+ 0;
			rslt.x = (int) (rslt.x 
					+ (itemAttr.paddingLeft*itemAttr.scaleX - focusAttr.paddingLeft) 
					- (itemAttr.scalePrivotX+itemAttr.paddingLeft)*(itemAttr.scaleX-1) 
					- 0);
			rslt.y = (int) (rslt.y 
					+ (itemAttr.paddingTop*itemAttr.scaleY - focusAttr.paddingTop) 
					- (itemAttr.scalePrivotY+itemAttr.paddingTop)*(itemAttr.scaleY-1) 
					- 0);
		} else {
			rslt.width = itemAttr.width + focusAttr.paddingLeft + focusAttr.paddingRight;
			rslt.height = itemAttr.height + focusAttr.paddingTop + focusAttr.paddingBottom;
			rslt.x += (itemAttr.paddingLeft - focusAttr.paddingLeft);
			rslt.y += (itemAttr.paddingTop - focusAttr.paddingTop);
		}
		
		if (globalFocusView) {
			rslt.x += offsetX;
			rslt.y += offsetY + panelOffsetY;
		} else {
			rslt.y += panelOffsetY;
		}

		return rslt;
	}

	/**
	 * get ItemView LayoutParams
	 * @param index ItemView index
	 * @param gridAttr GridView Attribute
	 * @param itemAttr ItemView Attribute
	 * @return ItemView LayoutParams
	 */
	private static LayoutParams getItemViewLayoutParams( int index, 
			GridViewAttr gridAttr, ViewWithShadowAttr itemAttr) {
		int width = itemAttr.width + itemAttr.paddingLeft + itemAttr.paddingRight;
		int height = itemAttr.height + itemAttr.paddingTop + itemAttr.paddingBottom;

		int row = index / gridAttr.columns;
		int column = index % gridAttr.columns;
		
		int x = gridAttr.paddingLeft 
				+ column*(itemAttr.width+gridAttr.columnGap)
				- itemAttr.paddingLeft;
		
		int y = gridAttr.paddingTop 
				+ row*(itemAttr.height+gridAttr.rowGap)
				- itemAttr.paddingTop;
		
		LayoutParams rslt = new LayoutParams(width, height, x, y);

		return rslt;
	}
	
	/**
	 * ensure vertical offset between Panel and GridView
	 * @param itemParams ItemView LayoutParams
	 * @param itemAttr ItemView Attribute
	 * @param offsetY vertical offset between Panel and GridView
	 * @param ensureTop ensure top or ensure bottom safe line
	 * @param panelHeight Panel height
	 * @param viewportHeight GridView height
	 * @param gridAttr GridView Attribute
	 * @return vertical offset between Panel and GridView
	 */
	private static int ensureOffset(
			LayoutParams itemParams, ViewWithShadowAttr itemAttr, 
			int offsetY, boolean ensureTop, int panelHeight, 
			int viewportHeight, GridViewAttr gridAttr) {
		int top = itemParams.y + itemAttr.paddingTop;
		int bottom = top + itemAttr.height;
		int rslt;
		
		if (ensureTop) {
			if (viewportHeight-gridAttr.paddingBottom < bottom+offsetY) {
				offsetY = viewportHeight-gridAttr.paddingBottom-bottom;
			}

			if (gridAttr.paddingTop <= (top+offsetY)) {
				rslt = offsetY;
			} else {
				rslt = gridAttr.paddingTop - top;
			}
		} else {
			if (top+offsetY < gridAttr.paddingTop) {
				offsetY = gridAttr.paddingTop - top;
			}

			if ((bottom+offsetY) <= (viewportHeight-gridAttr.paddingBottom)) {
				rslt = offsetY;
			} else {
				rslt = viewportHeight - gridAttr.paddingBottom - bottom;
			}
		}
		
		if (panelHeight+rslt < viewportHeight) {
			rslt = viewportHeight - panelHeight;
		}
		
		if (0 < rslt) {
			rslt = 0;
		}
		
		return rslt;
	}
	
	
	/**
	 * ensure vertical offset between Panel and GridView
	 * @param itemParams ItemView LayoutParams
	 * @param itemAttr ItemView Attribute
	 * @param offsetY vertical offset between Panel and GridView
	 * @param ensureTop ensure top or ensure bottom safe line
	 * @param panelHeight Panel height
	 * @param viewportHeight GridView height
	 * @param gridAttr GridView Attribute
	 * @return vertical offset between Panel and GridView
	 */
	private static int generateOffset(
			LayoutParams itemParams, ViewWithShadowAttr itemAttr, 
			int offsetY, boolean ensureTop, int panelHeight, 
			int viewportHeight, GridViewAttr gridAttr) {
		int top = itemParams.y + itemAttr.paddingTop;
		int bottom = top + itemAttr.height;

		return gridAttr.paddingTop - top;
	}
	
	/**
	 * get first ItemView index
	 * @param offsetY vertical offset between Panel and GridView
	 * @param gridAttr GridView Attribute
	 * @param itemAttr ItemView Attribute
	 * @return ItemView index
	 */
	private static int getFirstIndex(int offsetY, 
			GridViewAttr gridAttr, ViewWithShadowAttr itemAttr) {
		int row = 
		(-offsetY - itemAttr.height - itemAttr.paddingBottom - gridAttr.paddingTop)
		/(itemAttr.height+gridAttr.rowGap);

		return row*gridAttr.columns;
	}
	
	/**
	 * get last ItemView index
	 * @param offsetY vertical offset between Panel and GridView
	 * @param panelHeight Panel height
	 * @param gridAttr GridView Attribute
	 * @param itemAttr ItemView Attribute
	 * @return ItemView index
	 */
	private static int getLastIndex(int offsetY, int panelHeight, 
			GridViewAttr gridAttr, ViewWithShadowAttr itemAttr) {
		int row = 
		(-offsetY + panelHeight + itemAttr.paddingTop - gridAttr.paddingTop)
		/(itemAttr.height+gridAttr.rowGap);
		
		return (row+1)*gridAttr.columns;
	}
	
	/**
	 * ensureTop 置顶聚焦itemView
	 */
	@Override
	public void setSelected(int index, boolean ensureTop) {
		// TODO Auto-generated method stub
		mNoAnimationInZero = true;
		mFocused = mSelected = index;
		
		int start = getFirstIndex(mPanelOffsetY, mAttrGrid, mAttrItem);
		int end = getLastIndex(mPanelOffsetY, mViewportHeight, mAttrGrid, mAttrItem);
		start = 0<=start ? start : 0;
		end = end<mAdapter.getCount() ? end : mAdapter.getCount();
		boolean mHas = false;
		for(int i=start; i<end; i++){
			if(mFocused == i){
				mHas = true;
				break;
			}
		}
		
		if(mHas){
			setMFocus(true);
		}
		else if (mGainedViewportSize 
				&& null!=mAdapter && index<mAdapter.getCount() 
				&& null!=mAttrGrid && null!=mAttrItem) {
			mRecycler.recycleUsing();
			mViewNormalPanel.removeAllViews();
			mViewFocusedPanel.removeAllViews();

			int count = mAdapter.getCount();
			
			// 计算 mContentHeight
			int rows = count / mAttrGrid.columns;
			
			if (0 != count%mAttrGrid.columns) {
				++rows;
			}
			
			mPanelHeight = 
					mAttrGrid.paddingTop
					+rows*(mAttrItem.height+mAttrGrid.rowGap)-mAttrGrid.rowGap
					+mAttrGrid.paddingBottom;
			// 完成计算 mContentHeight
			
			LayoutParams itemParams = getItemViewLayoutParams(mFocused, 
					mAttrGrid, mAttrItem);
			
			//焦点定位上移到顶部		
			if(ensureTop){
				LayoutParams currentContentLayoutParams = (LayoutParams) mViewPanel.getMLayoutParams();
				int prevContentOffsetY = currentContentLayoutParams.y;
				mPanelOffsetY = generateOffset(itemParams, mAttrItem, mPanelOffsetY, 
						true, mPanelHeight, mViewportHeight, mAttrGrid);
				
				
				if (prevContentOffsetY != mPanelOffsetY) {
					Anim.trans(mViewPanel, 0, mPanelOffsetY);
				}
			}
			
			// 添加 itemView
			addItemView(mPanelOffsetY);
			
			if (null==mViewFocus || !hasMFocus()) {
				return;
			}

			LayoutParams focusParams = getFocusViewLayoutParams(
					itemParams, 
					mAttrGrid, mAttrItem, mAttrFocus, 
					mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY);

			animFocusView(mViewFocus, focusParams, true);
		}
	}

	/**
	 * 聚焦的itemView不在显示区域上移到显示区
	 * @param index
	 */
	public void setSelect(int index) {
		if (mGainedViewportSize && null!=mAdapter && index<mAdapter.getCount() && null!=mAttrGrid && null!=mAttrItem) {
			int pre = 0;
			mNoAnimationInZero = true;
			if(index < mAdapter.getCount() && index >= 0){
				pre = mFocused;
				mTargetFocus = mFocused = mSelected = index;
			}else{
				mTargetFocus = mFocused = mSelected = 0;
			}

			int count = mAdapter.getCount();
			// 计算 mContentHeight
			int rows = count / mAttrGrid.columns;
			
			if (0 != count%mAttrGrid.columns) {
				++rows;
			}
			
			mPanelHeight = mAttrGrid.paddingTop+rows*(mAttrItem.height+mAttrGrid.rowGap)-mAttrGrid.rowGap+mAttrGrid.paddingBottom;
			// 完成计算 mContentHeight
			
			LayoutParams itemParams = getItemViewLayoutParams(mFocused, mAttrGrid, mAttrItem);
		
			LayoutParams currentContentLayoutParams = (LayoutParams) mViewPanel.getMLayoutParams();
			int prevContentOffsetY = currentContentLayoutParams.y;
			
			mPanelOffsetY = ensureOffset(itemParams, mAttrItem, mPanelOffsetY, 
					false, mPanelHeight, mViewportHeight, mAttrGrid);				

			int start = getFirstIndex(mPanelOffsetY, mAttrGrid, mAttrItem);
			int end = getLastIndex(mPanelOffsetY, mViewportHeight, mAttrGrid, mAttrItem);
			start = 0<=start ? start : 0;
			end = end<mAdapter.getCount() ? end : mAdapter.getCount();
			boolean mHas = false;
			for(int i=start; i<end; i++){
				if(mFocused == i){
					mHas = true;
				}else{
					IView itemView = mRecycler.getUsing(i);
					if (null != itemView) {
						focusItem(itemView, false, mAttrItem);
					}
				}
			}
			
			if (prevContentOffsetY != mPanelOffsetY) {
				if(hasMFocus()){
					currentContentLayoutParams.y = mPanelOffsetY;
					mViewPanel.setMLayoutParams(currentContentLayoutParams);
					
				}else{
					Anim.trans(mViewPanel, 0, mPanelOffsetY);
				}
			}
			
			// 添加 itemView
			if(!mHas){
				mRecycler.recycleUsing();
				mViewNormalPanel.removeAllViews();
				mViewFocusedPanel.removeAllViews();
				addItemView(mPanelOffsetY);
			}else{
				setMFocus(true);
			}
			
			if(null != mViewFocus){
				LayoutParams focusParams = getFocusViewLayoutParams(itemParams,	mAttrGrid, mAttrItem, mAttrFocus, 
						mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY);
				if(hasMFocus()){
					mViewFocus.setMLayoutParams(focusParams);
				}else{
					animFocusView(mViewFocus, focusParams, true);
				}
			}
		}
	}
	
	/**
	 * 聚焦到偏移显示区域的位置
	 * @param index
	 * @param x
	 * @param y
	 */
	public void setSelect(int index, int x, int y){
		if(y == 0){
		}else{
			mPanelOffsetY = y;
		}

		setSelect(index);
	}
	
	public void setSelected(int index, boolean ensureTop, boolean topFocus) {
		mTopFocus = topFocus;
		mNoAnimationInZero = true;
		mFocused = mSelected = index;

		if (mGainedViewportSize 
				&& null!=mAdapter && index<mAdapter.getCount() 
				&& null!=mAttrGrid && null!=mAttrItem) {
			mRecycler.recycleUsing();
			mViewNormalPanel.removeAllViews();
			mViewFocusedPanel.removeAllViews();

			int count = mAdapter.getCount();
			
			// 计算 mContentHeight
			int rows = count / mAttrGrid.columns;
			
			if (0 != count%mAttrGrid.columns) {
				++rows;
			}
			
			mPanelHeight = 
					mAttrGrid.paddingTop
					+rows*(mAttrItem.height+mAttrGrid.rowGap)-mAttrGrid.rowGap
					+mAttrGrid.paddingBottom;
			// 完成计算 mContentHeight
			
			LayoutParams itemParams = getItemViewLayoutParams(mFocused, 
					mAttrGrid, mAttrItem);
			
			//焦点定位上移到顶部		
			if(ensureTop){
				LayoutParams currentContentLayoutParams = (LayoutParams) mViewPanel.getMLayoutParams();
				int prevContentOffsetY = currentContentLayoutParams.y;
				mPanelOffsetY = generateOffset(itemParams, mAttrItem, mPanelOffsetY, 
						true, mPanelHeight, mViewportHeight, mAttrGrid);
				
				if(!topFocus){
					mPanelOffsetY += mAttrItem.height+mAttrGrid.rowGap;
				}
				
				if (prevContentOffsetY != mPanelOffsetY) {
					Anim.trans(mViewPanel, 0, mPanelOffsetY);
				}
			}
			
			// 添加 itemView
			addItemView(mPanelOffsetY);
			
			if (null==mViewFocus || !hasMFocus()) {
				return;
			}

			LayoutParams focusParams = getFocusViewLayoutParams(
					itemParams, 
					mAttrGrid, mAttrItem, mAttrFocus, 
					mUseGlobalFocus, mGroupOffsetX, mGroupOffsetY, mPanelOffsetY);

			animFocusView(mViewFocus, focusParams, true);
		}
	
	}
	
	/**
	 * notify data to update
	 */
	public void notifyDataSetChanged(){
		
		LayoutParams itemParams = getItemViewLayoutParams(mFocused, 
				mAttrGrid, mAttrItem);
		mPanelOffsetY = ensureOffset(itemParams, mAttrItem, mPanelOffsetY, 
				true, mPanelHeight, mViewportHeight, mAttrGrid);
		
		int start = getFirstIndex(mPanelOffsetY, mAttrGrid, mAttrItem);
		int end = getLastIndex(mPanelOffsetY, mViewportHeight, mAttrGrid, mAttrItem);
		start = 0<=start ? start : 0;
		end = end<mAdapter.getCount() ? end : mAdapter.getCount();
		
		IView itemView;
		for (int i=start; i<end; ++i) {
			if (mRecycler.isUsing(i)) {
				itemView = mAdapter.getView(i, mRecycler.getUsing(i));
			}
		}
	}

	public boolean hasEmptyData(){
		LayoutParams itemParams = getItemViewLayoutParams(mFocused, 
				mAttrGrid, mAttrItem);
		mPanelOffsetY = ensureOffset(itemParams, mAttrItem, mPanelOffsetY, 
				true, mPanelHeight, mViewportHeight, mAttrGrid);
		
		int start = getFirstIndex(mPanelOffsetY, mAttrGrid, mAttrItem);
		int end = getLastIndex(mPanelOffsetY, mViewportHeight, mAttrGrid, mAttrItem);
		start = 0<=start ? start : 0;
		end = end<mAdapter.getCount() ? end : mAdapter.getCount();
		
		IView itemView;
		for (int i=start; i<end; ++i) {
			if (mRecycler.isUsing(i)) {
				return true;
			}
		}
		return false;
	}
}
