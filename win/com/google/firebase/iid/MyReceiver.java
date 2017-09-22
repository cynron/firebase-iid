package com.google.firebase.iid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.iid.ServiceHelper;

final class MyReceiver extends BroadcastReceiver {

   // $FF: synthetic field
   private ServiceHelper service;


   MyReceiver(ServiceHelper service) {
      this.service = service;
      super();
   }

   public final void onReceive(Context ctx, Intent intent) {
      if(Log.isLoggable("InstanceID/Rpc", 3)) {
         String var3 = String.valueOf(intent.getExtras());
         Log.d("InstanceID/Rpc", (new StringBuilder(44 + String.valueOf(var3).length())).append("Received GSF callback via dynamic receiver: ").append(var3).toString());
      }

      this.service.onReceiverIntent(intent);
   }
}
