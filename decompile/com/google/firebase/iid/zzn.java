package com.google.firebase.iid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.iid.zzl;

final class zzn extends BroadcastReceiver {

   // $FF: synthetic field
   private zzl zzmjk;


   zzn(zzl var1) {
      this.zzmjk = var1;
      super();
   }

   public final void onReceive(Context var1, Intent var2) {
      if(Log.isLoggable("InstanceID/Rpc", 3)) {
         String var3 = String.valueOf(var2.getExtras());
         Log.d("InstanceID/Rpc", (new StringBuilder(44 + String.valueOf(var3).length())).append("Received GSF callback via dynamic receiver: ").append(var3).toString());
      }

      this.zzmjk.zzi(var2);
   }
}
