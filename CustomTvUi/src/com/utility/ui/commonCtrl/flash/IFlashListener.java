package com.utility.ui.commonCtrl.flash;

public interface IFlashListener {

	void onStart();
	
	void onFinish();
	
	void onCancel(boolean force);
	
}
