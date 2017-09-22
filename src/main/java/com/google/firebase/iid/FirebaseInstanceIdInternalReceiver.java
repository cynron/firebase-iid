package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.common.util.zzp;
import com.google.firebase.iid.zzh;
import com.google.firebase.iid.zzq;

public final class FirebaseInstanceIdInternalReceiver extends WakefulBroadcastReceiver {

   private static boolean zzhqp = false;
   private static zzh zzmja;
   private static zzh zzmjb;


   public final void onReceive(Context var1, Intent var2) {
      if(var2 != null) {
         Parcelable var3;
         if(!((var3 = var2.getParcelableExtra("wrapped_intent")) instanceof Intent)) {
            Log.e("FirebaseInstanceId", "Missing or invalid wrapped intent");
         } else {
            Intent var4 = (Intent)var3;
            if(zzek(var1)) {
               zzah(var1, var2.getAction()).zza(var4, this.goAsync());
            } else {
               zzq.getInstance().zza(var1, var2.getAction(), var4);
            }
         }
      }
   }

   static boolean zzek(Context var0) {
      return !zzp.isAtLeastO()?false:var0.getApplicationInfo().targetSdkVersion > 25;
   }

   static synchronized zzh zzah(Context var0, String var1) {
      if("com.google.firebase.MESSAGING_EVENT".equals(var1)) {
         if(zzmjb == null) {
            zzmjb = new zzh(var0, var1);
         }

         return zzmjb;
      } else {
         if(zzmja == null) {
            zzmja = new zzh(var0, var1);
         }

         return zzmja;
      }
   }

}
