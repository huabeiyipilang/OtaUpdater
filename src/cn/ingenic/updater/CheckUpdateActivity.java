package cn.ingenic.updater;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.ingenic.updater.VersionListView.OnVersionCheckedListener;

public class CheckUpdateActivity extends Activity implements OnClickListener, OnVersionCheckedListener {
	private final static int MSG_SYNC_START = 1;
	private final static int MSG_SYNC_FINISHED = 2;
	private final static int MSG_CHECK_UPDATE = 3;
	private final static int MSG_CHECK_ROLLBACK = 4;
	private final static int MSG_GET_VERSION_LIST = 5;

	
	private ProgressDialog mWaitingDialog;
	private UpdateManager mManager;
	private boolean mUpdate;
	private String mSelectedVersion;
	private Button mCheckButton;
	private Button mSelectButton;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SYNC_START:
				showCheckingDialog();
				Message sync_msg = mHandler.obtainMessage(MSG_SYNC_FINISHED);
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
			case MSG_GET_VERSION_LIST:
				List<String> list = mManager.getVersionListBaseCurrent();
				showVersionList(list);
				break;
			}
		}
	};
	
	private void showCheckingDialog(){
		mWaitingDialog = new ProgressDialog(CheckUpdateActivity.this);
		mWaitingDialog.setMessage(getResources().getString(R.string.checking));
		mWaitingDialog.setCancelable(false);
		mWaitingDialog.show();
	}
	
	private void hideCheckingDialog(){
		if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
			mWaitingDialog.dismiss();
			mWaitingDialog = null;
		}
	}
	
	private void showVersionList(List<String> list){
		VersionListView versionList = (VersionListView)findViewById(R.id.version_list);
		versionList.setVersionList(list);
		versionList.setOnVersionCheckedListener(this);
		ViewGroup deviceInfo = (ViewGroup)findViewById(R.id.device_info);
		ViewGroup versionInfo = (ViewGroup)findViewById(R.id.version_info);
		flipit(deviceInfo, versionInfo);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_check);
		mManager = UpdateManager.getInstance(this);
		mCheckButton = (Button)findViewById(R.id.btn_check_now);
		mCheckButton.setOnClickListener(this);
		mSelectButton = (Button)findViewById(R.id.btn_version_selected);
		mSelectButton.setOnClickListener(this);
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
//			translateToDownload(UpdateDownloadActivity.MODE_UPDATE, mManager.getNextVersion());
			break;
		case UpdateManager.CHECK_HAS_ROLLBACK:
//			translateToDownload(UpdateDownloadActivity.MODE_ROLLBACK, mManager.getPreVersion());
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
//			operate(MSG_CHECK_UPDATE);
			operate(MSG_GET_VERSION_LIST);
			break;
		case R.id.btn_version_selected:
			int mode = mUpdate ? UpdateDownloadActivity.MODE_UPDATE : UpdateDownloadActivity.MODE_ROLLBACK;
			translateToDownload(mode, mManager.getUpdateInfoTo(mSelectedVersion));
			break;
		}
	}
	
	private void operate(int cmd){
		Message msg = new Message();
		msg.what = MSG_SYNC_START;
		msg.obj = mHandler.obtainMessage(cmd);
		mHandler.sendMessage(msg);
	}

    private void flipit(final ViewGroup from, final ViewGroup to) {
        Interpolator accelerator = new AccelerateInterpolator();
        Interpolator decelerator = new DecelerateInterpolator();
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(from, "rotationY", 0f, 90f);
        visToInvis.setDuration(500);
        visToInvis.setInterpolator(accelerator);
        
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(to, "rotationY",
                -90f, 0f);
        invisToVis.setDuration(500);
        invisToVis.setInterpolator(decelerator);
        
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                from.setVisibility(View.GONE);
                invisToVis.start();
                to.setVisibility(View.VISIBLE);
            }
        });
        
        
        visToInvis.start();
    }

	@Override
	public void OnVersionChanged(String version, boolean update) {
		mSelectedVersion = version;
		mUpdate = update;
		int res = update ? R.string.update_to_version : R.string.rollback_to_version;
		mSelectButton.setEnabled(true);
		mSelectButton.setText(getString(res, mSelectedVersion));
	}

	/*
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
	*/
	

}
