package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.common.util.zzp;
import com.google.firebase.iid.zzh;
import com.google.firebase.iid.ReceiverUtil;

public final class FirebaseInstanceIdInternalReceiver extends WakefulBroadcastReceiver {

   private static boolean zzhqp = false;
   private static zzh zzmja;
   private static zzh zzmjb;


   public final void onReceive(Context ctx, Intent intent) {
      if(intent != null) {
         Parcelable var3;
         if(!((var3 = intent.getParcelableExtra("wrapped_intent")) instanceof Intent)) {
            Log.e("FirebaseInstanceId", "Missing or invalid wrapped intent");
         } else {
            Intent var4 = (Intent)var3;
            if(zzek(ctx)) {
               handleMessageEvent(ctx, intent.getAction()).zza(var4, this.goAsync());
            } else {
               ReceiverUtil.getInstance().handleIntent(ctx, intent.getAction(), var4);
            }
         }
      }
   }

   static boolean zzek(Context var0) {
      return !zzp.isAtLeastO()?false:var0.getApplicationInfo().targetSdkVersion > 25;
   }

   static synchronized zzh handleMessageEvent(Context var0, String action) {
      if("com.google.firebase.MESSAGING_EVENT".equals(action)) {
         if(zzmjb == null) {
            zzmjb = new zzh(var0, action);
         }

         return zzmjb;
      } else {
         if(zzmja == null) {
            zzmja = new zzh(var0, action);
         }

         return zzmja;
      }
   }

}
