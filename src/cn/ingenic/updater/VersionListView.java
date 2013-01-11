package cn.ingenic.updater;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class VersionListView extends LinearLayout {
	private Context mContext;
	
	//views
	private RadioGroup mVersionListView; 
	
	private OnVersionCheckedListener mListener;
	
	private List<String> mVersionList;
	
	public interface OnVersionCheckedListener{
		void OnVersionChanged(String version, boolean update);
	}
	
	public VersionListView(Context context) {
		super(context);
		mContext = context;
		initViews();
	}
	
	public VersionListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initViews();
	}

	private void initViews(){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View root = inflater.inflate(R.layout.version_list_view, this);
		mVersionListView = (RadioGroup)root.findViewById(R.id.rg_version_list);
		mVersionListView.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int rid) {
				if(mListener != null){
					RadioButton rBtn = (RadioButton)group.findViewById(rid);
					String ver = rBtn.getText().toString();
					mListener.OnVersionChanged(ver, isUpdateVersion(ver));
				}
			}
			
		});
	}
	
	private boolean isUpdateVersion(String version){
		String currentVersion = UpdateManager.getInstance(mContext).getCurrentVersion();
		int current_pos = mVersionList.indexOf(currentVersion);
		int pos = mVersionList.indexOf(version);
		return pos > current_pos;
	}
	
	public void setOnVersionCheckedListener(OnVersionCheckedListener listener){
		mListener = listener;
	}
	
	public void setVersionList(List<String> list){
		mVersionListView.removeAllViews();
		mVersionList = list;
		String currentVersion = UpdateManager.getInstance(mContext).getCurrentVersion();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		for(String ver : list){
			RadioButton rBtn = new RadioButton(mContext);
			rBtn.setLayoutParams(params);
			if(!ver.equals(currentVersion)){
				rBtn.setText(ver);
			}else{
				rBtn.setText(ver + mContext.getString(R.string.current_version_ps));
				rBtn.setEnabled(false);
			}
			mVersionListView.addView(rBtn);
		}
	}
}
