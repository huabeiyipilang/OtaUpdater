package cn.ingenic.updater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NoticesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notices_activity);
        /*Button exit = (Button)this.findViewById(R.id.exit);
        exit.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                finish();
            }
        });*/
        TextView tv = (TextView)findViewById(R.id.notice_msg);
        Intent intent= getIntent();
        String msg = intent.getStringExtra("msg");
        if (msg != null)
            tv.setText(msg);
        
    }

}
