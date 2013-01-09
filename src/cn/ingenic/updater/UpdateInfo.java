package cn.ingenic.updater;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateInfo implements Parcelable {
	public String index;
	public String version;
	public String description;
	public String url;
	public String size;
	public String md5;
	public ArrayList<String> next_version = new ArrayList<String>();
	public ArrayList<String> pre_version = new ArrayList<String>();
	
	private MyLog klilog = new MyLog(UpdateInfo.class);
	
	public UpdateInfo(){
		
	}
	
	private UpdateInfo(Parcel in){
		index = in.readString();
		version = in.readString();
		description = in.readString();
		url = in.readString();
		size = in.readString();
		md5 = in.readString();
		next_version = (ArrayList<String>) in.readSerializable();
		pre_version = (ArrayList<String>) in.readSerializable();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(index);
		out.writeString(version);
		out.writeString(description);
		out.writeString(url);
		out.writeString(size);
		out.writeString(md5);
		out.writeSerializable(next_version);
		out.writeSerializable(pre_version);
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
		builder.append(md5+";");
		
		String next = "";
			for(String n : next_version){
				next += n + ",";
			}
		builder.append(next+";");
		
		String pre = "";
			for(String n : pre_version){
				pre += n + ",";
			}
		builder.append(pre+";");
		
		return builder.toString();
	}
	
	public static UpdateInfo createFromString(String s){
		UpdateInfo info = new UpdateInfo();
		String [] values = s.split(";");
		int i = 0;
			info.index = values[i++];
			info.version = values[i++];
			info.description = values[i++];
			info.url = values[i++];
			info.size = values[i++];
			info.md5 = values[i++];
			try {
				info.next_version = (ArrayList<String>) Arrays.asList(values[i++].split(","));
				info.pre_version = (ArrayList<String>) Arrays.asList(values[i++].split(","));
			} catch (Exception e) {
				info.next_version = new ArrayList<String>();
				info.pre_version = new ArrayList<String>();
				e.printStackTrace();
			}
		return info;
	}

	public void dump(){
		klilog.i("=========UpdateInfo===========");
		klilog.i("index			:"+index);
		klilog.i("version		:"+version);
		klilog.i("description	:"+description);
		klilog.i("url			:"+url);
		klilog.i("size			:"+size);
		klilog.i("md5			:"+md5);
		klilog.i("next_version	:"+next_version);
		klilog.i("pre_version	:"+pre_version);
	}
}
