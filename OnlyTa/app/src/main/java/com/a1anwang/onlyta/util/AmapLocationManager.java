package com.a1anwang.onlyta.util;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by a1anwang.com on 2018/1/24.
 */

public class AmapLocationManager implements AMapLocationListener {
    private final static String AmapWebAPI_key="01858d5753db1e00ecd0067a7e1db4d7";

    private  OnLocationListener locationListener;




    //声明AMapLocationClient类对象

    public AMapLocationClient mLocationClient = null;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public AmapLocationManager(Context context,OnLocationListener l){
        super();
        locationListener=l;
        mLocationClient = new AMapLocationClient(context);
        //设置定位监听
        mLocationClient.setLocationListener(this);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }


    public  void startLocation(){
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                if(locationListener!=null){
                    LocationInfo locationInfo=new LocationInfo();
                    locationInfo.setLatitude(amapLocation.getLatitude());
                    locationInfo.setLongitude( amapLocation.getLongitude());
                    locationInfo.setAddress(amapLocation.getAddress());
                    locationListener.onLocationChanged(locationInfo);
                }


                LogUtils.e(LogUtils.TAG_1," onLocationChanged:"+amapLocation.getAddress());
            } else {

                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                if(locationListener!=null){

                    locationListener.onFailed(amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    }


    public class LocationInfo{
        private  double latitude=1000;//纬度
        private  double longitude=1000;//经度
        private  String address;
        private  String staticmapURL;    //地图缩率图地址。静态地图地址

        public void setAddress(String address) {
            this.address = address;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getAddress() {
            return address;
        }

        public String getStaticmapURL() {
            if(staticmapURL==null &&latitude!=1000&&longitude!=1000){

                staticmapURL="http://restapi.amap.com/v3/staticmap?location="+longitude+","+latitude+"&zoom=10&size=400*300&markers=mid,,:"+longitude+","+latitude+"&key="+AmapWebAPI_key;

            }
            return staticmapURL;
        }
    }

    public interface  OnLocationListener{
        public void onLocationChanged(LocationInfo mLocationInfo);
        public void onFailed(String err);

    }

}
