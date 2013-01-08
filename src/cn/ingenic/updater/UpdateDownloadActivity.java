package cn.ingenic.updater;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateDownloadActivity extends Activity implements OnClickListener {
	public final static int MODE_UPDATE = 1;
	public final static int MODE_ROLLBACK = 2;
	public final static String EXTRAS_MODE = "mode";
	public final static String EXTRAS_VERSION = "version";
	
	UpdateManager mManager;
	UpdateInfo mInfo;
	MyLog klilog = new MyLog(UpdateDownloadActivity.class);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mManager = UpdateManager.getInstance(this);
	    setContentView(R.layout.activity_update_download);
	    
	    Bundle bundle = getIntent().getExtras();
	    if(bundle == null){
	    	return;
	    }
	    int mode = bundle.getInt(EXTRAS_MODE, 0);
	    mInfo = (UpdateInfo)bundle.getParcelable(EXTRAS_VERSION);
	    
	    if(mode == 0 || mInfo == null){
	    	return;
	    }
	    klilog.i(mInfo.toString());
	    setupMode(mode);
	    StringBuilder sb = new StringBuilder();
	    sb.append(getString(R.string.version)+mInfo.version);
	    sb.append("\n");
	    sb.append(getString(R.string.size, mInfo.size));
	    sb.append("\n");
	    sb.append(getString(R.string.description));
	    sb.append("\n");
	    sb.append(mInfo.description.replace("\\n", "\n"));
	    ((TextView)findViewById(R.id.description)).setText(sb);
	    findViewById(R.id.btn_later).setOnClickListener(this);
	    findViewById(R.id.btn_ok).setOnClickListener(this);
	}
	
	private void setupMode(int mode){
		
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.btn_later:
			finish();
			break;
		case R.id.btn_ok:
			if(networkEnable()){
				download(mInfo.url);
			}else{
				Toast.makeText(this, R.string.network_not_avaliable, Toast.LENGTH_SHORT).show();
			}
			finish();
			break;
		}
	}
	
	private boolean networkEnable(){
		boolean anyDate = ((RadioButton)findViewById(R.id.any_data)).isChecked();
		boolean onlyWifi = ((RadioButton)findViewById(R.id.wifi_only)).isChecked();
		boolean res = false;
		ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null) {
			int nType = networkInfo.getType();
			if(anyDate){
				res = true;
			}else if(onlyWifi){
				res = nType == ConnectivityManager.TYPE_WIFI ? true : false;
			}
		}
		
		return res;
	}
	
	private void download(String url){
        DownloadManager dm=((DownloadManager)getSystemService("download"));
        Uri uri = Uri.parse(url);
        Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).mkdirs();
        Request dwreq = new DownloadManager.Request(uri);
        //下载时状态栏的提示标题
        dwreq.setTitle(getString(R.string.download_title));
        //下载时状态栏的提示描述
        dwreq.setDescription(getString(R.string.download_description));

        dwreq.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,  //保存的文件路径
                UpdateUtils.FILENAME_SAVED);     //保存的文件名字
        dwreq.setNotificationVisibility(Request.VISIBILITY_VISIBLE);       //是否在状态栏显示 (0,1,2,3)
        
        long id = dm.enqueue(dwreq);
        UpdateUtils.putDownloadInfo(this, id, mInfo);
    }

}
