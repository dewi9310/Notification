package notification.com.example.dewioktaviani.notification;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import notification.com.example.dewioktaviani.notification.Services.MyRebootReceiver;
import notification.com.example.dewioktaviani.notification.Services.MySignalRService;

public class MainActivity extends AppCompatActivity {
    public TextView tvMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = (TextView)findViewById(R.id.tvMsg);
        EditText etName = (EditText) findViewById(R.id.etName);
        EditText etUserID = (EditText) findViewById(R.id.etUserID);
        EditText etGroupName = (EditText) findViewById(R.id.etGroupName);
        Button btnSubmit = (Button)findViewById(R.id.btnSubmit);
        Button btnRegisterGroup = (Button)findViewById(R.id.btnRegisterGroup);
        Button btnBroadcastGroup = (Button)findViewById(R.id.btnBroadcastGroup);
        Button btnBroadcast = (Button)findViewById(R.id.btnBroadcast);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etName.getText().toString().trim().equals("")){
                    new MySignalRService().RegistrasiUser(etName.getText().toString(), "", tvMsg);
                }else{
                    Toast.makeText(getApplicationContext(), "isi dulu namanya", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnRegisterGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etUserID.getText().toString().trim().equals("")&&!etGroupName.getText().toString().trim().equals("")){
                    new MySignalRService().UpdateUserConnection(etUserID.getText().toString(), etGroupName.getText().toString().trim());
                }else{
                    Toast.makeText(getApplicationContext(), "isi dulu id sama groupnya", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });


        btnBroadcastGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService(new Intent(MainActivity.this, MySignalRService.class));
//                if (!etUserID.getText().toString().trim().equals("")){
//                    new MySignalRService().SendGroupMessage(etUserID.getText().toString());
//                }else{
//                    Toast.makeText(getApplicationContext(), "isi dulu id", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        tvMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MySignalRService().sendMSG();
            }
        });
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            IntentFilter filter = new IntentFilter();
//            filter.addAction("Services.MyRebootReceiver");
//            registerReceiver(new MyRebootReceiver(), filter);
//        }

        if (isMyServiceRunning(MySignalRService.class)){
            Toast.makeText(getApplicationContext(), "service idup", Toast.LENGTH_SHORT).show();
        }else {
            startService(new Intent(MainActivity.this, MySignalRService.class));
        }

//        startSignalR();
//        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
//        String token = FirebaseInstanceId.getInstance().getToken();
//        FirebaseMessaging.getInstance().subscribeToTopic("dewi");
//       Toast.makeText(MainActivity.this, FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void checkVersion(){

        String strLinkAPI = "http://10.171.13.50:8070/api/SignalR/SendBroadcastMessage";
        JSONObject resJson = new JSONObject();

//        try {
//            resJson.put("device_info", new clsHardCode().pDeviceInfo());
//            resJson.put("txtRefreshToken", dataToken.get(0).txtRefreshToken.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        final String mRequestBody = resJson.toString();
        volleyCheckVersion(MainActivity.this, strLinkAPI, mRequestBody, "Checking your version......", new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, Boolean status, String strErrorMsg) {
                if (response!=null){
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(response);
//                        jsonObject = new JSONObject(response);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                }

            }
        });
    }

    public void volleyCheckVersion(final Context context, String strLinkAPI, final String mRequestBody, String progressBarType, final VolleyResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final String[] body = new String[1];
        final String[] message = new String[1];
        final ProgressDialog Dialog = new ProgressDialog(context);
        Dialog.setMessage(progressBarType);
        Dialog.setCancelable(false);
        Dialog.show();

        final ProgressDialog finalDialog = Dialog;
        final ProgressDialog finalDialog1 = Dialog;



        StringRequest request = new StringRequest(Request.Method.POST, strLinkAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Boolean status = false;
                String errorMessage = null;
                listener.onResponse(response, status, errorMessage);
                finalDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = "";
                NetworkResponse networkResponse = error.networkResponse;
               if (error instanceof NetworkError) {
                    msg = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    msg = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    msg = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    msg = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    msg = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    msg = "Connection TimeOut! Please check your internet connection.";
                } else {
                    msg = "Error 500, Server Error";
                }

                if (msg!=null||!msg.equals("")){
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    finalDialog1.dismiss();
                }
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                dataToken = getDataToken(context);
//                access_token = dataToken.get(0).getTxtUserToken();
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + access_token);
//
//                return headers;
//            }
        };
        request.setRetryPolicy(new
                DefaultRetryPolicy(60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        queue.add(request);

    }
}
