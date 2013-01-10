package cn.ingenic.updater;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductInfo implements Parcelable{
	public String model;
	public String url;
	
	private MyLog klilog = new MyLog(this.getClass());
	
	public ProductInfo(){
		
	}
	
	private ProductInfo(Parcel in){
		model = in.readString();
		url = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(model);
		out.writeString(url);
	}
	
	public static final Parcelable.Creator<ProductInfo> CREATOR = new Parcelable.Creator<ProductInfo>(){

		@Override
		public ProductInfo createFromParcel(Parcel in) {
			return new ProductInfo(in);
		}

		@Override
		public ProductInfo[] newArray(int size) {
			return new ProductInfo[size];
		}
		
	};
	
	public void dump(){
		klilog.i("=========ProductInfo===========");
		klilog.i("model			:"+model);
		klilog.i("url			:"+url);
	}
}
