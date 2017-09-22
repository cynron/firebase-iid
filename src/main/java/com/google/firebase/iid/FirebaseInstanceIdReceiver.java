package com.google.firebase.iid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceIdInternalReceiver;
import com.google.firebase.iid.ReceiverUtil;

public final class FirebaseInstanceIdReceiver extends WakefulBroadcastReceiver {

   public final void onReceive(Context ctx, Intent intent) {
      intent.setComponent((ComponentName)null);
      intent.setPackage(ctx.getPackageName());
      if(VERSION.SDK_INT <= 18) {
         intent.removeCategory(ctx.getPackageName());
      }

      String var3;
      if((var3 = intent.getStringExtra("gcm.rawData64")) != null) {
         intent.putExtra("rawData", Base64.decode(var3, 0));
         intent.removeExtra("gcm.rawData64");
      }

      String action = null;
      String from = intent.getStringExtra("from");
      if(!"google.com/iid".equals(from) && !"gcm.googleapis.com/refresh".equals(from)) {
         if("com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            action = "com.google.firebase.MESSAGING_EVENT";
         } else {
            Log.d("FirebaseInstanceId", "Unexpected intent");
         }
      } else {
         action = "com.google.firebase.INSTANCE_ID_EVENT";
      }

      int var6 = -1;
      if(action != null) {
         int var10000;
         if(FirebaseInstanceIdInternalReceiver.zzek(ctx)) {
            if(this.isOrderedBroadcast()) {
               this.setResultCode(-1);
            }

            FirebaseInstanceIdInternalReceiver.handleMessageEvent(ctx, action).zza(intent, this.goAsync());
            var10000 = -1;
         } else {
            var10000 = ReceiverUtil.getInstance().handleIntent(ctx, action, intent);
         }

         var6 = var10000;
      }

      if(this.isOrderedBroadcast()) {
         this.setResultCode(var6);
      }

   }
}
