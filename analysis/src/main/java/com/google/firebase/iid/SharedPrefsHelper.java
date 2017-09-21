package com.google.firebase.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzt;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.KeyPairUtil;
import com.google.firebase.iid.zzs;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;

final class SharedPrefsHelper {

   SharedPreferences prefs;
   private Context ctx;


   public SharedPrefsHelper(Context ctx) {
      this(ctx, "com.google.android.gms.appid");
   }

   private SharedPrefsHelper(Context ctx, String var2) {
      this.ctx = ctx;
      this.prefs = ctx.getSharedPreferences(var2, 0);
      String var10001 = String.valueOf(var2);
      String var10002 = String.valueOf("-no-backup");
      if(var10002.length() != 0) {
         var10001 = var10001.concat(var10002);
      } else {
         String var10003 = new String;
         var10002 = var10001;
         var10001 = var10003;
         var10003.<init>(var10002);
      }

      String var4 = var10001;
      SharedPrefsHelper var3 = this;
      File var5 = zzt.getNoBackupFilesDir(this.ctx);
      File var6;
      if(!(var6 = new File(var5, var4)).exists()) {
         try {
            if(var6.createNewFile() && !var3.isEmpty()) {
               Log.i("InstanceID/Store", "App restored, clearing state");
               FirebaseInstanceId.zza(var3.zzaie, var3);
            }

            return;
         } catch (IOException var8) {
            if(Log.isLoggable("InstanceID/Store", 3)) {
               var10002 = String.valueOf(var8.getMessage());
               Log.d("InstanceID/Store", var10002.length() != 0?"Error creating file in no backup dir: ".concat(var10002):new String("Error creating file in no backup dir: "));
            }
         }
      }

   }

   public final synchronized long zzpv(String var1) {
      String var2 = zzbl(var1, "cre");
      String var3;
      if((var3 = this.prefs.getString(var2, (String)null)) != null) {
         try {
            return Long.parseLong(var3);
         } catch (NumberFormatException var4) {
            ;
         }
      }

      return 0L;
   }

   public final synchronized boolean isEmpty() {
      return this.prefs.getAll().isEmpty();
   }

   private static String zzn(String var0, String var1, String var2) {
      String var3 = "|T|";
      return (new StringBuilder(1 + String.valueOf(var0).length() + String.valueOf(var3).length() + String.valueOf(var1).length() + String.valueOf(var2).length())).append(var0).append(var3).append(var1).append("|").append(var2).toString();
   }

   private static String zzbl(String var0, String var1) {
      String var2 = "|S|";
      return (new StringBuilder(String.valueOf(var0).length() + String.valueOf(var2).length() + String.valueOf(var1).length())).append(var0).append(var2).append(var1).toString();
   }

   private final void zzht(String var1) {
      Editor var2 = this.prefs.edit();
      Iterator var3 = this.prefs.getAll().keySet().iterator();

      while(var3.hasNext()) {
         String var4;
         if((var4 = (String)var3.next()).startsWith(var1)) {
            var2.remove(var4);
         }
      }

      var2.commit();
   }

   public final synchronized void zzasu() {
      this.prefs.edit().clear().commit();
   }

   public final synchronized zzs zzo(String var1, String var2, String var3) {
      return zzs.zzpz(this.prefs.getString(zzn(var1, var2, var3), (String)null));
   }

   public final synchronized void zza(String var1, String var2, String var3, String var4, String var5) {
      String var6;
      if((var6 = zzs.zzc(var4, var5, System.currentTimeMillis())) != null) {
         Editor var7;
         (var7 = this.prefs.edit()).putString(zzn(var1, var2, var3), var6);
         var7.commit();
      }
   }

   public final synchronized void zzf(String var1, String var2, String var3) {
      String var4 = zzn(var1, var2, var3);
      Editor var5;
      (var5 = this.prefs.edit()).remove(var4);
      var5.commit();
   }

   final synchronized KeyPair zzpw(String var1) {
      KeyPair keyPair = KeyPairUtil.generateKeyPair();
      long var3 = System.currentTimeMillis();
      Editor var5;
      (var5 = this.prefs.edit()).putString(zzbl(var1, "|P|"), FirebaseInstanceId.zzm(keyPair.getPublic().getEncoded()));
      var5.putString(zzbl(var1, "|K|"), FirebaseInstanceId.zzm(keyPair.getPrivate().getEncoded()));
      var5.putString(zzbl(var1, "cre"), Long.toString(var3));
      var5.commit();
      return keyPair;
   }

   final synchronized void zzpx(String var1) {
      this.zzht(String.valueOf(var1).concat("|"));
   }

   public final synchronized void zzhu(String var1) {
      this.zzht(String.valueOf(var1).concat("|T|"));
   }

   public final synchronized KeyPair zzpy(String var1) {
      String var2 = this.prefs.getString(zzbl(var1, "|P|"), (String)null);
      String var3 = this.prefs.getString(zzbl(var1, "|K|"), (String)null);
      if(var2 != null && var3 != null) {
         try {
            byte[] var4 = Base64.decode(var2, 8);
            byte[] var10 = Base64.decode(var3, 8);
            KeyFactory var6;
            PublicKey var7 = (var6 = KeyFactory.getInstance("RSA")).generatePublic(new X509EncodedKeySpec(var4));
            PrivateKey var8 = var6.generatePrivate(new PKCS8EncodedKeySpec(var10));
            return new KeyPair(var7, var8);
         } catch (NoSuchAlgorithmException var9) {
            String var5 = String.valueOf(var9);
            Log.w("InstanceID/Store", (new StringBuilder(19 + String.valueOf(var5).length())).append("Invalid key stored ").append(var5).toString());
            FirebaseInstanceId.zza(this.ctx, this);
            return null;
         }
      } else {
         return null;
      }
   }
}
