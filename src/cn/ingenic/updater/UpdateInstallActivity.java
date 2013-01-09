package cn.ingenic.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import android.util.Log;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class UpdateInstallActivity extends Activity implements OnClickListener {
	private File mUpdateFile;
	private TextView mText;
	private String mUpdateDescription;
	private boolean mSaveFile;
	private boolean mClicked;
	private NotificationManager mNotificationManager;
	private static final String KEY_DESCRIPTION = "key_description";
	private static final String NOTIFICATION_TAG="update_tag";
	private static final int NOTIFICATION_ID = 102;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_update_install);
	    findViewById(R.id.abandon).setOnClickListener(this);
	    findViewById(R.id.later).setOnClickListener(this);
	    findViewById(R.id.install).setOnClickListener(this);
	    mText = (TextView)findViewById(R.id.update_info);
	    mSaveFile = true;
	    mClicked = false;
        mUpdateDescription = "";
        Intent intent = getIntent();
        if (intent.getStringExtra("update_file") != null) {
            String file_path = getIntent().getStringExtra("update_file");
            UpdateInfo info = (UpdateInfo) getIntent().getParcelableExtra(
                    "update_info");
            mUpdateFile = new File(file_path.substring(file_path
                    .indexOf("/sdcard")));
            String update_size = "";
            int size_kb = Integer.valueOf(info.size) / 1024;
            if (size_kb < 1024)
                update_size = size_kb + " KB";
            else
                update_size = size_kb / 1024 + "MB";
            mUpdateDescription = getString(R.string.version) + info.version
                    + " ( " + update_size + " )\n"
                    + getString(R.string.description) + info.description + "\n";
        } else { // come from Notification 
            mUpdateDescription = UpdateUtils.getStringFromSP(this, KEY_DESCRIPTION);
        }
        mText.setText(mUpdateDescription);
        
        mNotificationManager = (NotificationManager) getSystemService("notification");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.abandon:
		    mSaveFile = false;
		    mClicked = true;
		    mNotificationManager.cancel(NOTIFICATION_TAG,NOTIFICATION_ID);
			break;
		case R.id.later:
		    mSaveFile = true;
		    mClicked = true;
		    updateLater();
			break;
		case R.id.install:
		    mSaveFile = true;
		    mClicked = true;
			try {
			    getMD5(mUpdateFile);
				RecoverySystem.installPackage(this, mUpdateFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		this.finish();
	}

    @Override
    public void onStop() {
        super.onStop();
        if (!mSaveFile && mUpdateFile != null && mUpdateFile.exists()) {
            mUpdateFile.delete();
        }
        if (!mClicked) {
            updateLater();
        }
    }

    private void updateLater() {
        long when = System.currentTimeMillis();
        CharSequence contentTitle = getText(R.string.app_name);
        CharSequence contentText = getText(R.string.update_later_msg);
        
        Intent intent= new Intent(this,UpdateInstallActivity.class);
        PendingIntent cIntent=PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder=new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
        .setTicker(getString(R.string.update_later_msg))
        .setWhen(when+100)
        .setContentIntent(cIntent)
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setAutoCancel(false)
        .setOngoing(true);
        mNotificationManager.notify(NOTIFICATION_TAG,NOTIFICATION_ID,builder.getNotification());
        UpdateUtils.putStringToSP(this, KEY_DESCRIPTION, mUpdateDescription);
	}
    
    private String getMD5(File file){
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MessageDigest md = MessageDigest.getInstance("MD5");
            MappedByteBuffer bb = ch.map(FileChannel.MapMode.READ_ONLY, 0, file
                    .length());
            md.update(bb);
            byte updateBytes[] = md.digest();
            int len = updateBytes.length;
            char myChar[] = new char[len * 2];
            int k = 0;
            for (int i = 0; i < len; i++) {
                byte b = updateBytes[i];
                myChar[k++] = hexDigits[b >>> 4 & 0x0f];
                myChar[k++] = hexDigits[b & 0x0f];
            }
            in.close();
            ch.close();
            String md5 = new String(myChar).toUpperCase(Locale.ENGLISH);
            Log.d("dfdun", "MD5 = "+md5);
            return md5;
        } catch (IOException e) {
            Log.e("dfdun", "149:" + e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("dfdun", "151:" + e.toString());
        }
        return "";
    }
}
