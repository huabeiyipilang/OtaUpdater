package cn.ingenic.updater;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.view.View;
import android.view.View.OnClickListener;

public class UpdateInstallActivity extends Activity implements OnClickListener {
	private File mUpdateFile;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_update_install);
	    String file_path = getIntent().getStringExtra("update_file");
	    mUpdateFile = new File(file_path.substring(file_path.indexOf("/sdcard")));
	    findViewById(R.id.abandon).setOnClickListener(this);
	    findViewById(R.id.later).setOnClickListener(this);
	    findViewById(R.id.install).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.abandon:
			break;
		case R.id.later:
			break;
		case R.id.install:
			try {
				RecoverySystem.installPackage(this, mUpdateFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}

}
