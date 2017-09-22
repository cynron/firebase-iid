package com.google.firebase.iid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.zzq;

class FirebaseInstanceIdService$zza extends BroadcastReceiver {

   @Nullable
   private static BroadcastReceiver receiver;
   private int retryDelay;


   static synchronized void getInstance(Context ctx, int delay) {
      if(receiver == null) {
         receiver = new FirebaseInstanceIdService$zza(delay);
         ctx.getApplicationContext().registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
      }
   }

   private FirebaseInstanceIdService$zza(int retry) {
      this.retryDelay = retry;
   }

   public void onReceive(Context ctx, Intent intent) {
      Class var3 = FirebaseInstanceIdService$zza.class;
      synchronized(FirebaseInstanceIdService$zza.class) {
         if(receiver != this) {
            return;
         }

         if(!FirebaseInstanceIdService.isConnected(ctx)) {
            return;
         }

         if(Log.isLoggable("FirebaseInstanceId", 3)) {
            Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
         }

         ctx.getApplicationContext().unregisterReceiver(this);
         receiver = null;
      }

      ReceiverUtil.getInstance().handleIdEvent(ctx, FirebaseInstanceIdService.createIntentDelay(this.retryDelay));
   }
}
