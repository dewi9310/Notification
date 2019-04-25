package notification.com.example.dewioktaviani.notification;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by dewi.oktaviani on 04/04/2019.
 */

public class MyApps extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
