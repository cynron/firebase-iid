package com.google.firebase.iid;

import android.content.Intent;

interface IntentReceiver {

   void onReceiveIntent(Intent var1);

   void onError(String var1);
}
