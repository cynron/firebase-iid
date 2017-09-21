package com.google.firebase.iid;

import android.content.Intent;
import android.os.ConditionVariable;
import android.util.Log;
import com.google.firebase.iid.zzm;
import com.google.firebase.iid.zzp;
import java.io.IOException;

final class zzo implements zzp {

   private final ConditionVariable zzmjl;
   private Intent intent;
   private String zzmjm;


   private zzo() {
      this.zzmjl = new ConditionVariable();
   }

   public final void zzq(Intent var1) {
      this.intent = var1;
      this.zzmjl.open();
   }

   public final void onError(String var1) {
      this.zzmjm = var1;
      this.zzmjl.open();
   }

   public final Intent zzbyo() throws IOException {
      if(!this.zzmjl.block(30000L)) {
         Log.w("InstanceID/Rpc", "No response");
         throw new IOException("TIMEOUT");
      } else if(this.zzmjm != null) {
         throw new IOException(this.zzmjm);
      } else {
         return this.intent;
      }
   }

   // $FF: synthetic method
   zzo(zzm var1) {
      this();
   }
}
