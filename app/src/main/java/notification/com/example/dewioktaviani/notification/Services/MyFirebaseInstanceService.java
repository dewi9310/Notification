package notification.com.example.dewioktaviani.notification.Services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by dewi.oktaviani on 27/03/2019.
 */

public class MyFirebaseInstanceService extends FirebaseInstanceIdService {
    private static final String TAG="MyFirebaseInstanceServi";
    public MyFirebaseInstanceService() {
        super();
    }

//    @Override
//    public void handleIntent(Intent intent) {
//        super.handleIntent(intent);
//    }

    @Override
    public void onTokenRefresh() {
//        super.onTokenRefresh();
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().subscribeToTopic("dewi");
        Log.d(TAG, "Refreshed token: " + refreshedToken);

/* If you want to send messages to this application instance or manage this apps subscriptions on the server side, send the Instance ID token to your app server.*/

        sendRegistrationToServer(refreshedToken);
    }



    private void sendRegistrationToServer(String refreshedToken) {
        Log.d("TOKEN ", refreshedToken.toString());
    }

}

