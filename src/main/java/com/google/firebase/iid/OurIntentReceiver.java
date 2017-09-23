package com.google.firebase.iid;

import android.content.Intent;
import android.os.ConditionVariable;
import android.util.Log;
import com.google.firebase.iid.MyHandler;
import com.google.firebase.iid.IntentReceiver;
import java.io.IOException;

final class OurIntentReceiver implements IntentReceiver {

   private final ConditionVariable cond;
   private Intent intent;
   private String errorMsg;


   private OurIntentReceiver() {
      this.cond = new ConditionVariable();
   }

   public final void onReceiveIntent(Intent intent) {
      this.intent = intent;
      this.cond.open();
   }

   public final void onError(String errorMsg) {
      this.errorMsg = errorMsg;
      this.cond.open();
   }

   public final Intent waitForResult() throws IOException {
      if(!this.cond.block(30000L)) {
         Log.w("InstanceID/Rpc", "No response");
         throw new IOException("TIMEOUT");
      } else if(this.errorMsg != null) {
         throw new IOException(this.errorMsg);
      } else {
         return this.intent;
      }
   }

   // $FF: synthetic method
   OurIntentReceiver(MessageHandler var1) {
      this();
   }
}
