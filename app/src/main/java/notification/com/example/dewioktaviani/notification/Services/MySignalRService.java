package notification.com.example.dewioktaviani.notification.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import notification.com.example.dewioktaviani.notification.MainActivity;
import notification.com.example.dewioktaviani.notification.R;
import notification.com.example.dewioktaviani.notification.config.Config;

/**
 * Created by dewi.oktaviani on 10/04/2019.
 */

public class MySignalRService extends Service {
    public static HubConnection mHubConnection;
    private static HubProxy mHubProxy;
    static TextView textView;
    static Context mContext;
   static Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = MySignalRService.this;
        handler = new Handler(Looper.getMainLooper());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            IntentFilter filter = new IntentFilter();
            filter.addAction("Services.MyRebootReceiver");
            mContext.registerReceiver(new MyRebootReceiver(), filter);
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MySignalR", "onStartCommand");
        startSignalR();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent broadcastIntent = new Intent("Services.MyRebootReceiver");
        mContext.sendBroadcast(broadcastIntent);
        Log.d("MySignalR", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("MySignalR", "onTaskRemoved");
        Intent broadcastIntent = new Intent("Services.MyRebootReceiver");
        sendBroadcast(broadcastIntent);
        super.onTaskRemoved(rootIntent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.mContext = MySignalRService.this;
        handler = new Handler(Looper.getMainLooper());
        return null;
    }


    public void sendMSG(){
        String METHOD_SERVER = "SendMessage";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("txtConnectionId", mHubConnection.getConnectionId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mHubConnection.getConnectionId() != null) {
            mHubProxy.invoke(METHOD_SERVER, jsonObject.toString());
        }else {
            startSignalR();
        }
    }

    public void RegistrasiUser(String userName, String groupName, TextView textView){
        this.textView = textView;
        String METHOD_SERVER = "RegisterUserConnection";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("txtUserName", userName);
            jsonObject.put("txtConnectionId", mHubConnection.getConnectionId());
            jsonObject.put("txtGroupName", groupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mHubConnection.getConnectionId() != null) {
            mHubProxy.invoke(METHOD_SERVER, jsonObject.toString());
        }else {
            startSignalR();
        }
    }

    public void UpdateUserConnection(String txtUserId, String groupName){
        this.textView = textView;
        String METHOD_SERVER = "UpdateUserConnection";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("intUserId", txtUserId);
            jsonObject.put("txtConnectionId", mHubConnection.getConnectionId());
            jsonObject.put("txtGroupName", groupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mHubConnection.getConnectionId() != null) {
            mHubProxy.invoke(METHOD_SERVER, jsonObject.toString());
        }else {
            startSignalR();
        }
    }

    public void SendGroupMessage(String txtUserId){
        this.textView = textView;
        String METHOD_SERVER = "SendGroupMessage";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("intUserId", txtUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mHubConnection.getConnectionId() != null) {
            mHubProxy.invoke(METHOD_SERVER, jsonObject.toString());
        }else {
            startSignalR();
        }
    }

    public boolean startSignalR() {
        boolean status;
        Platform.loadPlatformComponent(new AndroidPlatformComponent());


        String serverUrl  = "http://10.171.13.50:8070";
//        String serverUrl  = "10.171.163.233/BelajarAPI";

        mHubConnection = new HubConnection(serverUrl);

        String SERVER_HUB = "NOtificationHubs";
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB);
        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);
        mHubProxy.on("SendMessage", new SubscriptionHandler1<String>() {
            @Override
            public void run(String s) {
                Config.title = s;
                Config.content =s;
                Config.imageUrl = s;
                Config.gameUrl = s;
                String a = s;
                if (s!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sendNotification();
                        }
                    });

                }
            }
        }, String.class);

        mHubProxy.on("SendBroadcastMessage", new SubscriptionHandler1<String>() {
            @Override
            public void run(String s) {
                Config.title = s;
                Config.content =s;
                Config.imageUrl = s;
                Config.gameUrl = s;
                String a = s;
                if (s!=null){
                    sendNotification();
                }
            }
        }, String.class);

        mHubProxy.on("UpdateUserConnection", new SubscriptionHandler1<String>() {
            @Override
            public void run(String s) {
                Config.title = s;
                Config.content =s;
                Config.imageUrl = s;
                Config.gameUrl = s;
                String a = s;
                if (s!=null){
                    sendNotification();
                }
            }
        }, String.class);

        mHubProxy.on("SendGroupMessage", new SubscriptionHandler1<String>() {
            @Override
            public void run(String s) {
                Config.title = s;
                Config.content =s;
                Config.imageUrl = s;
                Config.gameUrl = s;
                String a = s;
                if (s!=null){
                    sendNotification();
                }
            }
        }, String.class);

        mHubProxy.on("disconnectionMessage", new SubscriptionHandler1<String>() {
            @Override
            public void run(String s) {
                Config.title = s;
                Config.content =s;
                Config.imageUrl = s;
                Config.gameUrl = s;
                String a = s;
                if (s!=null){
                    sendNotification();
                }
            }
        }, String.class);

        mHubProxy.on("RegisterUserConnection", new SubscriptionHandler2<String, String>() {
            @Override
            public void run(String s, String s2) {
                Config.title = s;
                Config.content =s;
                Config.imageUrl = s;
                Config.gameUrl = s;
                if (s!=null&&s!=""){
                    textView.setText(s2);
                }
                if (s!=null){
                    sendNotification();
                }
            }
        }, String.class, String.class);

        try {
            signalRFuture.get();
            status = true;
        } catch (InterruptedException | ExecutionException e) {
            status = false;
        }
        String id = mHubConnection.getConnectionId();

        mHubConnection.received(new MessageReceivedHandler() {
            @Override
            public void onMessageReceived(JsonElement jsonElement) {
                JSONObject json;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String jsonString = jsonObject.toString();
                try {
                    json = new JSONObject(jsonString);
                    JSONArray jsonArray = json.getJSONArray("A");
                    String jsonArrayString = jsonArray.get(0).toString();
//                    handler = new Handler(Looper.getMainLooper());
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
////                            Toast.makeText(mContext, "Unable to add account. Add new account only through the application", Toast.LENGTH_LONG).show();
//                            Toast.makeText(mContext, jsonArrayString, Toast.LENGTH_SHORT).show();
//                        }
//                    });

//                    JSONObject jsonObjectFinal = new JSONObject(jsonArrayString);
//                    String strMethodName = jsonObjectFinal.get("strMethodName").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Toast.makeText(getApplicationContext(), Config.title, Toast.LENGTH_SHORT).show();
            }
        });

//        mHubConnection.received(new MessageReceivedHandler() {
//            @Override
//            public void onMessageReceived(JsonElement jsonElement) {

//
//                try {
//                    json = new JSONObject(jsonString);
//                    JSONArray jsonArray = json.getJSONArray("A");
//                    String jsonArrayString = jsonArray.get(0).toString();
//                    JSONObject jsonObjectFinal = new JSONObject(jsonArrayString);
//                    String strMethodName = jsonObjectFinal.get("strMethodName").toString();
//
//                    if (mHubConnectionSevice != null) {
//                        service.WMSMobileService.mHubConnectionSevice.onReceiveMessageHub(jsonObjectFinal);
//                    }
////                    if(updateSnackbar != null && strMethodName.equalsIgnoreCase("pushDataOffline")){
////                        WMSMobileService.updateSnackbar.onUpdateSnackBar(true);
////                    }
//
//                    if (strMethodName.equalsIgnoreCase("ConfirmSPMDetail")) {
//                        initMethodConfirmSPMDetail(jsonObjectFinal);
//                    } else if (strMethodName.equalsIgnoreCase("cancelSPMDetail")) {
//                        initMethodCancelSPMDetail(jsonObjectFinal);
//                    } else if(strMethodName.equalsIgnoreCase("revertCancelSPMDetail")){
//                        initMethodRevertSPMDetail(jsonObjectFinal);
//                    } else if (strMethodName.equalsIgnoreCase("pushDataOffline")) {
//                        updateFromPushDataOffline(jsonObjectFinal);
//                    }
//
//                    if (strMethodName.equals("getLatestSTAR")) {
//                        JSONObject jsonObjectHeader = jsonObjectFinal.getJSONObject("listOfmSPMHeader");
//
//                        String status = jsonObjectHeader.get("STATUS").toString();
//                        String sync = jsonObjectHeader.get("SYNC").toString();
//
//                        mSPMHeaderData _mSPMHeaderData = new mSPMHeaderData();
//
//                        tUserLoginData dataLogin = new tUserLoginData();
//                        dataLogin = new tUserLoginBL().getUserActive();
//
//                        if (status.equals("1") && sync.equals("0")) {
//
//                            SQLiteDatabase db = new clsMainBL().getDb();
//                            new clsHelper().DeleteHeaderDetailStar(db);
//
//                            _mSPMHeaderData.setIntSPMId(jsonObjectHeader.get("SPM_HEADER_ID").toString());
//                            _mSPMHeaderData.setTxtNoSPM(jsonObjectHeader.get("SPM_NO").toString());
//                            _mSPMHeaderData.setTxtBranchCode(jsonObjectHeader.get("BRANCH_CODE").toString());
//                            _mSPMHeaderData.setTxtSalesOrder(jsonObjectHeader.get("SALES_ORDER").toString());
//                            _mSPMHeaderData.setIntUserId(jsonObjectHeader.get("USER_ID").toString());
//                            _mSPMHeaderData.setBitStatus(jsonObjectHeader.get("SYNC").toString());
//                            _mSPMHeaderData.setBitSync(jsonObjectHeader.get("SYNC").toString());
//                            _mSPMHeaderData.setBitStart("0");
//                            _mSPMHeaderData.setIntUserId(dataLogin.getIntUserId());
//
//                            new mSPMHeaderBL().saveData(_mSPMHeaderData);
//
//                            JSONArray jsonArrayInner = jsonObjectFinal.getJSONArray("listOfmSPMDetail");
//
//                            List<mSPMDetailData> _mSPMDetailData = new ArrayList<>();
//
//                            JSONObject jsonObjectNew = new JSONObject();
//
//                            for (int i = 0; i < jsonArrayInner.length(); i++) {
//
//                                jsonObjectNew = jsonArrayInner.getJSONObject(i);
//
//                                mSPMDetailData data = new mSPMDetailData();
//
//                                data.setIntSPMDetailId(jsonObjectNew.get("SPM_DETAIL_ID").toString());
//                                data.setTxtNoSPM(jsonObjectNew.get("SPM_NO").toString());
//                                data.setTxtLocator(jsonObjectNew.get("LOCATOR").toString());
//                                data.setTxtItemCode(jsonObjectNew.get("ITEM_CODE").toString());
//                                data.setTxtItemName(jsonObjectNew.get("ITEM_NAME").toString());
//                                data.setIntQty(jsonObjectNew.get("QUANTITY").toString());
//                                data.setBitStatus(jsonObjectNew.get("STATUS").toString());
//                                data.setBitSync(jsonObjectNew.get("SYNC").toString());
//                                data.setTxtUOM(jsonObjectNew.get("UOM").toString());
//                                data.setTxtLotNumber(jsonObjectNew.get("LOT_NUM").toString());
//                                data.setIntUserId(dataLogin.getIntUserId());
//                                new mSPMDetailBL().insert(data);
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        clsStatusMenuStart _clsStatusMenuStart = new clsStatusMenuStart();
//        try {
//            _clsStatusMenuStart = new clsMainBL().checkUserActive();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (_clsStatusMenuStart.get_intStatus() == enumStatusMenuStart.UserActiveLogin) {
//            tUserLoginData _tUserLoginData;
//            _tUserLoginData = new tUserLoginBL().getUserActive();
//
//            new WMSMobileService().updateConnectionId(_tUserLoginData.getIntUserId());
//        }

        return status;
    }

    private void sendNotification(){


//        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
//        style.bigPicture(bitmap);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        boolean tes = new MainActivity().isMyServiceRunning(MySignalRService.class);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "101";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

            //Configure Notification Channel
            notificationChannel.setDescription("Game Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.setSound(defaultSound, null);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(Config.title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(Config.content)
                .setContentIntent(pendingIntent)
//                .setStyle(style)
//                .setLargeIcon(bitmap)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX);


        notificationManager.notify(1, notificationBuilder.build());


    }
}
