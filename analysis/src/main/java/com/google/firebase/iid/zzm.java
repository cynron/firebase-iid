package com.google.firebase.iid;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.firebase.iid.zzl;

final class zzm extends Handler {

   // $FF: synthetic field
   private zzl zzmjk;


   zzm(zzl var1, Looper var2) {
      this.zzmjk = var1;
      super(var2);
   }

   public final void handleMessage(Message var1) {
      this.zzmjk.zzc(var1);
   }
}
