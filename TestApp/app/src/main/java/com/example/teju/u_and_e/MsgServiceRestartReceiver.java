package com.example.teju.u_and_e;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Teju on 12/24/2017.
 */

public class MsgServiceRestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Msg service receiver", "onreceive");
        Intent serviceIntent = new Intent(context, MyService.class);
        context.startService(serviceIntent);
    }
}
