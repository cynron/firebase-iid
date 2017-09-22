package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.zzj;
import com.google.firebase.iid.zzk;
import com.google.firebase.iid.zzq;
import com.google.firebase.iid.zzr;
import com.google.firebase.iid.zzs;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class FirebaseInstanceId {

   private static Map zzhtf = new ArrayMap();
   private static zzk zzmiw;
   private final FirebaseApp zzmix;
   private final zzj zzmiy;
   private final String zzmiz;


   public static FirebaseInstanceId getInstance() {
      return getInstance(FirebaseApp.getInstance());
   }

   @Keep
   public static synchronized FirebaseInstanceId getInstance(@NonNull FirebaseApp var0) {
      FirebaseInstanceId var1;
      if((var1 = (FirebaseInstanceId)zzhtf.get(var0.getOptions().getApplicationId())) == null) {
         zzj var2 = zzj.zza(var0.getApplicationContext(), (Bundle)null);
         if(zzmiw == null) {
            zzmiw = new zzk(zzj.zzbyl());
         }

         var1 = new FirebaseInstanceId(var0, var2);
         zzhtf.put(var0.getOptions().getApplicationId(), var1);
      }

      return var1;
   }

   private FirebaseInstanceId(FirebaseApp var1, zzj var2) {
      this.zzmix = var1;
      this.zzmiy = var2;
      String var4;
      String var5;
      String[] var6;
      String var7;
      this.zzmiz = (var4 = this.zzmix.getOptions().getGcmSenderId()) != null?var4:((var5 = this.zzmix.getOptions().getApplicationId()).startsWith("1:")?((var6 = var5.split(":")).length < 2?null:((var7 = var6[1]).isEmpty()?null:var7)):var5);
      if(this.zzmiz == null) {
         throw new IllegalStateException("IID failing to initialize, FirebaseApp is missing project ID");
      } else {
         FirebaseInstanceIdService.zza(this.zzmix.getApplicationContext(), this);
      }
   }

   public String getId() {
      return zza(this.zzmiy.zzasp());
   }

   public long getCreationTime() {
      return this.zzmiy.getCreationTime();
   }

   public void deleteInstanceId() throws IOException {
      this.zzmiy.zza("*", "*", (Bundle)null);
      this.zzmiy.zzasq();
   }

   @Nullable
   public String getToken() {
      zzs var1;
      if((var1 = this.zzbyi()) == null || var1.zzqa(zzj.zzhtl)) {
         FirebaseInstanceIdService.zzel(this.zzmix.getApplicationContext());
      }

      return var1 != null?var1.zzkmz:null;
   }

   @Nullable
   final zzs zzbyi() {
      return zzj.zzbyl().zzo("", this.zzmiz, "*");
   }

   final String zzbyj() throws IOException {
      return this.getToken(this.zzmiz, "*");
   }

   @WorkerThread
   public String getToken(String var1, String var2) throws IOException {
      Bundle var3 = new Bundle();
      this.zzab(var3);
      return this.zzmiy.getToken(var1, var2, var3);
   }

   @WorkerThread
   public void deleteToken(String var1, String var2) throws IOException {
      Bundle var3 = new Bundle();
      this.zzab(var3);
      this.zzmiy.zza(var1, var2, var3);
   }

   public final void zzpq(String var1) {
      zzmiw.zzpq(var1);
      FirebaseInstanceIdService.zzel(this.zzmix.getApplicationContext());
   }

   static zzk zzbyk() {
      return zzmiw;
   }

   final void zzpr(String var1) throws IOException {
      zzs var2;
      if((var2 = this.zzbyi()) != null && !var2.zzqa(zzj.zzhtl)) {
         Bundle var3;
         Bundle var10000 = var3 = new Bundle();
         String var10002 = String.valueOf("/topics/");
         String var10003 = String.valueOf(var1);
         String var10004;
         if(var10003.length() != 0) {
            var10002 = var10002.concat(var10003);
         } else {
            var10004 = new String;
            var10003 = var10002;
            var10002 = var10004;
            var10004.<init>(var10003);
         }

         var10000.putString("gcm.topic", var10002);
         String var10001 = var2.zzkmz;
         var10002 = String.valueOf("/topics/");
         var10003 = String.valueOf(var1);
         if(var10003.length() != 0) {
            var10002 = var10002.concat(var10003);
         } else {
            var10004 = new String;
            var10003 = var10002;
            var10002 = var10004;
            var10004.<init>(var10003);
         }

         String var6 = var10002;
         String var5 = var10001;
         this.zzab(var3);
         this.zzmiy.zzb(var5, var6, var3);
      } else {
         throw new IOException("token not available");
      }
   }

   final void zzps(String var1) throws IOException {
      zzs var2;
      if((var2 = this.zzbyi()) != null && !var2.zzqa(zzj.zzhtl)) {
         Bundle var3;
         Bundle var10000 = var3 = new Bundle();
         String var10002 = String.valueOf("/topics/");
         String var10003 = String.valueOf(var1);
         String var10004;
         if(var10003.length() != 0) {
            var10002 = var10002.concat(var10003);
         } else {
            var10004 = new String;
            var10003 = var10002;
            var10002 = var10004;
            var10004.<init>(var10003);
         }

         var10000.putString("gcm.topic", var10002);
         zzj var4 = this.zzmiy;
         String var10001 = var2.zzkmz;
         var10002 = String.valueOf("/topics/");
         var10003 = String.valueOf(var1);
         if(var10003.length() != 0) {
            var10002 = var10002.concat(var10003);
         } else {
            var10004 = new String;
            var10003 = var10002;
            var10002 = var10004;
            var10004.<init>(var10003);
         }

         var4.zza(var10001, var10002, var3);
      } else {
         throw new IOException("token not available");
      }
   }

   private final void zzab(Bundle var1) {
      var1.putString("gmp_app_id", this.zzmix.getOptions().getApplicationId());
   }

   static String zzm(byte[] var0) {
      return Base64.encodeToString(var0, 11);
   }

   static String zza(KeyPair var0) {
      byte[] var1 = var0.getPublic().getEncoded();

      try {
         byte[] var2;
         byte var3 = (var2 = MessageDigest.getInstance("SHA1").digest(var1))[0];
         int var5 = 112 + (15 & var3);
         var2[0] = (byte)var5;
         return Base64.encodeToString(var2, 0, 8, 11);
      } catch (NoSuchAlgorithmException var4) {
         Log.w("FirebaseInstanceId", "Unexpected error, device missing required alghorithms");
         return null;
      }
   }

   static int zzei(Context var0) {
      return zzap(var0, var0.getPackageName());
   }

   static int zzap(Context var0, String var1) {
      try {
         return var0.getPackageManager().getPackageInfo(var1, 0).versionCode;
      } catch (NameNotFoundException var4) {
         String var3 = String.valueOf(var4);
         Log.w("FirebaseInstanceId", (new StringBuilder(23 + String.valueOf(var3).length())).append("Failed to find package ").append(var3).toString());
         return 0;
      }
   }

   static String zzde(Context var0) {
      try {
         return var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0).versionName;
      } catch (NameNotFoundException var3) {
         String var2 = String.valueOf(var3);
         Log.w("FirebaseInstanceId", (new StringBuilder(38 + String.valueOf(var2).length())).append("Never happens: can\'t find own package ").append(var2).toString());
         return null;
      }
   }

   static void zza(Context var0, zzr var1) {
      var1.zzasu();
      Intent var2;
      (var2 = new Intent()).putExtra("CMD", "RST");
      zzq.zzbyp().zze(var0, var2);
   }

   static void zzej(Context var0) {
      Intent var1;
      (var1 = new Intent()).putExtra("CMD", "SYNC");
      zzq.zzbyp().zze(var0, var1);
   }

}
