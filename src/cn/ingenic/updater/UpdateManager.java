package cn.ingenic.updater;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;

public class UpdateManager {
	public static final int SYNC_SUCCESS = 1;
	public static final int SYNC_FAIL = 2;

	public static final int CHECK_FAILED = 1;
	public static final int CHECK_NO_UPDATE = 2;
	public static final int CHECK_HAS_UPDATE = 3;
	public static final int CHECK_NO_ROLLBACK = 4;
	public static final int CHECK_HAS_ROLLBACK = 5;
	
	private static UpdateManager sInstance = null;
	private Context mContext;
	private List<UpdateInfo> mUpdateInfoList;
	
	private UpdateManager(Context context){
		mContext = context;
	}
	
	public static UpdateManager getInstance(Context context){
		if(sInstance == null){
			sInstance = new UpdateManager(context);
		}
		return sInstance;
	}
	
	public void sync(Message callback){
		new SyncDataTask(callback).execute("");
	}
	
	public int checkUpdate(){
		if(mUpdateInfoList == null || mUpdateInfoList.size() == 0){
			return CHECK_FAILED;
		}
		UpdateInfo next = getNextVersion();
		if(next != null){
			return CHECK_HAS_UPDATE;
		}else{
			return CHECK_NO_UPDATE;
		}
	}
	
	public int checkRollback(){
		if(mUpdateInfoList == null || mUpdateInfoList.size() == 0){
			return CHECK_FAILED;
		}
		UpdateInfo pre = getPreVersion();
		if(pre != null){
			return CHECK_HAS_ROLLBACK;
		}else{
			return CHECK_NO_ROLLBACK;
		}
	}
	
	public UpdateInfo getNextVersion(){
		UpdateInfo next = null;
		UpdateInfo current = getCurrentVersion();
		if (current != null && current.next_version != null) {
			String next_index = current.next_version.get(current.next_version.size() - 1);
			next = getInfoByIndex(next_index);
		}
		return next;
	}
	
	public UpdateInfo getPreVersion(){
		UpdateInfo pre = null;
		UpdateInfo current = getCurrentVersion();
		if(current != null && current.pre_version != null){
			String pre_index = current.pre_version.get(0);
			pre = getInfoByIndex(pre_index);
		}
		return pre;
	}
	
	private UpdateInfo getCurrentVersion(){
		String current_index = getCurrentIndex();
		return getInfoByIndex(current_index);
	}
	
	private UpdateInfo getInfoByIndex(String index){
		UpdateInfo update_info = null;
		for(UpdateInfo info:mUpdateInfoList){
			if(info.index.equals(index)){
				update_info = info;
			}
		}
		return update_info;
	}
	
	private String getCurrentIndex(){
		return "2";
	}
	
	private void onSyncSuccess(){
		/*
		try {
			InputStream is = mContext.getResources().getAssets().open("update_list2.xml");
			mUpdateInfoList = UpdateInfoHelper.getInstance().getUpdateList(is);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mContext,
					R.string.sync_failed, Toast.LENGTH_SHORT).show();
		}
		*/
	}


	private class SyncDataTask extends AsyncTask {
		
		private Message callback;

		public SyncDataTask(Message msg){
			callback = msg;
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			return SyncData();
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if((Integer)result == SYNC_SUCCESS){
				onSyncSuccess();
			}
			callback.arg1 = (Integer) result;
			callback.sendToTarget();
		}

		private int SyncData() {
			int res = SYNC_SUCCESS;
			HttpGet getMethod = new HttpGet(UpdateUtils.URL_TO_CHECK_UPDATE);
			HttpClient httpClient = new DefaultHttpClient();
			try {
				HttpResponse response = httpClient.execute(getMethod); // 发起GET请求
				String result = EntityUtils.toString(response.getEntity(),
						UpdateUtils.ENCODE);
				if (200 == response.getStatusLine().getStatusCode()) { // 获取响应码
					mUpdateInfoList = UpdateInfoHelper.getInstance().getUpdateList(result);
				}else{
					res = SYNC_FAIL;
				}
			} catch (ClientProtocolException e) {
				res = SYNC_FAIL;
				e.printStackTrace();
			} catch (IOException e) {
				res = SYNC_FAIL;
				e.printStackTrace();
			}
			return res;
		}

	}
}
