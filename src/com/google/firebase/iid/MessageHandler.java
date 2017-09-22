package com.google.firebase.iid;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.firebase.iid.ServiceHelper;

final class MessageHandler extends Handler {

   // $FF: synthetic field
   private ServiceHelper service;


   MessageHandler(ServiceHelper var1, Looper looper) {
      this.service = var1;
      super(looper);
   }

   public final void handleMessage(Message msg) {
      this.service.handleMessage(msg);
   }
}
