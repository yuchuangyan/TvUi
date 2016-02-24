package com.frank.customtvui.listview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ListAdapter;

import com.utility.ui.IView;
import com.utility.ui.MAbsoluteLayout;
import com.utility.ui.commonCtrl.flash.IFlashListener;
import com.utility.widget.ass.Anim;

public class CustomListView extends MAbsoluteLayout{

	private ListAdapter mAdapter;
    // 回收站
    private static final RecycleBin mRecycler = new RecycleBin();
    //左边的保护padding值
    private int mLeftPaddding = 60;
    // 下边距溢出范围
    private int mBottomOffSet = 20;
    
    //所有View左边距
    private int mLeftOffSet = 0;
    
    // 当前选中位置
    private int mSelectedPosition = 0;
    
    // adapter 监听
    private InnerDataSetObserver mDataSetObserver = new InnerDataSetObserver();

    // 控件总长度
    private int mFillInWidth = 0;
    
    //每个position控件的宽度
    private int mChildWidth = 600;
    
    //当前控件中所有的子控件
    private Map<Integer, View> mChildsMap = new HashMap<Integer, View>();
    
    //当前聚焦的view
    private View mCurrentFocusView;
    //当前聚焦的Item的导航code
    
    //当前ListView的上
    private int mLocationY = 0;
    
    //default
    private int mTranslateX = 0;
    
    //isFocus
    private boolean mFocus = true;
    
    //动画时长
    private int mDuration = 300;
    
  	//内存回收
  	private List<Integer> mDeleteKey = new ArrayList<Integer>();
  	
  	public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public CustomListView(Context context) {
		super(context);
		initView();
	}
	
	
	private void initView(){
		removeAllViewsInLayout();
		setBackgroundColor(Color.TRANSPARENT);	//设置透明色
        setPadding(0, 0, 0, 0);	//设置padding值
        setClipChildren(false);
        Anim.setDuration(mDuration);
	}
	
	private class InnerDataSetObserver extends DataSetObserver {
        //data isChanged ?
    	boolean dataChanged;
        @Override
        public void onChanged() {
        	mChildsMap.clear();
            dataChanged = true;
            removeAllViewsInLayout();
    		layoutChildFromPosition(mSelectedPosition);
        }

        @Override
        public void onInvalidated() {
            // nothing
        }

        public boolean isDataChanged() {
            boolean changed = dataChanged;
            dataChanged = false;
            return changed;
        }

        public void onEmpty() {
        	mFillInWidth = 0;
            dataChanged = true;
            removeAllViewsInLayout();
        }
    }
	
	/**
	 * 获取当前聚焦的Item的导航code
	 */
	public int getSelectView(){
		return mSelectedPosition;
	}
	
	/**
	 * 获取第position个child距离上边距的Y值
	 * eg:position = 3时，返回的值为前面两个position之和
	 * @param position
	 * @return
	 */
    public int	getParamsYToPos(int position){
    	if(position>=getCount()){
    		position = getCount();
    	}
    	return mChildWidth*position;
    }
    
    /**
     * 设置ListView控件的整个宽度
     */
    public void setChildsWidth(int childWidth){
    	mChildWidth = childWidth;
    	mFillInWidth = childWidth*getCount();
    	mFillInWidth += mLeftPaddding+mBottomOffSet;
    	setMeasuredDimension(mFillInWidth, getMeasuredHeight());	//设置AbsoluteLayout的高度
    }
    
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        mRecycler.clear();
        if (mAdapter == null || mAdapter.isEmpty()) {
            mDataSetObserver.onEmpty();
        } else {
            mAdapter.registerDataSetObserver(mDataSetObserver);
            mDataSetObserver.onChanged();
            mSelectedPosition = 0;
        }
    }
    
    /**
     * 设置ListView在父控件的左边距
     * @param x
     */
    public void setLocationX(int x){
    	mLocationY = x;
    }

    public ListAdapter getAdapter() {
        return mAdapter;
    }
    
    /**
     * 获取当前List里的Item个数
     * @return
     */
    public int getCount() {
    	if(mAdapter != null){
    		return mAdapter.getCount();
    	}
    	return 0;
    }

    /**
     * 此方法只能供setSelection使用，别的地方不能调用此方法
     * @return
     */
    private View getSelectedView() {
    	mCurrentFocusView = mChildsMap.get(mSelectedPosition);
        return mCurrentFocusView;
    }
    
    //**get当前的选中的Item
    public int getSelectedItemPosition() {
        return mSelectedPosition;
    }
    
    /**
     * 获取当前选中的是否为最后一个Item
     */
    public boolean getSelectedItemIsLast(){
    	if(mSelectedPosition >= mAdapter.getCount()-1){
    		return true;
    	}else{
    		return false;
    	}
    }

    //***设置当前选中的position
    public void setSelection(int position){
    	layoutChildFromPosition(position);
    	mSelectedPosition = position;
        mCurrentFocusView = getSelectedView();
    }
    
    /**
     * 滑动选中某一个position
     * @param position
     */
    public void setSelectionSmoothy(int position){
    	layoutChildFromPosition(position);
    	mSelectedPosition = position;
        mCurrentFocusView = getSelectedView();
        mTranslateX = - getParamsYToPos(mSelectedPosition);
        //滑动聚焦范围不能超过屏幕的上保护距离 0+mTopOffSet为上边界距	
        if(mTranslateX+mLeftPaddding >= 0){		
        	mTranslateX = 0;
        }
        if(mTranslateX + mFillInWidth <= getHeight() - mBottomOffSet){
        	mTranslateX = getHeight()-mFillInWidth - mBottomOffSet;
        }
        Anim.trans(((IView)CustomListView.this), mTranslateX, mLocationY, mFlashListener);
    }
    
    /**
     * 设置焦点
     * @param focus
     */
    public void setFocus(boolean focus){
    	mFocus = focus;
    	if(mFocus){
    		setSelection(mSelectedPosition);
    	}else{
    	}
    }
    
    /**
     * 获取是否有焦点
     * @return
     */
    public boolean getFocus(){
    	return mFocus;
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	if(event.getAction() != KeyEvent.ACTION_DOWN){
    		return true;
    	}
    	if(mCurrentFocusView != null && mCurrentFocusView.dispatchKeyEvent(event)){
    		return true;
    	}
    	switch(event.getKeyCode()){
    	case KeyEvent.KEYCODE_DPAD_LEFT:
    		moveLeft();
    		return true;
    	case KeyEvent.KEYCODE_DPAD_RIGHT:
    		moveRight();
    		return true;
    	}
    	return false;
    }
    
    // 焦点下移一个
    private boolean moveRight() {
        int selection = mSelectedPosition;
        selection++;
        if (selection >= getCount()) {
            return false;
        }
        setSelection(selection);
        mTranslateX = - getParamsYToPos(selection);
        if(mTranslateX + mFillInWidth <= getWidth() - mBottomOffSet){
        	mTranslateX = getWidth()-mFillInWidth - mBottomOffSet;
        }
        Anim.trans(((IView)CustomListView.this), mTranslateX, mLocationY, mFlashListener);
        return true;
    }
    
    // 焦点上移一个
    private boolean moveLeft() {
        int selection = mSelectedPosition;
        selection--;
        if (selection < 0 ) {
            return false;
        }
        setSelection(selection);
        mTranslateX = -getParamsYToPos(selection);
		// 滑动聚焦范围不能超过屏幕的上保护距离 0+mTopOffSet为上边界距
		if (mTranslateX + mLeftPaddding >= 0) {
			mTranslateX = 0;
		}
		Anim.trans(((IView) CustomListView.this), mTranslateX,mLocationY, mFlashListener);
        return true;
    }
    
    IFlashListener mFlashListener = new IFlashListener() {
		
		@Override
		public void onStart() {
		}
		
		@Override
		public void onFinish() {
			recycleUnuseView();
		}
		
		@Override
		public void onCancel(boolean force) {
		}
	};
    
    /**
     * 从fromPosition位置开始创建各个ItemView
     * 将view都创建在AbsoluteLayout层次里面
     */
    private void layoutChildFromPosition(int fromPosition){
    	if(fromPosition<0){
    		return;
    	}
    	//重新布局文件
    	for(int i = fromPosition;i < getCount();i++){
    		if(getParamsYToPos(i) > getWidth()+getParamsYToPos(fromPosition)){
    			return;
    		}
    		if(mChildsMap.get(i) == null){
    			addItemView(i);
    		}
    	}
    	
    }
    
    /**
     * 添加指定的ItemView
     * @param index
     */
    private void addItemView(int position){
    	if(position < 0 || position >= getCount()){
    		return;
    	}
    	View temp = mRecycler.getScrapView();
    	View view = mAdapter.getView(position, temp, this);
    	if(!mChildsMap.containsValue(view)){	//存疑？？？？
    		mChildsMap.put(position, view);
    	}
		AbsoluteLayout.LayoutParams params =new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,mLeftPaddding + getParamsYToPos(position),mLeftOffSet);
		view.setLayoutParams(params);
		if(view.getParent() == null){
			addView(view);
		}
    }
    
    /**
	 * 对大板内部某个控件做回收
	 */
	private void removeItemView(View view){
		removeView(view);
		//遍历删除某个View
		for(Integer key : mChildsMap.keySet()){
			if(mChildsMap.get(key) == view){
				mChildsMap.remove(key);
				break;
			}
		}
		mRecycler.addScrapView(view);
	}
	
	/**
	 * 对大板内部某个控件做回收
	 * int当前第几个控件
	 */
	private void removeItemView(int key){
		View temp = mChildsMap.get(key);
		removeView(temp);
		mChildsMap.remove(key);
		
		mRecycler.addScrapView(temp);
	}
    
	/**
	 * 回收View
	 */
	private void recycleUnuseView(){
		//遍历删除无用的View
    	View temp;
    	for(Integer key : mChildsMap.keySet()){
    		temp = mChildsMap.get(key);
    		if(temp != null){
    			 int x = ((AbsoluteLayout.LayoutParams)temp.getLayoutParams()).x;
    			 if((x < getParamsYToPos(mSelectedPosition - 1) && getParamsYToPos(mSelectedPosition -1 )+mTranslateX<0)|| x > getParamsYToPos(mSelectedPosition) + getWidth()){
    				 mDeleteKey.add(key);
    			 }
    		}
    	}
    	for(int i=0;i < mDeleteKey.size();i++){
    		removeItemView(mDeleteKey.get(i));
    	}
    	mDeleteKey.clear();
	}
	
	/**
	 * View回收站
	 * @author san
	 * type为1时表示Special类型View
	 */
    private static class RecycleBin {
        private final Stack<View> mScrapViews;		//AppRecommViews

        public RecycleBin() {
            mScrapViews = new Stack<View>();
        }
        
        /**
         * viewType区分Item中那一类型的Item，可扩充
         * 从回收站取出缓存数据
         * @param viewType
         * @return 
         */
        public View getScrapView() {
        	if (!mScrapViews.empty()) {
    			return mScrapViews.pop();
    		}
        	return null;
        }
        
        /**
         * viewType为ItemView显示类型
         * @param 往回收站存放数据 v
         * @param viewType
         */
        public void addScrapView(View v) {
        	mScrapViews.push(v);
        }

        public void clear() {
            mScrapViews.clear();
        }
    }
    
}