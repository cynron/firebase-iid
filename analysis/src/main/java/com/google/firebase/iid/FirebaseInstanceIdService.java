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
import com.google.firebase.iid.zzj;
import com.google.firebase.iid.zzk;
import com.google.firebase.iid.zzl;
import com.google.firebase.iid.zzq;
import com.google.firebase.iid.zzs;
import com.google.firebase.iid.FirebaseInstanceIdService.zza;
import java.io.IOException;

public class FirebaseInstanceIdService extends zzb {

   @VisibleForTesting
   private static Object zzmjc = new Object();
   @VisibleForTesting
   private static boolean zzmjd = false;
   private boolean zzmje = false;


   public final boolean zzo(Intent var1) {
      this.zzmje = Log.isLoggable("FirebaseInstanceId", 3);
      if(var1.getStringExtra("error") == null && var1.getStringExtra("registration_id") == null) {
         return false;
      } else {
         String var2 = zzp(var1);
         if(this.zzmje) {
            String var10002 = String.valueOf(var2);
            Log.d("FirebaseInstanceId", var10002.length() != 0?"Register result in service ".concat(var10002):new String("Register result in service "));
         }

         this.zzpt(var2);
         zzj.zzbym().zzi(var1);
         return true;
      }
   }

   public void handleIntent(Intent var1) {
      String var2;
      if((var2 = var1.getAction()) == null) {
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
            this.zza(var1, false, false);
            return;
         default:
            String var7 = zzp(var1);
            zzj var8 = this.zzpt(var7);
            String var9 = var1.getStringExtra("CMD");
            if(this.zzmje) {
               String var10 = String.valueOf(var1.getExtras());
               Log.d("FirebaseInstanceId", (new StringBuilder(18 + String.valueOf(var7).length() + String.valueOf(var9).length() + String.valueOf(var10).length())).append("Service command ").append(var7).append(" ").append(var9).append(" ").append(var10).toString());
            }

            if(null != var1.getStringExtra("unregistered")) {
               zzj.zzbyl().zzhu(var7 == null?"":var7);
               zzj.zzbym().zzi(var1);
            } else if("gcm.googleapis.com/refresh".equals(var1.getStringExtra("from"))) {
               zzj.zzbyl().zzhu(var7);
               this.zza(var1, false, true);
            } else if("RST".equals(var9)) {
               var8.zzasq();
               this.zza(var1, true, true);
            } else {
               if("RST_FULL".equals(var9)) {
                  if(!zzj.zzbyl().isEmpty()) {
                     var8.zzasq();
                     zzj.zzbyl().zzasu();
                     this.zza(var1, true, true);
                     return;
                  }
               } else {
                  if("SYNC".equals(var9)) {
                     zzj.zzbyl().zzhu(var7);
                     this.zza(var1, false, true);
                     return;
                  }

                  if("PING".equals(var9)) {
                     Bundle var12 = var1.getExtras();
                     String var13;
                     if((var13 = zzl.zzdg(this)) == null) {
                        Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
                        return;
                     }

                     Intent var14;
                     (var14 = new Intent("com.google.android.gcm.intent.SEND")).setPackage(var13);
                     var14.putExtras(var12);
                     zzl.zzd(this, var14);
                     var14.putExtra("google.to", "google.com/iid");
                     var14.putExtra("google.message_id", zzl.zzast());
                     this.sendOrderedBroadcast(var14, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
                  }
               }

            }
         }
      }
   }

   protected final Intent zzn(Intent var1) {
      return (Intent)zzq.zzbyp().zzmjq.poll();
   }

   private static String zzp(Intent var0) {
      String var1;
      return (var1 = var0.getStringExtra("subtype")) == null?"":var1;
   }

   private final zzj zzpt(String var1) {
      if(var1 == null) {
         return zzj.zza(this, (Bundle)null);
      } else {
         Bundle var2;
         (var2 = new Bundle()).putString("subtype", var1);
         return zzj.zza(this, var2);
      }
   }

   private final void zza(Intent var1, boolean var2, boolean var3) {
      Object var4 = zzmjc;
      synchronized(zzmjc) {
         zzmjd = false;
      }

      if(zzl.zzdg(this) != null) {
         zzs var5;
         FirebaseInstanceId var17;
         if((var5 = (var17 = FirebaseInstanceId.getInstance()).zzbyi()) != null && !var5.zzqa(zzj.zzhtl)) {
            zzk var18;
            for(String var7 = (var18 = FirebaseInstanceId.zzbyk()).zzbyn(); var7 != null; var7 = var18.zzbyn()) {
               String[] var8;
               if((var8 = var7.split("!")).length == 2) {
                  String var9 = var8[0];
                  String var10 = var8[1];

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
                        FirebaseInstanceId.getInstance().zzpr(var10);
                        if(this.zzmje) {
                           Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                        }
                        break;
                     case 1:
                        FirebaseInstanceId.getInstance().zzps(var10);
                        if(this.zzmje) {
                           Log.d("FirebaseInstanceId", "unsubscribe operation succeeded");
                        }
                     }
                  } catch (IOException var13) {
                     this.zza(var1, var13.getMessage());
                     return;
                  }
               }

               var18.zzpu(var7);
            }

            Log.d("FirebaseInstanceId", "topic sync succeeded");
         } else {
            try {
               String var6;
               if((var6 = var17.zzbyj()) != null) {
                  if(this.zzmje) {
                     Log.d("FirebaseInstanceId", "get master token succeeded");
                  }

                  zza((Context)this, var17);
                  if(var3 || var5 == null || var5 != null && !var6.equals(var5.zzkmz)) {
                     this.onTokenRefresh();
                  }

               } else {
                  this.zza(var1, "returned token is null");
               }
            } catch (IOException var15) {
               this.zza(var1, var15.getMessage());
            } catch (SecurityException var16) {
               Log.e("FirebaseInstanceId", "Unable to get master token", var16);
            }
         }
      }
   }

   private final void zza(Intent var1, String var2) {
      boolean var3 = zzem(this);
      int var10;
      if(var1 == null) {
         var10 = 10;
      } else {
         var10 = var1.getIntExtra("next_retry_delay_in_seconds", 0);
      }

      if(var10 < 10 && !var3) {
         var10 = 30;
      } else if(var10 < 10) {
         var10 = 10;
      } else if(var10 > 28800) {
         var10 = 28800;
      }

      int var4 = var10;
      Log.d("FirebaseInstanceId", (new StringBuilder(47 + String.valueOf(var2).length())).append("background sync failed: ").append(var2).append(", retry in ").append(var10).append("s").toString());
      Object var5 = zzmjc;
      synchronized(zzmjc) {
         AlarmManager var9 = (AlarmManager)this.getSystemService("alarm");
         PendingIntent var12 = zzq.zza(this, 0, zzfw(var4 << 1), 134217728);
         var9.set(3, SystemClock.elapsedRealtime() + (long)(var4 * 1000), var12);
         zzmjd = true;
      }

      if(!var3) {
         if(this.zzmje) {
            Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
         }

         zza.zzl(this, var4);
      }

   }

   static void zza(Context var0, FirebaseInstanceId var1) {
      Object var2 = zzmjc;
      synchronized(zzmjc) {
         if(zzmjd) {
            return;
         }
      }

      zzs var5;
      if((var5 = var1.zzbyi()) == null || var5.zzqa(zzj.zzhtl) || FirebaseInstanceId.zzbyk().zzbyn() != null) {
         zzel(var0);
      }

   }

   static void zzel(Context var0) {
      if(zzl.zzdg(var0) != null) {
         Object var1 = zzmjc;
         synchronized(zzmjc) {
            if(!zzmjd) {
               zzq.zzbyp().zze(var0, zzfw(0));
               zzmjd = true;
            }

         }
      }
   }

   private static Intent zzfw(int var0) {
      Intent var1;
      (var1 = new Intent("ACTION_TOKEN_REFRESH_RETRY")).putExtra("next_retry_delay_in_seconds", var0);
      return var1;
   }

   private static boolean zzem(Context var0) {
      NetworkInfo var1;
      return (var1 = ((ConnectivityManager)var0.getSystemService("connectivity")).getActiveNetworkInfo()) != null && var1.isConnected();
   }

   @WorkerThread
   public void onTokenRefresh() {}

   // $FF: synthetic method
   static boolean zzen(Context var0) {
      return zzem(var0);
   }

   // $FF: synthetic method
   static Intent zzfx(int var0) {
      return zzfw(var0);
   }

}
