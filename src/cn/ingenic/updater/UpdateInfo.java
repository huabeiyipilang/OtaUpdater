package cn.ingenic.updater;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateInfo implements Parcelable {
	public int index;
	public String version;
	public String description;
	public String url;
	public int size;
	public int[] next_version;
	public int[] pre_version;
	
	private MyLog klilog = new MyLog(UpdateInfo.class);
	
	public UpdateInfo(){
		
	}
	
	private UpdateInfo(Parcel in){
		index = in.readInt();
		version = in.readString();
		description = in.readString();
		url = in.readString();
		size = in.readInt();
		try {
			in.readIntArray(next_version);
		} catch (NullPointerException e) {
			next_version = null;
		}
		try {
			in.readIntArray(pre_version);
		} catch (NullPointerException e) {
			pre_version = null;
		}
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeInt(index);
		out.writeString(version);
		out.writeString(description);
		out.writeString(url);
		out.writeInt(size);
		out.writeIntArray(next_version);
		out.writeIntArray(pre_version);
	}
	
	public static final Parcelable.Creator<UpdateInfo> CREATOR = new Parcelable.Creator<UpdateInfo>(){

		@Override
		public UpdateInfo createFromParcel(Parcel in) {
			return new UpdateInfo(in);
		}

		@Override
		public UpdateInfo[] newArray(int size) {
			return new UpdateInfo[size];
		}
		
	};
	

	public void dump(){
		klilog.i("=========UpdateInfo===========");
		klilog.i("index: 		"+index);
		klilog.i("version: 		"+version);
		klilog.i("description: 	"+description);
		klilog.i("url: 			"+url);
		klilog.i("size: 		"+size);
		klilog.i("next_version: "+next_version);
		klilog.i("pre_version: 	"+pre_version);
	}
}
