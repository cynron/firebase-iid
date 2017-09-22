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
import com.google.firebase.iid.zzj;
import com.google.firebase.iid.zzm;
import com.google.firebase.iid.zzn;
import com.google.firebase.iid.zzo;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.Iterator;
import java.util.Random;

public final class zzl {

   private static String zzhto = null;
   private static boolean zzhtp = false;
   private static int zzhtq = 0;
   private static int zzhtr = 0;
   private static int zzhts = 0;
   private static BroadcastReceiver zzhtt = null;
   private Context zzaie;
   private final SimpleArrayMap zzmjj = new SimpleArrayMap();
   private Messenger zzhri;
   private Messenger zzhtv;
   private MessengerCompat zzhtw;
   private static PendingIntent zzhre;
   private long zzhtx;
   private long zzhty;
   private int zzhtz;
   private int zzhua;
   private long zzhub;


   public zzl(Context var1) {
      this.zzaie = var1;
   }

   public static String zzdg(Context var0) {
      if(zzhto != null) {
         return zzhto;
      } else {
         zzhtq = Process.myUid();
         PackageManager var1;
         PackageManager var2;
         Iterator var3 = (var2 = var1 = var0.getPackageManager()).queryBroadcastReceivers(new Intent("com.google.iid.TOKEN_REQUEST"), 0).iterator();

         boolean var10000;
         while(true) {
            if(var3.hasNext()) {
               String var4 = ((ResolveInfo)var3.next()).activityInfo.packageName;
               if(!zza(var2, var4, "com.google.iid.TOKEN_REQUEST")) {
                  continue;
               }

               zzhtp = true;
               var10000 = true;
               break;
            }

            var10000 = false;
            break;
         }

         if(var10000) {
            return zzhto;
         } else if(!zzp.isAtLeastO() && zza(var1)) {
            return zzhto;
         } else {
            Log.w("InstanceID/Rpc", "Failed to resolve IID implementation package, falling back");
            if(zzb(var1, "com.google.android.gms")) {
               zzhtp = zzp.isAtLeastO();
               return zzhto;
            } else if(!zzp.zzalj() && zzb(var1, "com.google.android.gsf")) {
               zzhtp = false;
               return zzhto;
            } else {
               Log.w("InstanceID/Rpc", "Google Play services is missing, unable to get tokens");
               return null;
            }
         }
      }
   }

   private static boolean zza(PackageManager var0) {
      Iterator var1 = var0.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0).iterator();

      String var2;
      do {
         if(!var1.hasNext()) {
            return false;
         }

         var2 = ((ResolveInfo)var1.next()).serviceInfo.packageName;
      } while(!zza(var0, var2, "com.google.android.c2dm.intent.REGISTER"));

      zzhtp = false;
      return true;
   }

   private static boolean zza(PackageManager var0, String var1, String var2) {
      if(0 == var0.checkPermission("com.google.android.c2dm.permission.SEND", var1)) {
         return zzb(var0, var1);
      } else {
         Log.w("InstanceID/Rpc", (new StringBuilder(56 + String.valueOf(var1).length() + String.valueOf(var2).length())).append("Possible malicious package ").append(var1).append(" declares ").append(var2).append(" without permission").toString());
         return false;
      }
   }

   private static boolean zzb(PackageManager var0, String var1) {
      try {
         ApplicationInfo var2;
         zzhto = (var2 = var0.getApplicationInfo(var1, 0)).packageName;
         zzhtr = var2.uid;
         return true;
      } catch (NameNotFoundException var3) {
         return false;
      }
   }

   private static String zza(KeyPair var0, String ... var1) {
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
         return FirebaseInstanceId.zzm(var4.sign());
      } catch (GeneralSecurityException var5) {
         Log.e("InstanceID/Rpc", "Unable to sign registration request", var5);
         return null;
      }
   }

   private final void zzass() {
      if(this.zzhri == null) {
         zzdg(this.zzaie);
         this.zzhri = new Messenger(new zzm(this, Looper.getMainLooper()));
      }
   }

   final void zzc(Message var1) {
      if(var1 != null) {
         if(var1.obj instanceof Intent) {
            Intent var2;
            (var2 = (Intent)var1.obj).setExtrasClassLoader(MessengerCompat.class.getClassLoader());
            if(var2.hasExtra("google.messenger")) {
               Parcelable var3;
               if((var3 = var2.getParcelableExtra("google.messenger")) instanceof MessengerCompat) {
                  this.zzhtw = (MessengerCompat)var3;
               }

               if(var3 instanceof Messenger) {
                  this.zzhtv = (Messenger)var3;
               }
            }

            this.zzi((Intent)var1.obj);
         } else {
            Log.w("InstanceID/Rpc", "Dropping invalid message");
         }
      }
   }

   public static synchronized void zzd(Context var0, Intent var1) {
      if(zzhre == null) {
         Intent var2;
         (var2 = new Intent()).setPackage("com.google.example.invalidpackage");
         zzhre = PendingIntent.getBroadcast(var0, 0, var2, 0);
      }

      var1.putExtra("app", zzhre);
   }

   private final void zzb(String var1, Intent var2) {
      SimpleArrayMap var3 = this.zzmjj;
      synchronized(this.zzmjj) {
         com.google.firebase.iid.zzp var4;
         if((var4 = (com.google.firebase.iid.zzp)this.zzmjj.remove(var1)) == null) {
            String var10002 = String.valueOf(var1);
            Log.w("InstanceID/Rpc", var10002.length() != 0?"Missing callback for ".concat(var10002):new String("Missing callback for "));
         } else {
            var4.zzq(var2);
         }
      }
   }

   private final void zzbk(String var1, String var2) {
      SimpleArrayMap var3 = this.zzmjj;
      synchronized(this.zzmjj) {
         if(var1 == null) {
            for(int var4 = 0; var4 < this.zzmjj.size(); ++var4) {
               ((com.google.firebase.iid.zzp)this.zzmjj.valueAt(var4)).onError(var2);
            }

            this.zzmjj.clear();
         } else {
            com.google.firebase.iid.zzp var7;
            if((var7 = (com.google.firebase.iid.zzp)this.zzmjj.remove(var1)) == null) {
               String var10002 = String.valueOf(var1);
               Log.w("InstanceID/Rpc", var10002.length() != 0?"Missing callback for ".concat(var10002):new String("Missing callback for "));
               return;
            }

            var7.onError(var2);
         }

      }
   }

   final void zzi(Intent var1) {
      if(var1 == null) {
         if(Log.isLoggable("InstanceID/Rpc", 3)) {
            Log.d("InstanceID/Rpc", "Unexpected response: null");
         }

      } else {
         String var2 = var1.getAction();
         String var10002;
         if(!"com.google.android.c2dm.intent.REGISTRATION".equals(var2)) {
            if(Log.isLoggable("InstanceID/Rpc", 3)) {
               var10002 = String.valueOf(var1.getAction());
               Log.d("InstanceID/Rpc", var10002.length() != 0?"Unexpected response ".concat(var10002):new String("Unexpected response "));
            }

         } else {
            String var3;
            if((var3 = var1.getStringExtra("registration_id")) == null) {
               var3 = var1.getStringExtra("unregistered");
            }

            if(var3 != null) {
               this.zzhtx = SystemClock.elapsedRealtime();
               this.zzhub = 0L;
               this.zzhtz = 0;
               this.zzhua = 0;
               String var4 = null;
               if(var3.startsWith("|")) {
                  String[] var5 = var3.split("\\|");
                  if(!"ID".equals(var5[1])) {
                     var10002 = String.valueOf(var3);
                     Log.w("InstanceID/Rpc", var10002.length() != 0?"Unexpected structured response ".concat(var10002):new String("Unexpected structured response "));
                  }

                  var4 = var5[2];
                  if(var5.length > 4) {
                     if("SYNC".equals(var5[3])) {
                        FirebaseInstanceId.zzej(this.zzaie);
                     } else if("RST".equals(var5[3])) {
                        Context var10000 = this.zzaie;
                        zzj.zza(this.zzaie, (Bundle)null);
                        FirebaseInstanceId.zza(var10000, zzj.zzbyl());
                        var1.removeExtra("registration_id");
                        this.zzb(var4, var1);
                        return;
                     }
                  }

                  if((var3 = var5[var5.length - 1]).startsWith(":")) {
                     var3 = var3.substring(1);
                  }

                  var1.putExtra("registration_id", var3);
               }

               if(var4 == null) {
                  if(Log.isLoggable("InstanceID/Rpc", 3)) {
                     Log.d("InstanceID/Rpc", "Ignoring response without a request ID");
                  }

               } else {
                  this.zzb(var4, var1);
               }
            } else {
               String var8;
               String var9;
               if((var8 = var1.getStringExtra("error")) == null) {
                  var9 = String.valueOf(var1.getExtras());
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

                     var1.putExtra("error", var8);
                  }

                  this.zzbk(var9, var8);
                  long var16;
                  if((var16 = var1.getLongExtra("Retry-After", 0L)) > 0L) {
                     this.zzhty = SystemClock.elapsedRealtime();
                     this.zzhua = (int)var16 * 1000;
                     this.zzhub = SystemClock.elapsedRealtime() + (long)this.zzhua;
                     int var12 = this.zzhua;
                     Log.w("InstanceID/Rpc", (new StringBuilder(52)).append("Explicit request from server to backoff: ").append(var12).toString());
                  } else {
                     if(("SERVICE_NOT_AVAILABLE".equals(var8) || "AUTHENTICATION_FAILED".equals(var8)) && "com.google.android.gsf".equals(zzhto)) {
                        ++this.zzhtz;
                        if(this.zzhtz >= 3) {
                           if(this.zzhtz == 3) {
                              this.zzhua = 1000 + (new Random()).nextInt(1000);
                           }

                           this.zzhua <<= 1;
                           this.zzhub = SystemClock.elapsedRealtime() + (long)this.zzhua;
                           int var15 = this.zzhua;
                           Log.w("InstanceID/Rpc", (new StringBuilder(31 + String.valueOf(var8).length())).append("Backoff due to ").append(var8).append(" for ").append(var15).toString());
                        }
                     }

                  }
               }
            }
         }
      }
   }

   final Intent zza(Bundle var1, KeyPair var2) throws IOException {
      Intent var3;
      if((var3 = this.zzb(var1, var2)) != null && var3.hasExtra("google.messenger") && (var3 = this.zzb(var1, var2)) != null && var3.hasExtra("google.messenger")) {
         var3 = null;
      }

      return var3;
   }

   public static synchronized String zzast() {
      return Integer.toString(zzhts++);
   }

   private final Intent zzb(Bundle var1, KeyPair var2) throws IOException {
      String var3 = zzast();
      zzo var4 = new zzo((zzm)null);
      SimpleArrayMap var5 = this.zzmjj;
      synchronized(this.zzmjj) {
         this.zzmjj.put(var3, var4);
      }

      long var15 = SystemClock.elapsedRealtime();
      if(this.zzhub != 0L && var15 <= this.zzhub) {
         long var44 = this.zzhub - var15;
         int var19 = this.zzhua;
         Log.w("InstanceID/Rpc", (new StringBuilder(78)).append("Backoff mode, next request attempt: ").append(var44).append(" interval: ").append(var19).toString());
         throw new IOException("RETRY_LATER");
      } else {
         this.zzass();
         if(zzhto == null) {
            throw new IOException("MISSING_INSTANCEID_SERVICE");
         } else {
            this.zzhtx = SystemClock.elapsedRealtime();
            Intent var17;
            (var17 = new Intent(zzhtp?"com.google.iid.TOKEN_REQUEST":"com.google.android.c2dm.intent.REGISTER")).setPackage(zzhto);
            var1.putString("gmsv", Integer.toString(FirebaseInstanceId.zzap(this.zzaie, zzdg(this.zzaie))));
            var1.putString("osv", Integer.toString(VERSION.SDK_INT));
            var1.putString("app_ver", Integer.toString(FirebaseInstanceId.zzei(this.zzaie)));
            var1.putString("app_ver_name", FirebaseInstanceId.zzde(this.zzaie));
            var1.putString("cliv", "fiid-11200000");
            var1.putString("appid", FirebaseInstanceId.zza(var2));
            String var18 = FirebaseInstanceId.zzm(var2.getPublic().getEncoded());
            var1.putString("pub2", var18);
            var1.putString("sig", zza(var2, new String[]{this.zzaie.getPackageName(), var18}));
            var17.putExtras(var1);
            zzd(this.zzaie, var17);
            zzl var20 = this;
            this.zzhtx = SystemClock.elapsedRealtime();
            var17.putExtra("kid", (new StringBuilder(5 + String.valueOf(var3).length())).append("|ID|").append(var3).append("|").toString());
            var17.putExtra("X-kid", (new StringBuilder(5 + String.valueOf(var3).length())).append("|ID|").append(var3).append("|").toString());
            boolean var23 = "com.google.android.gsf".equals(zzhto);
            if(Log.isLoggable("InstanceID/Rpc", 3)) {
               String var24 = String.valueOf(var17.getExtras());
               Log.d("InstanceID/Rpc", (new StringBuilder(8 + String.valueOf(var24).length())).append("Sending ").append(var24).toString());
            }

            if(var23) {
               zzl var25 = this;
               synchronized(this) {
                  if(zzhtt == null) {
                     zzhtt = new zzn(var25);
                     if(Log.isLoggable("InstanceID/Rpc", 3)) {
                        Log.d("InstanceID/Rpc", "Registered GSF callback receiver");
                     }

                     IntentFilter var27;
                     (var27 = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION")).addCategory(var25.zzaie.getPackageName());
                     var25.zzaie.registerReceiver(zzhtt, var27, "com.google.android.c2dm.permission.SEND", (Handler)null);
                  }
               }

               this.zzaie.startService(var17);
            } else {
               label217: {
                  var17.putExtra("google.messenger", this.zzhri);
                  if(this.zzhtv != null || this.zzhtw != null) {
                     Message var42;
                     (var42 = Message.obtain()).obj = var17;

                     try {
                        if(var20.zzhtv != null) {
                           var20.zzhtv.send(var42);
                        } else {
                           var20.zzhtw.send(var42);
                        }
                        break label217;
                     } catch (RemoteException var40) {
                        if(Log.isLoggable("InstanceID/Rpc", 3)) {
                           Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                        }
                     }
                  }

                  if(zzhtp) {
                     this.zzaie.sendBroadcast(var17);
                  } else {
                     this.zzaie.startService(var17);
                  }
               }
            }

            boolean var35 = false;

            Intent var43;
            try {
               var35 = true;
               var43 = var4.zzbyo();
               var35 = false;
            } finally {
               if(var35) {
                  SimpleArrayMap var9 = this.zzmjj;
                  synchronized(this.zzmjj) {
                     this.zzmjj.remove(var3);
                  }
               }
            }

            SimpleArrayMap var6 = this.zzmjj;
            synchronized(this.zzmjj) {
               this.zzmjj.remove(var3);
               return var43;
            }
         }
      }
   }

}
