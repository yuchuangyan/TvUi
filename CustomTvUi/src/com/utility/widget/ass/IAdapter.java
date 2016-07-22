package com.utility.widget.ass;

import com.utility.ui.IView;

public interface IAdapter {
	
	int getCount();
	
	IView getView(int index, IView contentView);

}
