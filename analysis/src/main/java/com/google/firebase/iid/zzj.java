package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.zzl;
import com.google.firebase.iid.zzr;
import com.google.firebase.iid.zzs;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;

public final class zzj {

   private static Map zzhtf = new ArrayMap();
   private Context mContext;
   private static zzr zzmjg;
   private static zzl zzmjh;
   private KeyPair zzhti;
   private String zzhtj = "";
   static String zzhtl;


   private zzj(Context var1, String var2, Bundle var3) {
      this.mContext = var1.getApplicationContext();
      this.zzhtj = var2;
   }

   public static synchronized zzj zza(Context var0, Bundle var1) {
      String var2;
      if((var2 = var1 == null?"":var1.getString("subtype")) == null) {
         var2 = "";
      }

      var0 = var0.getApplicationContext();
      if(zzmjg == null) {
         zzmjg = new zzr(var0);
         zzmjh = new zzl(var0);
      }

      zzhtl = Integer.toString(FirebaseInstanceId.zzei(var0));
      zzj var3;
      if((var3 = (zzj)zzhtf.get(var2)) == null) {
         var3 = new zzj(var0, var2, var1);
         zzhtf.put(var2, var3);
      }

      return var3;
   }

   final KeyPair zzasp() {
      if(this.zzhti == null) {
         this.zzhti = zzmjg.zzpy(this.zzhtj);
      }

      if(this.zzhti == null) {
         this.zzhti = zzmjg.zzpw(this.zzhtj);
      }

      return this.zzhti;
   }

   public final long getCreationTime() {
      return zzmjg.zzpv(this.zzhtj);
   }

   public final void zzasq() {
      zzmjg.zzpx(this.zzhtj);
      this.zzhti = null;
   }

   public final void zza(String var1, String var2, Bundle var3) throws IOException {
      if(Looper.getMainLooper() == Looper.myLooper()) {
         throw new IOException("MAIN_THREAD");
      } else {
         zzmjg.zzf(this.zzhtj, var1, var2);
         if(var3 == null) {
            var3 = new Bundle();
         }

         var3.putString("delete", "1");
         this.zzb(var1, var2, var3);
      }
   }

   public static zzr zzbyl() {
      return zzmjg;
   }

   public static zzl zzbym() {
      return zzmjh;
   }

   public final String getToken(String var1, String var2, Bundle var3) throws IOException {
      if(Looper.getMainLooper() == Looper.myLooper()) {
         throw new IOException("MAIN_THREAD");
      } else {
         boolean var4 = true;
         if(var3.getString("ttl") == null && !"jwt".equals(var3.getString("type"))) {
            zzs var5;
            if((var5 = zzmjg.zzo(this.zzhtj, var1, var2)) != null && !var5.zzqa(zzhtl)) {
               return var5.zzkmz;
            }
         } else {
            var4 = false;
         }

         String var6;
         if((var6 = this.zzb(var1, var2, var3)) != null && var4) {
            zzmjg.zza(this.zzhtj, var1, var2, var6, zzhtl);
         }

         return var6;
      }
   }

   public final String zzb(String var1, String var2, Bundle var3) throws IOException {
      if(var2 != null) {
         var3.putString("scope", var2);
      }

      var3.putString("sender", var1);
      String var4 = "".equals(this.zzhtj)?var1:this.zzhtj;
      var3.putString("subtype", var4);
      var3.putString("X-subtype", var4);
      Intent var5;
      if((var5 = zzmjh.zza(var3, this.zzasp())) == null) {
         throw new IOException("SERVICE_NOT_AVAILABLE");
      } else {
         String var6;
         if((var6 = var5.getStringExtra("registration_id")) == null) {
            var6 = var5.getStringExtra("unregistered");
         }

         if(var6 == null) {
            if((var6 = var5.getStringExtra("error")) != null) {
               throw new IOException(var6);
            } else {
               String var7 = String.valueOf(var5.getExtras());
               Log.w("InstanceID/Rpc", (new StringBuilder(29 + String.valueOf(var7).length())).append("Unexpected response from GCM ").append(var7).toString(), new Throwable());
               throw new IOException("SERVICE_NOT_AVAILABLE");
            }
         } else {
            return var6;
         }
      }
   }

}
