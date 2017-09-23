package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Build.VERSION;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.zzp;
import com.google.android.gms.iid.MessengerCompat;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.KeyPairStore;
import com.google.firebase.iid.MyHandler;
import com.google.firebase.iid.OurIntentReceiver;
import com.google.firebase.iid.IntentReceiver;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.Iterator;
import java.util.Random;

/* zzl.java */
public final class ServiceHelper {

   private static String underlyService = null;
   private static boolean isTokenRequestBroadcast = false;
   private static int myUid = 0;
   private static int serviceUid = 0;
   private static int reqSeq = 0;
   private static BroadcastReceiver receiver = null;
   private Context ctx;
   private final SimpleArrayMap requestMap = new SimpleArrayMap();
   private Messenger recvMessager;
   private Messenger request;
   private MessengerCompat requestCompat;
   private static PendingIntent pendingIntent;
   private long currentTime;
   private long retryStart;
   private int retryTime;
   private int retryAfter;
   private long nextRetryTime;


   public ServiceHelper(Context ctx) {
      this.ctx = ctx;
   }

   public static String detectUnderlyService(Context ctx) {
      if(underlyService != null) {
         return underlyService;
      } else {
         myUid = Process.myUid();
         PackageManager pkgManager1;
         PackageManager pkgManager;
         Iterator it = (pkgManager = pkgManager1 = ctx.getPackageManager()).queryBroadcastReceivers(new Intent("com.google.iid.TOKEN_REQUEST"), 0).iterator();

         boolean success;
         while(true) {
            if(it.hasNext()) {
               String pkgName = ((ResolveInfo)it.next()).activityInfo.packageName;
               if(!resolveUnderlyService(pkgManager, pkgName, "com.google.iid.TOKEN_REQUEST")) {
                  continue;
               }

               isTokenRequestBroadcast = true;
               success = true;
               break;
            }

            success = false;
            break;
         }

         if(success) {
            return underlyService;
         } else if(!zzp.isAtLeastO() && detectForRegister(pkgManager1)) {
            return underlyService;
         } else {
            Log.w("InstanceID/Rpc", "Failed to resolve IID implementation package, falling back");
            if(tryGoogleService(pkgManager1, "com.google.android.gms")) {
               isTokenRequestBroadcast = zzp.isAtLeastO();
               return underlyService;
            } else if(!zzp.zzalj() && tryGoogleService(pkgManager1, "com.google.android.gsf")) {
               isTokenRequestBroadcast = false;
               return underlyService;
            } else {
               Log.w("InstanceID/Rpc", "Google Play services is missing, unable to get tokens");
               return null;
            }
         }
      }
   }

   private static boolean detectForRegister(PackageManager pkgManager) {
      Iterator var1 = pkgManager.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0).iterator();

      String var2;
      do {
         if(!var1.hasNext()) {
            return false;
         }

         var2 = ((ResolveInfo)var1.next()).serviceInfo.packageName;
      } while(!resolveUnderlyService(pkgManager, var2, "com.google.android.c2dm.intent.REGISTER"));

      isTokenRequestBroadcast = false;
      return true;
   }

   private static boolean resolveUnderlyService(PackageManager var0, String var1, String var2) {
      if(0 == var0.checkPermission("com.google.android.c2dm.permission.SEND", var1)) {
         return tryGoogleService(var0, var1);
      } else {
         Log.w("InstanceID/Rpc", (new StringBuilder(56 + String.valueOf(var1).length() + String.valueOf(var2).length())).append("Possible malicious package ").append(var1).append(" declares ").append(var2).append(" without permission").toString());
         return false;
      }
   }

   private static boolean tryGoogleService(PackageManager pkgManager, String pkgName) {
      try {
         ApplicationInfo var2;
         underlyService = (var2 = pkgManager.getApplicationInfo(pkgName, 0)).packageName;
         serviceUid = var2.uid;
         return true;
      } catch (NameNotFoundException var3) {
         return false;
      }
   }

   private static String calcSign(KeyPair var0, String ... var1) {
      byte[] var2;
      try {
         var2 = TextUtils.join("\n", var1).getBytes("UTF-8");
      } catch (UnsupportedEncodingException var6) {
         Log.e("InstanceID/Rpc", "Unable to encode string", var6);
         return null;
      }

      try {
         PrivateKey var3;
         Signature var4;
         (var4 = Signature.getInstance((var3 = var0.getPrivate()) instanceof RSAPrivateKey?"SHA256withRSA":"SHA256withECDSA")).initSign(var3);
         var4.update(var2);
         return FirebaseInstanceId.base64Encode(var4.sign());
      } catch (GeneralSecurityException var5) {
         Log.e("InstanceID/Rpc", "Unable to sign registration request", var5);
         return null;
      }
   }

   private final void setupReceiverMessager() {
      if(this.recvMessager == null) {
         detectUnderlyService(this.ctx);
         this.recvMessager = new Messenger(new MessageHandler(this, Looper.getMainLooper()));
      }
   }

   final void handleMessage(Message msg) {
      if(msg != null) {
         if(msg.obj instanceof Intent) {
            Intent var2;
            (var2 = (Intent)msg.obj).setExtrasClassLoader(MessengerCompat.class.getClassLoader());
            if(var2.hasExtra("google.messenger")) {
               Parcelable var3;
               if((var3 = var2.getParcelableExtra("google.messenger")) instanceof MessengerCompat) {
                  this.requestCompat = (MessengerCompat)var3;
               }

               if(var3 instanceof Messenger) {
                  this.request = (Messenger)var3;
               }
            }

            this.onReceiverIntent((Intent)msg.obj);
         } else {
            Log.w("InstanceID/Rpc", "Dropping invalid message");
         }
      }
   }

   public static synchronized void addPendingIntent(Context ctx, Intent var1) {
      if(pendingIntent == null) {
         Intent var2;
         (var2 = new Intent()).setPackage("com.google.example.invalidpackage");
         pendingIntent = PendingIntent.getBroadcast(ctx, 0, var2, 0);
      }

      var1.putExtra("app", pendingIntent);
   }

   private final void putIntentRespFor(String reqId, Intent intent) {
      SimpleArrayMap var3 = this.requestMap;
      synchronized(this.requestMap) {
         com.google.firebase.iid.IntentReceiver var4;
         if((var4 = (com.google.firebase.iid.IntentReceiver)this.requestMap.remove(reqId)) == null) {
            String var10002 = String.valueOf(reqId);
            Log.w("InstanceID/Rpc", var10002.length() != 0?"Missing callback for ".concat(var10002):new String("Missing callback for "));
         } else {
            var4.onReceiveIntent(intent);
         }
      }
   }

   private final void putErrorResponseFor(String reqId, String errorMsg) {
      SimpleArrayMap var3 = this.requestMap;
      synchronized(this.requestMap) {
         if(reqId == null) {
            for(int var4 = 0; var4 < this.requestMap.size(); ++var4) {
               ((com.google.firebase.iid.IntentReceiver)this.requestMap.valueAt(var4)).onError(errorMsg);
            }

            this.requestMap.clear();
         } else {
            com.google.firebase.iid.IntentReceiver var7;
            if((var7 = (com.google.firebase.iid.IntentReceiver)this.requestMap.remove(reqId)) == null) {
               String var10002 = String.valueOf(reqId);
               Log.w("InstanceID/Rpc", var10002.length() != 0?"Missing callback for ".concat(var10002):new String("Missing callback for "));
               return;
            }

            var7.onError(errorMsg);
         }

      }
   }

   final void onReceiverIntent(Intent intent) {
      if(intent == null) {
         if(Log.isLoggable("InstanceID/Rpc", 3)) {
            Log.d("InstanceID/Rpc", "Unexpected response: null");
         }

      } else {
         String var2 = intent.getAction();
         String var10002;
         if(!"com.google.android.c2dm.intent.REGISTRATION".equals(var2)) {
            if(Log.isLoggable("InstanceID/Rpc", 3)) {
               var10002 = String.valueOf(intent.getAction());
               Log.d("InstanceID/Rpc", var10002.length() != 0?"Unexpected response ".concat(var10002):new String("Unexpected response "));
            }

         } else {
            String var3;
            if((var3 = intent.getStringExtra("registration_id")) == null) {
               var3 = intent.getStringExtra("unregistered");
            }

            if(var3 != null) {
               this.currentTime = SystemClock.elapsedRealtime();
               this.nextRetryTime = 0L;
               this.retryTime = 0;
               this.retryAfter = 0;
               String reqId = null;
               if(var3.startsWith("|")) {
                  String[] var5 = var3.split("\\|");
                  if(!"ID".equals(var5[1])) {
                     var10002 = String.valueOf(var3);
                     Log.w("InstanceID/Rpc", var10002.length() != 0?"Unexpected structured response ".concat(var10002):new String("Unexpected structured response "));
                  }

                  reqId = var5[2];
                  if(var5.length > 4) {
                     if("SYNC".equals(var5[3])) {
                        FirebaseInstanceId.syncCommand(this.ctx);
                     } else if("RST".equals(var5[3])) {
                        Context var10000 = this.ctx;
                        KeyPairStore.createOrGetKeyPairStoreForBundle(this.ctx, (Bundle)null);
                        FirebaseInstanceId.resetState(var10000, KeyPairStore.getPrefs());
                        intent.removeExtra("registration_id");
                        this.putIntentRespFor(reqId, intent);
                        return;
                     }
                  }

                  if((var3 = var5[var5.length - 1]).startsWith(":")) {
                     var3 = var3.substring(1);
                  }

                  intent.putExtra("registration_id", var3);
               }

               if(reqId == null) {
                  if(Log.isLoggable("InstanceID/Rpc", 3)) {
                     Log.d("InstanceID/Rpc", "Ignoring response without a request ID");
                  }

               } else {
                  this.putIntentRespFor(reqId, intent);
               }
            } else {
               String var8;
               String var9;
               if((var8 = intent.getStringExtra("error")) == null) {
                  var9 = String.valueOf(intent.getExtras());
                  Log.w("InstanceID/Rpc", (new StringBuilder(49 + String.valueOf(var9).length())).append("Unexpected response, no error or registration id ").append(var9).toString());
               } else {
                  if(Log.isLoggable("InstanceID/Rpc", 3)) {
                     var10002 = String.valueOf(var8);
                     Log.d("InstanceID/Rpc", var10002.length() != 0?"Received InstanceID error ".concat(var10002):new String("Received InstanceID error "));
                  }

                  var9 = null;
                  if(var8.startsWith("|")) {
                     String[] var10 = var8.split("\\|");
                     if(!"ID".equals(var10[1])) {
                        var10002 = String.valueOf(var8);
                        Log.w("InstanceID/Rpc", var10002.length() != 0?"Unexpected structured response ".concat(var10002):new String("Unexpected structured response "));
                     }

                     if(var10.length > 2) {
                        var9 = var10[2];
                        if((var8 = var10[3]).startsWith(":")) {
                           var8 = var8.substring(1);
                        }
                     } else {
                        var8 = "UNKNOWN";
                     }

                     intent.putExtra("error", var8);
                  }

                  this.putErrorResponseFor(var9, var8);
                  long var16;
                  if((var16 = intent.getLongExtra("Retry-After", 0L)) > 0L) {
                     this.retryStart = SystemClock.elapsedRealtime();
                     this.retryAfter = (int)var16 * 1000;
                     this.nextRetryTime = SystemClock.elapsedRealtime() + (long)this.retryAfter;
                     int var12 = this.retryAfter;
                     Log.w("InstanceID/Rpc", (new StringBuilder(52)).append("Explicit request from server to backoff: ").append(var12).toString());
                  } else {
                     if(("SERVICE_NOT_AVAILABLE".equals(var8) || "AUTHENTICATION_FAILED".equals(var8)) && "com.google.android.gsf".equals(underlyService)) {
                        ++this.retryTime;
                        if(this.retryTime >= 3) {
                           if(this.retryTime == 3) {
                              this.retryAfter = 1000 + (new Random()).nextInt(1000);
                           }

                           this.retryAfter <<= 1;
                           this.nextRetryTime = SystemClock.elapsedRealtime() + (long)this.retryAfter;
                           int var15 = this.retryAfter;
                           Log.w("InstanceID/Rpc", (new StringBuilder(31 + String.valueOf(var8).length())).append("Backoff due to ").append(var8).append(" for ").append(var15).toString());
                        }
                     }

                  }
               }
            }
         }
      }
   }

   final Intent sendTokenRequest(Bundle bundle, KeyPair keyPair) throws IOException {
      Intent resp;
      if((resp = this.sendTokenRequestInternal(bundle, keyPair)) != null && resp.hasExtra("google.messenger")) {
         resp = null;
      }

      return resp;
   }

   public static synchronized String genReqSeq() {
      return Integer.toString(reqSeq++);
   }

   private final Intent sendTokenRequestInternal(Bundle bundle, KeyPair keyPair) throws IOException {
      String reqId = genReqSeq();
      OurIntentReceiver intentRcv = new OurIntentReceiver((MessageHandler)null);
      SimpleArrayMap var5 = this.requestMap;
      synchronized(this.requestMap) {
         this.requestMap.put(reqId, intentRcv);
      }

      long var15 = SystemClock.elapsedRealtime();
      if(this.nextRetryTime != 0L && var15 <= this.nextRetryTime) {
         long var44 = this.nextRetryTime - var15;
         int var19 = this.retryAfter;
         Log.w("InstanceID/Rpc", (new StringBuilder(78)).append("Backoff mode, next request attempt: ").append(var44).append(" interval: ").append(var19).toString());
         throw new IOException("RETRY_LATER");
      } else {
         this.setupReceiverMessager();
         if(underlyService == null) {
            throw new IOException("MISSING_INSTANCEID_SERVICE");
         } else {
            this.currentTime = SystemClock.elapsedRealtime();
            Intent var17;
            (var17 = new Intent(isTokenRequestBroadcast?"com.google.iid.TOKEN_REQUEST":"com.google.android.c2dm.intent.REGISTER")).setPackage(underlyService);
            bundle.putString("gmsv", Integer.toString(FirebaseInstanceId.getPkgVersionCode(this.ctx, detectUnderlyService(this.ctx))));
            bundle.putString("osv", Integer.toString(VERSION.SDK_INT));
            bundle.putString("app_ver", Integer.toString(FirebaseInstanceId.getVersionCode(this.ctx)));
            bundle.putString("app_ver_name", FirebaseInstanceId.getVersionName(this.ctx));
            bundle.putString("cliv", "fiid-11200000");
            bundle.putString("appid", FirebaseInstanceId.generateIdFromKeyPair(keyPair));
            String var18 = FirebaseInstanceId.base64Encode(keyPair.getPublic().getEncoded());
            bundle.putString("pub2", var18);
            bundle.putString("sig", calcSign(keyPair, new String[]{this.ctx.getPackageName(), var18}));
            var17.putExtras(bundle);
            addPendingIntent(this.ctx, var17);
            ServiceHelper var20 = this;
            this.currentTime = SystemClock.elapsedRealtime();
            var17.putExtra("kid", (new StringBuilder(5 + String.valueOf(reqId).length())).append("|ID|").append(reqId).append("|").toString());
            var17.putExtra("X-kid", (new StringBuilder(5 + String.valueOf(reqId).length())).append("|ID|").append(reqId).append("|").toString());
            boolean var23 = "com.google.android.gsf".equals(underlyService);
            if(Log.isLoggable("InstanceID/Rpc", 3)) {
               String var24 = String.valueOf(var17.getExtras());
               Log.d("InstanceID/Rpc", (new StringBuilder(8 + String.valueOf(var24).length())).append("Sending ").append(var24).toString());
            }

            if(var23) {
               synchronized(this) {
                  if(receiver == null) {
                     receiver = new MyReceiver(this);
                     if(Log.isLoggable("InstanceID/Rpc", 3)) {
                        Log.d("InstanceID/Rpc", "Registered GSF callback receiver");
                     }

                     IntentFilter var27;
                     (var27 = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION")).addCategory(this.ctx.getPackageName());
                     this.ctx.registerReceiver(receiver, var27, "com.google.android.c2dm.permission.SEND", (Handler)null);
                  }
               }

               this.ctx.startService(var17);
            } else {
               label217: {
                  var17.putExtra("google.messenger", this.recvMessager);
                  if(this.request != null || this.requestCompat != null) {
                     Message var42;
                     (var42 = Message.obtain()).obj = var17;

                     try {
                        if(var20.request != null) {
                           var20.request.send(var42);
                        } else {
                           var20.requestCompat.send(var42);
                        }
                        break label217;
                     } catch (RemoteException var40) {
                        if(Log.isLoggable("InstanceID/Rpc", 3)) {
                           Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                        }
                     }
                  }

                  if(isTokenRequestBroadcast) {
                     this.ctx.sendBroadcast(var17);
                  } else {
                     this.ctx.startService(var17);
                  }
               }
            }

            boolean var35 = false;

            Intent var43;
            try {
               var35 = true;
               var43 = intentRcv.waitForResult();
               var35 = false;
            } finally {
               if(var35) {
                  SimpleArrayMap var9 = this.requestMap;
                  synchronized(this.requestMap) {
                     this.requestMap.remove(reqId);
                  }
               }
            }

            SimpleArrayMap var6 = this.requestMap;
            synchronized(this.requestMap) {
               this.requestMap.remove(reqId);
               return var43;
            }
         }
      }
   }

}
