package com.a1anwang.onlyta.rongyunplugin.custommessage;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * Created by a1anwang.com on 2018/1/24.
 * 自定义消息, 请求对方位置信息
 */
@MessageTag(value = "app:request_target_location", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class TALocationRequestMessage  extends MessageContent {
    private String content;//消息属性，可随意定义 ,暂时没用到

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public  TALocationRequestMessage() {
            super();
    }

    public  TALocationRequestMessage(byte[] data){
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("content"))
                content = jsonObj.optString("content");

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
    }


    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", content);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    //给消息赋值。
    public TALocationRequestMessage(Parcel in) {
        content= ParcelUtils.readFromParcel(in);//该类为工具类，消息属性

        //这里可继续增加你消息的属性
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<TALocationRequestMessage> CREATOR = new Creator<TALocationRequestMessage>() {

        @Override
        public TALocationRequestMessage createFromParcel(Parcel source) {
            return new TALocationRequestMessage(source);
        }

        @Override
        public TALocationRequestMessage[] newArray(int size) {
            return new TALocationRequestMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, content);//该类为工具类，对消息中属性进行序列化
        //这里可继续增加你消息的属性
    }



}
