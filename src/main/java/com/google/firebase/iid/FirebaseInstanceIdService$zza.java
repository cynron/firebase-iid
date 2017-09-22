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
   private int zzmjf;


   static synchronized void zzl(Context var0, int var1) {
      if(receiver == null) {
         receiver = new FirebaseInstanceIdService$zza(var1);
         var0.getApplicationContext().registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
      }
   }

   private FirebaseInstanceIdService$zza(int var1) {
      this.zzmjf = var1;
   }

   public void onReceive(Context var1, Intent var2) {
      Class var3 = FirebaseInstanceIdService$zza.class;
      synchronized(FirebaseInstanceIdService$zza.class) {
         if(receiver != this) {
            return;
         }

         if(!FirebaseInstanceIdService.zzen(var1)) {
            return;
         }

         if(Log.isLoggable("FirebaseInstanceId", 3)) {
            Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
         }

         var1.getApplicationContext().unregisterReceiver(this);
         receiver = null;
      }

      zzq.getInstance().zze(var1, FirebaseInstanceIdService.zzfx(this.zzmjf));
   }
}
