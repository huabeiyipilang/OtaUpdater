package cn.ingenic.updater;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver {
	MyLog klilog = new MyLog(UpdateReceiver.class);

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long cache_id = UpdateUtils.getDownloadId(context);
			long downloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			if (cache_id != downloadId) {
				Log.e("dfdun", "download id = " + downloadId);
				return;
			}
			DownloadManager dm = ((DownloadManager) context
					.getSystemService("download"));
			Query query = new Query();
			query.setFilterById(cache_id);
			Cursor c = dm.query(query);
			if (c.moveToFirst()) {
				int columnIndex = c
						.getColumnIndex(DownloadManager.COLUMN_STATUS);
				if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
					String update_uri = c.getString(c
									.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
					showUpdateInstall(context, update_uri, UpdateUtils.getUpdateInfoCache(context));
					//wipe cache
					UpdateUtils.putDownloadInfo(context, 0, null);
				}
			}
		}
	}

	private void showUpdateInstall(Context context, String file, UpdateInfo info) {
		Intent intent = new Intent(context, UpdateInstallActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("update_file", file);
		intent.putExtra("update_info", info);
		context.startActivity(intent);
	}

}
