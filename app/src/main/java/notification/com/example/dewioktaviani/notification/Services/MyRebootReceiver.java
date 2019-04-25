package notification.com.example.dewioktaviani.notification.Services;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyRebootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
//		Log.d("MySignalR", "MyRebootReceiver");
//		Intent serviceIntentMyServiceNative = new Intent(context, MySignalRService.class);
//        context.startService(serviceIntentMyServiceNative);
		Log.i(MyRebootReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
		Intent intentNew = new Intent(context, MySignalRService.class);
		context.startService(intentNew);
	}
}
