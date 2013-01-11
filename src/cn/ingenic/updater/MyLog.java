package cn.ingenic.updater;

import android.util.Log;

public class MyLog {
	private final static String APP_NAME = "OtaUpdater";
	private Class mCls;
	
	public MyLog(Class cls){
		mCls = cls;
	}
	
	public void i(String msg){
		Log.i(APP_NAME, "["+mCls.getSimpleName()+"] "+msg);
	}

	public void w(String msg){
		Log.w(APP_NAME, "["+mCls.getSimpleName()+"] "+msg);
	}
	
	public void e(String msg){
		Log.e(APP_NAME, "["+mCls.getSimpleName()+"] "+msg);
	}
}
