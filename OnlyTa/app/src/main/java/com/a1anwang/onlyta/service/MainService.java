package com.a1anwang.onlyta.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.a1anwang.onlyta.App;
import com.a1anwang.onlyta.R;
import com.a1anwang.onlyta.rongyunplugin.RongyunEvent;
import com.a1anwang.onlyta.rongyunplugin.custommessage.TALocationRequestMessage;
import com.a1anwang.onlyta.rongyunplugin.custommessage.TALocationResponeMessage;
import com.a1anwang.onlyta.ui.activity.MainActivity;
import com.a1anwang.onlyta.util.AmapLocationManager;
import com.a1anwang.onlyta.util.LogUtils;
import com.a1anwang.onlyta.util.MConfig;
import com.a1anwang.onlyta.util.MyConstants;
import com.a1anwang.onlyta.util.MyMusicPlayer;
import com.a1anwang.onlyta.util.MySharedPreferences;
import com.a1anwang.onlyta.util.MyUtils;
import com.a1anwang.onlyta.util.ToastUtils;
import com.a1anwang.onlyta.util.httputil.MyHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

/**
 * Created by a1anwang.com on 2018/1/29.
 */

public class MainService extends Service{

    public static  final String Action_Click_Foreground_Notification="Action_Click_Foreground_Notification."+MainService.class.getName();

    public final static String Action_Update_Target_Nickname="Action_Update_Target_Nickname"+MainService.class.getName();;
    public final static String Extra_Target_Nickname="Extra_Target_Nickname"+MainService.class.getName();;


    MySharedPreferences mySharedPreferences;
    App application;

    String mTargetId;

    MyMusicPlayer musicPlayer;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY ;
    }
    private LocalBinder binder = new LocalBinder();
    /**
     * 创建Binder对象，返回给客户端即Activity使用，提供数据交换的接口
     */
    public class LocalBinder extends Binder {
        // 声明一个方法，getService。（提供给客户端调用）
        public  MainService getService() {
            // 返回当前对象LocalService,这样我们就可在客户端端调用Service的公共方法了
            return MainService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("MainService  onCreate");
        application= (App) getApplication();

        mySharedPreferences=MySharedPreferences.getInstance(this);
        registerBroadcastReceiver();
        startForeground();

        musicPlayer=new MyMusicPlayer();
        if(application.userAccount!=null){
            //已登录账号,那么连接融云
            connectIM(application.userAccount.rongyun_token);
            boolean hasTargetId;
            if(application.userAccount.target_uid<=0){
                hasTargetId=false;
            }else{
                mTargetId=application.userAccount.target_uid+"";
                hasTargetId=true;
            }

            if(hasTargetId){
                setListeners();
            }
        }

    }
    BroadcastReceiver receiver;
    private void registerBroadcastReceiver() {
        IntentFilter filter=new IntentFilter();
        filter.addAction(Action_Click_Foreground_Notification);

        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(action.equals(Action_Click_Foreground_Notification)){
                    //点击了主通知栏
                    LogUtils.e("点击了主通知栏");
                    Intent intent1=new Intent(MainService.this, MainActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);

                }
            }
        };
        registerReceiver(receiver,filter);
    }

    private void startForeground() {
        String channelID = "1";

        String channelName = "Main";
        Intent notificationIntent = new Intent(Action_Click_Foreground_Notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID,channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(getApplicationContext(),channelID).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.app_name)+"正在运行").setContentIntent(pendingIntent).build();
            startForeground(1, notification);
        }else{
            Notification.Builder builder = new Notification.Builder(this)
                    //设置小图标
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //设置通知标题
                    .setContentTitle(getString(R.string.app_name)+"正在运行")
                    .setContentIntent(pendingIntent);
            startForeground(1,builder.build());
        }
    }

    public void setTargetId(String targetId){
        mTargetId=targetId;
        setListeners();
    }

    private   void setListeners() {
        LogUtils.e("setListeners");
        RongyunEvent.getInstance().setOnTA_Location_ClickListener(this,onTA_location_clickListener);
        RongyunEvent.getInstance().setOnReceiveMessageListener( onReceiveMessageListener);
    }


    RongyunEvent.OnMessageSendListener messageSendListener=new RongyunEvent.OnMessageSendListener() {
        @Override
        public void onAttached(Message message) {

        }

        @Override
        public void onSuccess(Message message) {
            MessageContent messageContent=message.getContent();
            if(messageContent.getClass().getSimpleName().equals(TALocationRequestMessage.class.getSimpleName())) {
                MySharedPreferences.getInstance(MainService.this).saveLastLocationRequestTime(System.currentTimeMillis());
                //位置请求发送成功之后,进行一个延时5s判断,如果对方没有任何回应,说明对方app挂掉了,那么这个时候发送一个系统消息告知对方不在线
                startDelayCheck();
            }
        }

        @Override
        public void onError(Message message, RongIMClient.ErrorCode errorCode) {

        }
    };

    RongyunEvent.OnTA_Location_ClickListener onTA_location_clickListener= new RongyunEvent.OnTA_Location_ClickListener() {
        @Override
        public void onTA_Location_Click() {
            long lastRequestTime=MySharedPreferences.getInstance(MainService.this).getLastLocationRequestTime();
            long currentTime=System.currentTimeMillis();
            if((currentTime-lastRequestTime)>MyConstants.Location_Request_Interval){
                //点击了 TA的位置,发送一条自定义消息:立马告诉老子你的位置
                RongyunEvent.getInstance().sendLocationRequestMessage(mTargetId, messageSendListener );
            }else{
                ToastUtils.showToast(MainService.this,"1分钟内只可以请求一次",1500);
            }

        }
    };

    RongyunEvent.OnReceiveMessageListener onReceiveMessageListener=new RongyunEvent.OnReceiveMessageListener() {
        @Override
        public void onReceived(Message message, int i) {

            LogUtils.e(LogUtils.TAG_1,"收到消息:"+message.toString());


            MessageContent messageContent=message.getContent();
            if(messageContent.getClass().getSimpleName().equals(TALocationRequestMessage.class.getSimpleName())){
                //这是自定义消息,请求位置信息
                LogUtils.e(LogUtils.TAG_1,"收到请求位置信息消息  :"+  message.toString());

                long receivedTime=message.getReceivedTime();
                long sentTime=message.getSentTime();
                LogUtils.e(LogUtils.TAG_1,"收到请求位置信息消息 接收时间:"+ MyUtils.formatTimeYMDHMS(receivedTime)+" 发送时间:"+ MyUtils.formatTimeYMDHMS(sentTime));

                if((receivedTime-sentTime)<MyConstants.Location_Request_OverTime){
                    //只有中间时间小于 超时时间才发送位置回复, 避免 退出APP后再次上线收到请求消息自动回复,因为没必要了
                    if(mySharedPreferences.getAutoRespondLocation()){
                        //已开启自动回应,先告知对方收到,同时开始进行定位    //"1"开启自动回应位置   "0"未开启自动回应位置,"3" 不在线
                        RongyunEvent.getInstance().sendLocationResponseMessage(mTargetId, "1", messageSendListener);

                        startGetLocation();
                    }else{
                        //未开启自动回应,发个反馈消息告知对方
                        RongyunEvent.getInstance().sendLocationResponseMessage(mTargetId, "0", messageSendListener);
                    }
                }

            }else if(messageContent.getClass().getSimpleName().equals(TALocationResponeMessage.class.getSimpleName())){
                //收到位置请求回应
                cancelDelayCheck();
            }
            if(!message.getUId().equals(application.userAccount.target_uid+""))
                return;//收到的消息不是 关联的用户的,忽略
            if(message.getContent().getUserInfo()!=null){ //收到融云默消息,那么取出其中的 对方用户信息,更新到UI上,这样对方的昵称或者头像修改之后可以及时更新
                final String target_nickname=message.getContent().getUserInfo().getName();
                if(target_nickname!=null){
                    Intent intent=new Intent(Action_Update_Target_Nickname);
                    intent.putExtra(Extra_Target_Nickname,target_nickname);
                    sendBroadcast(intent);
                }
            }
        }
    };
    Handler handler=new Handler();

    Runnable delayRunnable=new Runnable() {
        @Override
        public void run() {
            checkTargetOnlineState(0);//检查对方在线状态
        }
    };



    private void startDelayCheck() {
        handler.removeCallbacks(delayRunnable);
        handler.postDelayed(delayRunnable, MyConstants.Location_Request_OverTime);

    }
    private  void cancelDelayCheck(){
        handler.removeCallbacks(delayRunnable);
    }

    private void startGetLocation() {


        new AmapLocationManager(this, new AmapLocationManager.OnLocationListener() {
            @Override
            public void onLocationChanged(AmapLocationManager.LocationInfo mLocationInfo) {

                //获取到位置信息,然后发送给对方
                RongyunEvent.getInstance().sendLocationMessage(mTargetId,mLocationInfo.getLatitude(),mLocationInfo.getLongitude(),mLocationInfo.getAddress(),mLocationInfo.getStaticmapURL(),messageSendListener);
            }

            @Override
            public void onFailed(String err) {
                //获取位置信息失败, 也发送一个消息告知对方获取失败,暂时不处理

            }
        }).startLocation();
    }

    /**
     *
     * @param checkType  0代表 检测状态同时如果状态为不在线的话,由server 代表对方发送一个单聊消息过来,其他参数暂未定义
     */
    private void checkTargetOnlineState(int checkType) {
        String url = MConfig.ServerIP + "checkTargetOnlineState.php";
        Map<String,String> param=new HashMap<>();
        param.put("checkType",checkType+"");

        param.put("uid", application.userAccount.uid+"");
        param.put("target_uid", application.userAccount.target_uid+"");
        MyHttpUtil.doPost(url, param, new MyHttpUtil.MyHttpCallBack() {
            @Override
            public void onResponse(final String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int err = jsonObject.getInt("err");
                            if (err == 0) {
                                // 成功

                            } else if (err == 1) {
                                // 缺少参数

                            } else {

                            }
                        } catch (JSONException e) {

                        }

            }

            @Override
            public void noNetwork() {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
    public void connectIM(String token) {

        RongyunEvent.getInstance().connectIM(this, token, new RongyunEvent.RongyunConnectListener() {
            @Override
            public void onTokenIncorrect() {

            }

            @Override
            public void onSuccess(String userid) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

    }
}
