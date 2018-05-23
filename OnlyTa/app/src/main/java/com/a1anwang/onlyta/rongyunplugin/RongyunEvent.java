package com.a1anwang.onlyta.rongyunplugin;

import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;

import com.a1anwang.onlyta.rongyunplugin.custommessage.TALocationRequestMessage;
import com.a1anwang.onlyta.rongyunplugin.custommessage.TALocationRequestMessageItemProvider;
import com.a1anwang.onlyta.rongyunplugin.custommessage.TALocationResponeMessage;
import com.a1anwang.onlyta.rongyunplugin.custommessage.TALocationResponeMessageItemProvider;
import com.a1anwang.onlyta.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.LocationMessage;

import static com.a1anwang.onlyta.util.LogUtils.TAG_1;

/**
 * Created by a1anwang.com on 2018/1/29.
 */

public class RongyunEvent {



    private static class RongyunEventHold {
        /**
         * 单例对象实例
         */
        static final RongyunEvent INSTANCE = new RongyunEvent();
    }

    public static RongyunEvent getInstance() {

        return  RongyunEventHold.INSTANCE;
    }

    /**
     * private的构造函数用于避免外界直接使用new来实例化对象
     */
    private RongyunEvent() {
    }

    /**
     * readResolve方法应对单例对象被序列化时候
     */
    private Object readResolve() {
        return getInstance();
    }


    /**
     * RongIM.init 融云初始化后做的事情
     */
    public void afterInit(){
        RongIM.getInstance().registerMessageType(TALocationRequestMessage.class);
        RongIM.getInstance().registerMessageTemplate(new TALocationRequestMessageItemProvider());

        RongIM.getInstance().registerMessageType(TALocationResponeMessage.class);
        RongIM.getInstance().registerMessageTemplate(new TALocationResponeMessageItemProvider());
        setMyExtensionModule();//这是+号自定义功能
    }

    /**
     * 融云连接服务器 connectIM 成功之后做的事情
     */
    public void afterConnected(){
        RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
        RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目
    }

    public void setCurrentUserinfo(String uid,String nickname,String headImageURL){
        UserInfo userInfo=new UserInfo(uid,nickname, Uri.parse(headImageURL));
        RongIM.getInstance().setCurrentUserInfo(userInfo);
        RongIM.getInstance().setMessageAttachedUserInfo(true);

    }

    /**
     *  设置聊天+ 号后的扩展列表
     */
    private void setMyExtensionModule() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new MyExtensionModule());
            }
        }
    }

    public void setOnTA_Location_ClickListener(Context context, final OnTA_Location_ClickListener listener){

        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        MyLocationPlugin myLocationPlugin = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof MyExtensionModule) {
                    List<IPluginModule> pluginModules =  module.getPluginModules(Conversation.ConversationType.PRIVATE );
                     for (IPluginModule pluginModule:pluginModules){
                         if(pluginModule instanceof MyLocationPlugin){
                             myLocationPlugin= (MyLocationPlugin) pluginModule;
                             break;
                         }
                     }
                }

            }
        }
        if(myLocationPlugin!=null){
            myLocationPlugin.setOnClickListener(listener);
        }

    }


    public  void setOnReceiveMessageListener(final OnReceiveMessageListener onReceiveMessageListener){
        RongIM.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener(){

            @Override
            public boolean onReceived(Message message, int i) {
                if(onReceiveMessageListener!=null){
                    onReceiveMessageListener.onReceived(message,i);
                }
                return false;
            }
        });
    }


    public void sendLocationRequestMessage(String mTargetId, final OnMessageSendListener messageSendListener){
        //pushContent：当客户端离线，接受推送通知时，通知的内容会显示为 pushContent 的内容。如果发送的是自定义消息，该字段必须填写，否则会无法收到该消息的推送。
        //pushData：收到该消息的推送时的附加信息。如果设置该字段，用户在收到该消息的推送时，能通过推送监听 onNotificationMessageArrived() 里的参数 PushNotificationMessage 的 getPushData() 方法获取。
        TALocationRequestMessage messageContent=new TALocationRequestMessage();
        messageContent.setContent("[立马告诉老子你在哪里]");
        Message message1=new Message();
        message1.setTargetId(mTargetId);
        message1.setConversationType( Conversation.ConversationType.PRIVATE);
        message1.setContent(messageContent);
        RongIM.getInstance().sendMessage(message1 ,"立马告诉老子你的位置","",new IRongCallback.ISendMessageCallback(){

            @Override
            public void onAttached(Message message) {
                LogUtils.e(LogUtils.TAG_1," onAttached:"+message.toString());
                if(messageSendListener!=null){
                    messageSendListener.onAttached(message);
                }
            }

            @Override
            public void onSuccess(Message message) {
                LogUtils.e(LogUtils.TAG_1," onSuccess:"+message.toString());
               if(messageSendListener!=null){
                    messageSendListener.onSuccess(message);
                }

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtils.e(LogUtils.TAG_1," onError:"+errorCode);
                if(messageSendListener!=null){
                    messageSendListener.onError(message, errorCode);
                }
            }
        });
    }

    public void sendLocationResponseMessage(String mTargetId, String response,final OnMessageSendListener messageSendListener) {
        Message respondmessage=new Message();
        respondmessage.setTargetId(mTargetId);
        respondmessage.setConversationType( Conversation.ConversationType.PRIVATE);

//        ""

        TALocationResponeMessage messafeContent= new TALocationResponeMessage();
        if(response.equals("0")){
            messafeContent.setContent("[朕已阅,但朕比较忙,懒得告诉你位置]");
        }else if(response.equals("1")){
            messafeContent.setContent("[朕已阅,正在定位中,定位好立马发给你]");

        }else if(response.equals("3")){
            messafeContent.setContent("[我暂时不在线,可能原因:app未加入白名单被清理(此条消息为系统自动发送)]");

        }
        respondmessage.setContent(messafeContent);

        RongIM.getInstance().sendMessage(respondmessage,"","",new IRongCallback.ISendMessageCallback(){

            @Override
            public void onAttached(Message message) {
                LogUtils.e(LogUtils.TAG_1," onAttached:"+message.toString());
                if(messageSendListener!=null){
                    messageSendListener.onAttached(message);
                }
            }

            @Override
            public void onSuccess(Message message) {
                LogUtils.e(LogUtils.TAG_1," onSuccess:"+message.toString());
                if(messageSendListener!=null){
                    messageSendListener.onSuccess(message);
                }

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtils.e(LogUtils.TAG_1," onError:"+errorCode);
                if(messageSendListener!=null){
                    messageSendListener.onError(message, errorCode);
                }
            }
        });

    }

    /**
     * 发送地图位置消息
     * @param mTargetId

     * @param messageSendListener
     */
    public void sendLocationMessage(String mTargetId, double latitude,double longitude,String address,String staticmapURL,final OnMessageSendListener messageSendListener) {
        Message message=new Message();
        message.setTargetId(mTargetId);
        message.setConversationType( Conversation.ConversationType.PRIVATE);
        LocationMessage locationMessage=  LocationMessage.obtain(latitude,longitude,address, Uri.parse(staticmapURL));
        message.setContent(locationMessage);

        RongIM.getInstance().sendLocationMessage(message,"","",new IRongCallback.ISendMessageCallback(){

            @Override
            public void onAttached(Message message) {
                LogUtils.e(LogUtils.TAG_1," onAttached:"+message.toString());
                if(messageSendListener!=null){
                    messageSendListener.onAttached(message);
                }
            }

            @Override
            public void onSuccess(Message message) {
                LogUtils.e(LogUtils.TAG_1," onSuccess:"+message.toString());
                if(messageSendListener!=null){
                    messageSendListener.onSuccess(message);
                }

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                LogUtils.e(LogUtils.TAG_1," onError:"+errorCode);
                if(messageSendListener!=null){
                    messageSendListener.onError(message, errorCode);
                }
            }
        });

    }


    public boolean isConnectedIM(){
        return RongIM.getInstance().getCurrentConnectionStatus()== RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED;
    }

    public void addConnectListener(RongyunConnectListener l) {
        connectListeners.add(l);
    }
    private List<RongyunConnectListener> connectListeners=new ArrayList<>();
    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {RongIM.init(this);} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token    从服务端获取的用户身份令牌（Token）。

     */
    public void connectIM(Context context, String token, final RongyunConnectListener connectListener) {
        if (context.getApplicationInfo().packageName.equals(getCurProcessName(context.getApplicationContext()))) {
            if(connectListener!=null){
                connectListeners.add(connectListener);
            }
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    LogUtils.e(TAG_1," onTokenIncorrect");
                    for (RongyunConnectListener l:connectListeners){
                        l.onTokenIncorrect();
                    }

                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtils.e(TAG_1," onSuccess userid:"+userid);
                    afterConnected();
                    for (RongyunConnectListener l:connectListeners){
                        l.onSuccess(userid);
                    }
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtils.e(TAG_1," onError errorCode:"+errorCode);
                    for (RongyunConnectListener l:connectListeners){
                        l.onError(errorCode);
                    }
                }
            });
        }
    }
    private    String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    public interface RongyunConnectListener{
        public void onTokenIncorrect();
        public void onSuccess(String userid);
        public void onError(RongIMClient.ErrorCode errorCode);
    }

    public interface OnTA_Location_ClickListener{
        public void onTA_Location_Click();

    }
    public interface OnMessageSendListener{
        public void onAttached(Message message);
        public void onSuccess(Message message);
        public void onError(Message message, RongIMClient.ErrorCode errorCode);
    }
    public interface OnReceiveMessageListener{
        public void onReceived(Message message, int i);
    }


}
