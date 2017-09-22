package com.google.firebase.iid;

import android.content.Intent;

interface IntentReceiver {

   void handle(Intent var1);

   void onError(String var1);
}
