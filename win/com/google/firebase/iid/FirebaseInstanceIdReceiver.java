package com.google.firebase.iid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceIdInternalReceiver;
import com.google.firebase.iid.zzq;

public final class FirebaseInstanceIdReceiver extends WakefulBroadcastReceiver {

   public final void onReceive(Context var1, Intent var2) {
      var2.setComponent((ComponentName)null);
      var2.setPackage(var1.getPackageName());
      if(VERSION.SDK_INT <= 18) {
         var2.removeCategory(var1.getPackageName());
      }

      String var3;
      if((var3 = var2.getStringExtra("gcm.rawData64")) != null) {
         var2.putExtra("rawData", Base64.decode(var3, 0));
         var2.removeExtra("gcm.rawData64");
      }

      String var4 = null;
      String var5 = var2.getStringExtra("from");
      if(!"google.com/iid".equals(var5) && !"gcm.googleapis.com/refresh".equals(var5)) {
         if("com.google.android.c2dm.intent.RECEIVE".equals(var2.getAction())) {
            var4 = "com.google.firebase.MESSAGING_EVENT";
         } else {
            Log.d("FirebaseInstanceId", "Unexpected intent");
         }
      } else {
         var4 = "com.google.firebase.INSTANCE_ID_EVENT";
      }

      int var6 = -1;
      if(var4 != null) {
         int var10000;
         if(FirebaseInstanceIdInternalReceiver.zzek(var1)) {
            if(this.isOrderedBroadcast()) {
               this.setResultCode(-1);
            }

            FirebaseInstanceIdInternalReceiver.zzah(var1, var4).zza(var2, this.goAsync());
            var10000 = -1;
         } else {
            var10000 = zzq.getInstance().zza(var1, var4, var2);
         }

         var6 = var10000;
      }

      if(this.isOrderedBroadcast()) {
         this.setResultCode(var6);
      }

   }
}
