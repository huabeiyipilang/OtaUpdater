package cn.ingenic.updater;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CheckUpdateActivity extends Activity implements OnClickListener {
	private final static int MSG_SYNC_START = 1;
	private final static int MSG_SYNC_FINISHED = 2;
	private final static int MSG_CHECK_UPDATE = 3;
	private final static int MSG_CHECK_ROLLBACK = 4;

	
	private ProgressDialog mWaitingDialog;
	private UpdateManager mManager;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SYNC_START:
				showCheckingDialog();
				Message sync_msg = obtainMessage(MSG_SYNC_FINISHED);
				sync_msg.obj = msg.obj;
				mManager.sync(sync_msg);
				break;
			case MSG_SYNC_FINISHED:
				hideCheckingDialog();
				onSyncFinished(msg);
				break;
			case MSG_CHECK_UPDATE:
				int res_update = mManager.checkUpdate();
				onCheckFinished(res_update);
				break;
			case MSG_CHECK_ROLLBACK:
				int res_rollback = mManager.checkRollback();
				onCheckFinished(res_rollback);
				break;
			}
		}
	};
	
	private void showCheckingDialog(){
		mWaitingDialog = new ProgressDialog(CheckUpdateActivity.this);
		mWaitingDialog.setMessage(getResources().getString(R.string.checking));
		mWaitingDialog.show();
	}
	
	private void hideCheckingDialog(){
		if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
			mWaitingDialog.dismiss();
			mWaitingDialog = null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_check);
		mManager = UpdateManager.getInstance(this);
		findViewById(R.id.btn_check_now).setOnClickListener(this);
		setupDeviceInfomation();
	}

	private void setupDeviceInfomation(){
		TextView product = (TextView)findViewById(R.id.tv_product);
		TextView android_version = (TextView)findViewById(R.id.tv_android_version);
		TextView system_version = (TextView)findViewById(R.id.tv_system_version);
		product.setText(android.os.Build.MODEL);
		android_version.setText(android.os.Build.VERSION.RELEASE);
		system_version.setText("");
	}
	
	private void onSyncFinished(Message msg){
		switch (msg.arg1) {
		case UpdateManager.SYNC_SUCCESS:
			((Message)msg.obj).sendToTarget();
			break;
		case UpdateManager.SYNC_FAIL:
			Toast.makeText(CheckUpdateActivity.this,
					R.string.sync_failed, Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	private void onCheckFinished(int res){
		switch(res){
		case UpdateManager.CHECK_FAILED:
			break;
		case UpdateManager.CHECK_NO_UPDATE:
			Toast.makeText(CheckUpdateActivity.this,
					R.string.already_latest_version, Toast.LENGTH_SHORT).show();
			break;
		case UpdateManager.CHECK_HAS_UPDATE:
			translateToDownload(UpdateDownloadActivity.MODE_UPDATE, mManager.getNextVersion());
			break;
		case UpdateManager.CHECK_HAS_ROLLBACK:
			translateToDownload(UpdateDownloadActivity.MODE_ROLLBACK, mManager.getPreVersion());
			break;
		case UpdateManager.CHECK_NO_ROLLBACK:
			Toast.makeText(CheckUpdateActivity.this,
					R.string.no_rollback_version, Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	private void translateToDownload(int mode, UpdateInfo info){
		Bundle bundle = new Bundle();
		bundle.putInt(UpdateDownloadActivity.EXTRAS_MODE, mode);
		bundle.putParcelable(UpdateDownloadActivity.EXTRAS_VERSION, info);
		Intent intent = new Intent(CheckUpdateActivity.this,
				UpdateDownloadActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_check_now:
			operate(MSG_CHECK_UPDATE);
			break;
		}
	}
	
	private void operate(int cmd){
		Message msg = new Message();
		msg.what = MSG_SYNC_START;
		msg.obj = mHandler.obtainMessage(cmd);
		mHandler.sendMessage(msg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.check_update_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_version_back:
			operate(MSG_CHECK_ROLLBACK);
			break;
		}
		return true;
	}
	
	

}
