package com.google.firebase.iid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.zzb;
import com.google.firebase.iid.KeyPairStore;
import com.google.firebase.iid.zzk;
import com.google.firebase.iid.zzl;
import com.google.firebase.iid.zzq;
import com.google.firebase.iid.zzs;
import com.google.firebase.iid.FirebaseInstanceIdService.zza;
import java.io.IOException;

public class FirebaseInstanceIdService extends zzb {

   @VisibleForTesting
   private static Object lock = new Object();
   @VisibleForTesting
   private static boolean isPending = false;
   private boolean isLog = false;


   public final boolean onStart(Intent intent) {
      this.isLog = Log.isLoggable("FirebaseInstanceId", 3);
      if(intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
         return false;
      } else {
         String var2 = getSubType(intent);
         if(this.isLog) {
            String var10002 = String.valueOf(var2);
            Log.d("FirebaseInstanceId", var10002.length() != 0?"Register result in service ".concat(var10002):new String("Register result in service "));
         }

         this.getKeyStoreForSubtype(var2);
         KeyPairStore.getServiceHelper().onReceiverIntent(intent);
         return true;
      }
   }

   public void handleIntent(Intent intent) {
      String var2;
      if((var2 = intent.getAction()) == null) {
         var2 = "";
      }

      byte var4 = -1;
      switch(var2.hashCode()) {
      case -1737547627:
         if(var2.equals("ACTION_TOKEN_REFRESH_RETRY")) {
            var4 = 0;
         }
      default:
         switch(var4) {
         case 0:
            this.handleIntent(intent, false, false);
            return;
         default:
            String var7 = getSubType(intent);
            KeyPairStore keyPairStore = this.getKeyStoreForSubtype(var7);
            String var9 = intent.getStringExtra("CMD");
            if(this.isLog) {
               String var10 = String.valueOf(intent.getExtras());
               Log.d("FirebaseInstanceId", (new StringBuilder(18 + String.valueOf(var7).length() + String.valueOf(var9).length() + String.valueOf(var10).length())).append("Service command ").append(var7).append(" ").append(var9).append(" ").append(var10).toString());
            }

            if(null != intent.getStringExtra("unregistered")) {
               KeyPairStore.getPrefs().removeToken(var7 == null?"":var7);
               KeyPairStore.getServiceHelper().onRecieverIntent(intent);
            } else if("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
               KeyPairStore.getPrefs().removeToken(var7);
               this.handleIntent(intent, false, true);
            } else if("RST".equals(var9)) {
               keyPairStore.removeInstanceId();
               this.handleIntent(intent, true, true);
            } else {
               if("RST_FULL".equals(var9)) {
                  if(!KeyPairStore.getPrefs().isEmpty()) {
                     keyPairStore.removeInstanceId();
                     KeyPairStore.getPrefs().clear();
                     this.handleIntent(intent, true, true);
                     return;
                  }
               } else {
                  if("SYNC".equals(var9)) {
                     KeyPairStore.getPrefs().removeToken(var7);
                     this.handleIntent(intent, false, true);
                     return;
                  }

                  if("PING".equals(var9)) {
                     Bundle var12 = intent.getExtras();
                     String var13;
                     if((var13 = ServiceHelper.detectUnderlyService(this)) == null) {
                        Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
                        return;
                     }

                     Intent var14;
                     (var14 = new Intent("com.google.android.gcm.intent.SEND")).setPackage(var13);
                     var14.putExtras(var12);
                     ServiceHelper.addPendingIntent(this, var14);
                     var14.putExtra("google.to", "google.com/iid");
                     var14.putExtra("google.message_id", ServiceHelper.genReqSeq());
                     this.sendOrderedBroadcast(var14, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
                  }
               }

            }
         }
      }
   }

   protected final Intent pollIdEventQueue(Intent var1) {
      return (Intent)ReceiverUtil.getInstance().IdEventQueue.poll();
   }

   private static String getSubType(Intent var0) {
      String var1;
      return (var1 = var0.getStringExtra("subtype")) == null?"":var1;
   }

   private final KeyPairStore getKeyStoreForSubtype(String var1) {
      if(var1 == null) {
         return KeyPairStore.createOrGetKeyPairStoreForBundle(this, (Bundle)null);
      } else {
         Bundle var2;
         (var2 = new Bundle()).putString("subtype", var1);
         return KeyPairStore.createOrGetKeyPairStoreForBundle(this, var2);
      }
   }

   private final void handleIntent(Intent intent, boolean var2, boolean isTokenFresh) {
      Object var4 = lock;
      synchronized(lock) {
         isPending = false;
      }

      if(ServiceHelper.detectUnderlyService(this) != null) {
         TokenWrapper tokenWrapper;
         FirebaseInstanceId id;
         if((tokenWrapper = (id = FirebaseInstanceId.getInstance()).getTokenWrapper()) != null && !tokenWrapper.shouldRefresh(KeyPairStore.appVersion)) {
            TopicOpQueue var18;
            for(String var7 = (var18 = FirebaseInstanceId.getTopicOpQueue()).popFirst(); var7 != null; var7 = var18.fetchFirst()) {
               String[] var8;
               if((var8 = var7.split("!")).length == 2) {
                  String var9 = var8[0];
                  String topic = var8[1];

                  try {
                     byte var12 = -1;
                     switch(var9.hashCode()) {
                     case 83:
                        if(var9.equals("S")) {
                           var12 = 0;
                        }
                        break;
                     case 85:
                        if(var9.equals("U")) {
                           var12 = 1;
                        }
                     }

                     switch(var12) {
                     case 0:
                        FirebaseInstanceId.getInstance().requestTopicToken(topic);
                        if(this.isLog) {
                           Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                        }
                        break;
                     case 1:
                        FirebaseInstanceId.getInstance().deleteTopicToken(topic);
                        if(this.isLog) {
                           Log.d("FirebaseInstanceId", "unsubscribe operation succeeded");
                        }
                     }
                  } catch (IOException var13) {
                     this.handleError(intent, var13.getMessage());
                     return;
                  }
               }

               var18.popFirst(var7);
            }

            Log.d("FirebaseInstanceId", "topic sync succeeded");
         } else {
            try {
               String var6;
               if((var6 = id.getTokenMasterScope()) != null) {
                  if(this.isLog) {
                     Log.d("FirebaseInstanceId", "get master token succeeded");
                  }

                  reset((Context)this, id);
                  if(isTokenFresh || tokenWrapper == null || tokenWrapper != null && !var6.equals(tokenWrapper.token)) {
                     this.onTokenRefresh();
                  }

               } else {
                  this.handleError(intent, "returned token is null");
               }
            } catch (IOException var15) {
               this.handleError(intent, var15.getMessage());
            } catch (SecurityException var16) {
               Log.e("FirebaseInstanceId", "Unable to get master token", var16);
            }
         }
      }
   }

   static public class Receiver extends BroadcastReceiver {
   
	  @Nullable
	  private static BroadcastReceiver receiver;
	  private int retryDelay;
   
   
	  static synchronized void start(Context ctx, int delay) {
		 if(receiver == null) {
			receiver = new Receiver(delay);
			ctx.getApplicationContext().registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		 }
	  }
   
	  private Receiver(int retry) {
		 this.retryDelay = retry;
	  }
   
	  public void onReceive(Context ctx, Intent intent) {
		 Class var3 = Receiver.class;
		 synchronized(Receiver.class) {
			if(receiver != this) {
			   return;
			}
   
			if(!FirebaseInstanceIdService.isConnected(ctx)) {
			   return;
			}
   
			if(Log.isLoggable("FirebaseInstanceId", 3)) {
			   Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
			}
   
			ctx.getApplicationContext().unregisterReceiver(this);
			receiver = null;
		 }
   
		 ReceiverUtil.getInstance().handleIdEvent(ctx, FirebaseInstanceIdService.createIntentDelay(this.retryDelay));
	  }
   }

   private final void handleError(Intent intent, String var2) {
      boolean var3 = isConnectedInternal(this);
      int var10;
      if(intent == null) {
         var10 = 10;
      } else {
         var10 = intent.getIntExtra("next_retry_delay_in_seconds", 0);
      }

      if(var10 < 10 && !var3) {
         var10 = 30;
      } else if(var10 < 10) {
         var10 = 10;
      } else if(var10 > 28800) {
         var10 = 28800;
      }

      int delay = var10;
      Log.d("FirebaseInstanceId", (new StringBuilder(47 + String.valueOf(var2).length())).append("background sync failed: ").append(var2).append(", retry in ").append(var10).append("s").toString());
      Object var5 = lock;
      synchronized(lock) {
         AlarmManager var9 = (AlarmManager)this.getSystemService("alarm");
         PendingIntent var12 = ReceiverUtil.createIdEventPendingIntent(this, 0, createIntentDelayInternal(delay << 1), 134217728);
         var9.set(3, SystemClock.elapsedRealtime() + (long)(delay * 1000), var12);
         isPending = true;
      }

      if(!var3) {
         if(this.isLog) {
            Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
         }

         Receiver.start(this, delay);
      }

   }

   static void reset(Context ctx, FirebaseInstanceId id) {
      Object var2 = lock;
      synchronized(lock) {
         if(isPending) {
            return;
         }
      }

      TokenWrapper var5;
      if((var5 = id.getTokenWrapper()) == null || var5.shouldRefresh(KeyPairStore.appVersion) || FirebaseInstanceId.getTopicOpQueue().fetchFirst() != null) {
         startRetryRequest(ctx);
      }

   }

   static void startRetryRequest(Context ctx) {
      if(ServiceHelper.detectUnderlyService(ctx) != null) {
         Object var1 = lock;
         synchronized(lock) {
            if(!isPending) {
               ReceiverUtil.getInstance().handleIdEvent(ctx, createIntentDelayInternal(0));
               isPending = true;
            }

         }
      }
   }

   private static Intent createIntentDelayInternal(int delay) {
      Intent var1;
      (var1 = new Intent("ACTION_TOKEN_REFRESH_RETRY")).putExtra("next_retry_delay_in_seconds", delay);
      return var1;
   }

   private static boolean isConnectedInternal(Context ctx) {
      NetworkInfo var1;
      return (var1 = ((ConnectivityManager)ctx.getSystemService("connectivity")).getActiveNetworkInfo()) != null && var1.isConnected();
   }

   @WorkerThread
   public void onTokenRefresh() {}

   // $FF: synthetic method
   static boolean isConnected(Context var0) {
      return isConnectedInternal(var0);
   }

   // $FF: synthetic method
   static Intent createIntentDelay(int delay) {
      return createIntentDelayInternal(delay);
   }

}
