package cn.ingenic.updater;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class UpdateInstallActivity extends Activity implements OnClickListener {
	private File mUpdateFile;
	private TextView mText;
	private boolean mSaveFile;
	private boolean mClicked;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_update_install);
	    mSaveFile = true;
	    mClicked = false;
	    String file_path = getIntent().getStringExtra("update_file");
	    UpdateInfo info = (UpdateInfo)getIntent().getParcelableExtra("update_info");
	    mUpdateFile = new File(file_path.substring(file_path.indexOf("/sdcard")));
	    findViewById(R.id.abandon).setOnClickListener(this);
	    findViewById(R.id.later).setOnClickListener(this);
	    findViewById(R.id.install).setOnClickListener(this);
	    mText = (TextView)findViewById(R.id.update_info);

        mText.setText(getString(R.string.version) + info.version + " ( "
                + info.size / 1024 / 1024 + " MB )\n"
                + getString(R.string.description) + info.description + "\n");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.abandon:
		    mSaveFile = false;
		    mClicked = true;
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
        Notification.Builder builder=new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
        .setTicker(getString(R.string.update_later_msg))
        .setWhen(when+100)
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setAutoCancel(false)
        .setOngoing(true);
        NotificationManager nm = (NotificationManager) getSystemService("notification");
        nm.notify(1,builder.getNotification());
	}
}
