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
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(index+";");
		builder.append(version+";");
		builder.append(description+";");
		builder.append(url+";");
		builder.append(size+";");
		
		String next = "";
		if(next_version != null){
			for(int n : next_version){
				next += n + ",";
			}
		}
		builder.append(next+";");
		
		String pre = "";
		if(pre_version != null){
			for(int n : pre_version){
				pre += n + ",";
			}
		}
		builder.append(pre+";");
		
		return builder.toString();
	}
	
	public static UpdateInfo createFromString(String s){
		UpdateInfo info = new UpdateInfo();
		String [] values = s.split(";");
		int i = 0;
			info.index = Integer.valueOf(values[i++]);
			info.version = values[i++];
			info.description = values[i++];
			info.url = values[i++];
			info.size = Integer.valueOf(values[i++]);
			try {
				info.next_version = arrayS2I(values[i++].split(","));
				info.pre_version = arrayS2I(values[i++].split(","));
			} catch (Exception e) {
				info.next_version = null;
				info.pre_version = null;
				e.printStackTrace();
			}
		return info;
	}
	
	private static int[] arrayS2I(String[] ss){
		if(ss.length == 0){
			return null;
		}
		int[] ii = new int[ss.length];
		for(int n = 0; n < ii.length; n++){
			ii[n] = Integer.valueOf(ss[n]);
		}
		return ii;
	}
	

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
