package com.frank.customtvui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.frank.customtvui.adapter.HtmlAdapter;
import com.frank.customtvui.listview.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Tv Ui控件
 * @author san
 *
 */
public class HorizontalListActivity extends Activity {
	private CustomListView mListView;
	private HtmlAdapter mAdapter;
	private List<String> mDatas = new ArrayList<String>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        for(int i = 0;i< 400;i++){
			mDatas.add("current index is " + i);
		}
		if(mAdapter == null){
			mAdapter = new HtmlAdapter(HorizontalListActivity.this,mDatas);
		}
		mListView = (CustomListView) findViewById(R.id.html_page_listview);
		mListView.setLocationX(300);
		mListView.setAdapter(mAdapter);
		mListView.setChildsWidth(400);
		mListView.setSelection(0);
		mListView.setFocus(true);
    }
    
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		super.dispatchKeyEvent(event);
		if(event.getAction() != KeyEvent.ACTION_DOWN){
			return false;
		}
		return mListView.dispatchKeyEvent(event);
	}

}
