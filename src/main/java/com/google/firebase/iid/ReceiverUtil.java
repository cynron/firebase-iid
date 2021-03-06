package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceIdInternalReceiver;
import java.util.LinkedList;
import java.util.Queue;

public final class ReceiverUtil {

   private static ReceiverUtil instance;
   private final SimpleArrayMap actionMap = new SimpleArrayMap();
   private Boolean wakelockPerm = null;
   @VisibleForTesting
   final Queue IdEventQueue = new LinkedList();
   @VisibleForTesting
   private Queue msgEventQueue = new LinkedList();


   public static synchronized ReceiverUtil getInstance() {
      if(instance == null) {
         instance = new ReceiverUtil();
      }

      return instance;
   }

   public static PendingIntent createIdEventPendingIntent(Context var0, int var1, Intent var2, int var3) {
      return createPendingIntent(var0, 0, "com.google.firebase.INSTANCE_ID_EVENT", var2, 134217728);
   }

   public static PendingIntent createMsgPendingIntent(Context var0, int var1, Intent var2, int var3) {
      return createPendingIntent(var0, var1, "com.google.firebase.MESSAGING_EVENT", var2, 1073741824);
   }

   private static PendingIntent createPendingIntent(Context var0, int var1, String action, Intent var3, int var4) {
      Intent var5;
      (var5 = new Intent(var0, FirebaseInstanceIdInternalReceiver.class)).setAction(action);
      var5.putExtra("wrapped_intent", var3);
      return PendingIntent.getBroadcast(var0, var1, var5, var4);
   }

   public final Intent pollMsgEventQueue() {
      return (Intent)this.msgEventQueue.poll();
   }

   public final void handleIdEvent(Context var1, Intent var2) {
      this.handleIntent(var1, "com.google.firebase.INSTANCE_ID_EVENT", var2);
   }

   public final int handleIntent(Context var1, String action, Intent var3) {
      byte var5 = -1;
      switch(action.hashCode()) {
      case -842411455:
         if(action.equals("com.google.firebase.INSTANCE_ID_EVENT")) {
            var5 = 0;
         }
         break;
      case 41532704:
         if(action.equals("com.google.firebase.MESSAGING_EVENT")) {
            var5 = 1;
         }
      }

      switch(var5) {
      case 0:
         this.IdEventQueue.offer(var3);
         break;
      case 1:
         this.msgEventQueue.offer(var3);
         break;
      default:
         String var10002 = String.valueOf(action);
         Log.w("FirebaseInstanceId", var10002.length() != 0?"Unknown service action: ".concat(var10002):new String("Unknown service action: "));
         return 500;
      }

      Intent newIntent;
      (newIntent = new Intent(action)).setPackage(var1.getPackageName());
      return this.deliveryToDest(var1, newIntent);
   }

   private final int deliveryToDest(Context var1, Intent var2) {
      Intent var7 = var2;
      ReceiverUtil var5 = this;
      String var8;
      synchronized(this.actionMap) {
         var8 = (String)var5.actionMap.get(var7.getAction());
      }

      label87: {
         String var10002;
         if(var8 == null) {
            ResolveInfo var9;
            if((var9 = var1.getPackageManager().resolveService(var2, 0)) == null || var9.serviceInfo == null) {
               Log.e("FirebaseInstanceId", "Failed to resolve target intent service, skipping classname enforcement");
               break label87;
            }

            ServiceInfo var10 = var9.serviceInfo;
            if(!var1.getPackageName().equals(var10.packageName) || var10.name == null) {
               String var18 = var10.packageName;
               String var12 = var10.name;
               Log.e("FirebaseInstanceId", (new StringBuilder(94 + String.valueOf(var18).length() + String.valueOf(var12).length())).append("Error resolving target intent service, skipping classname enforcement. Resolved service was: ").append(var18).append("/").append(var12).toString());
               break label87;
            }

            var8 = var10.name;
            String var10000;
            if(var10.name.startsWith(".")) {
               var10000 = String.valueOf(var1.getPackageName());
               String var10001 = String.valueOf(var8);
               if(var10001.length() != 0) {
                  var10000 = var10000.concat(var10001);
               } else {
                  var10002 = new String;
                  var10001 = var10000;
                  var10000 = var10002;
                  var10002.<init>(var10001);
               }
            } else {
               var10000 = var8;
            }

            var8 = var10000;
            SimpleArrayMap var11 = this.actionMap;
            synchronized(this.actionMap) {
               var5.actionMap.put(var7.getAction(), var8);
            }
         }

         if(Log.isLoggable("FirebaseInstanceId", 3)) {
            var10002 = String.valueOf(var8);
            Log.d("FirebaseInstanceId", var10002.length() != 0?"Restricting intent to a specific service: ".concat(var10002):new String("Restricting intent to a specific service: "));
         }

         var2.setClassName(var1.getPackageName(), var8);
      }

      try {
         if(this.wakelockPerm == null) {
            this.wakelockPerm = Boolean.valueOf(var1.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") == 0);
         }

         ComponentName var3;
         if(this.wakelockPerm.booleanValue()) {
            var3 = WakefulBroadcastReceiver.startWakefulService(var1, var2);
         } else {
            var3 = var1.startService(var2);
            Log.d("FirebaseInstanceId", "Missing wake lock permission, service start may be delayed");
         }

         if(var3 == null) {
            Log.e("FirebaseInstanceId", "Error while delivering the message: ServiceIntent not found.");
            return 404;
         } else {
            return -1;
         }
      } catch (SecurityException var14) {
         Log.e("FirebaseInstanceId", "Error while delivering the message to the serviceIntent", var14);
         return 401;
      } catch (IllegalStateException var15) {
         String var4 = String.valueOf(var15);
         Log.e("FirebaseInstanceId", (new StringBuilder(45 + String.valueOf(var4).length())).append("Failed to start service while in background: ").append(var4).toString());
         return 402;
      }
   }
}
