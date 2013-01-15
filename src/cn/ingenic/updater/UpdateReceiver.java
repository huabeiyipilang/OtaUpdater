package cn.ingenic.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.SystemProperties;
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
                    if (SystemProperties.get("ro.build.version.release").startsWith("4.1"))
                        cpFile();// :TODO cp file from /flash to /sdcard
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

    public static String cpFile() { // /sdcard0/update.zip to sdcard1/update.zip
        try {
            File oldfile = new File("/flash/update.zip");
            File newfile = new File("/sdcard/update.zip");
            if (newfile.exists())
                newfile.delete();
            FileInputStream in = new FileInputStream(oldfile);
            FileOutputStream out = new FileOutputStream(newfile);
            FileChannel inc=in.getChannel();
            FileChannel outc = out.getChannel();
            int len =2097152;
            ByteBuffer b=null;
            while (true) {
                if (inc.position() == inc.size()) {
                    in.close();
                    out.close();
                    return "OK";
                }
                if(inc.size()-inc.position() < len){
                    len = (int)(inc.size()-inc.position());
                }else{
                    len =2097152;
                }
                b = ByteBuffer.allocateDirect(len);
                inc.read(b); b.flip();
                outc.write(b);
                outc.force(false);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
           return e.toString()+ " " ;
        }
        //return "OK";
    }
}
