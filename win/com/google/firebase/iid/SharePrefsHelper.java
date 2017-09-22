package com.google.firebase.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzt;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.KeyPairUtil;
import com.google.firebase.iid.TokenWrapper;
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

final class SharePrefsHelper {

   SharedPreferences prefs;
   private Context ctx;


   public SharePrefsHelper(Context ctx) {
      this(ctx, "com.google.android.gms.appid");
   }

   private SharePrefsHelper(Context ctx, String name) {
      this.ctx = ctx;
      this.prefs = ctx.getSharedPreferences(name, 0);
      String nameStr = String.valueOf(name);
      String bakNameStr = String.valueOf("-no-backup");
      if(bakNameStr.length() != 0) {
         nameStr = nameStr.concat(bakNameStr);
      } else {
         String var10003 = new String;
         bakNameStr = nameStr;
         nameStr = var10003;
         var10003.<init>(bakNameStr);
      }

      String var4 = nameStr;

      File var5 = zzt.getNoBackupFilesDir(this.ctx);
      File var6;
      if(!(var6 = new File(var5, var4)).exists()) {
         try {
            if(var6.createNewFile() && !this.isEmpty()) {
               Log.i("InstanceID/Store", "App restored, clearing state");
               FirebaseInstanceId.resetState(this.ctx, this);
            }

            return;
         } catch (IOException var8) {
            if(Log.isLoggable("InstanceID/Store", 3)) {
               bakNameStr = String.valueOf(var8.getMessage());
               Log.d("InstanceID/Store", bakNameStr.length() != 0?"Error creating file in no backup dir: ".concat(bakNameStr):new String("Error creating file in no backup dir: "));
            }
         }
      }

   }

   public final synchronized long getCreationTime(String projectId) {
      String var2 = makeKeyForKeyPair(projectId, "cre");
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

   private static String makeKeyForToken(String subType, String authorizedEntity, String scope) {
      /* <subType>|T|<authorizedEntity>|scope */
	  return subType + "|T|" + authorizedEntity + "|" + scope;
   }

   private static String makeKeyForKeyPair(String projectId, String item) {
	  /* <projectId>|S|<item> */
	  return projectId + "|S|" + item;
   }

   private final void removeKeyStartWith(String prefix) {
      Editor var2 = this.prefs.edit();
      Iterator var3 = this.prefs.getAll().keySet().iterator();

      while(var3.hasNext()) {
         String var4;
         if((var4 = (String)var3.next()).startsWith(prefix)) {
            var2.remove(var4);
         }
      }

      var2.commit();
   }

   public final synchronized void clear() {
      this.prefs.edit().clear().commit();
   }

   public final synchronized TokenWrapper getTokenWrapper(String subType, String authorizedEntity, String scope) {
      return TokenWrapper.decodeToken(this.prefs.getString(makeKeyForToken(subType, authorizedEntity, scope), (String)null));
   }
 

   public final synchronized void saveToken(String subType, String authorizedEntity, String scope, String token, String appVersion) {
      String tokenStr;
      if((tokenStr = TokenWrapper.encodeToken(token, appVersion, System.currentTimeMillis())) != null) {
         Editor editor;
         (editor = this.prefs.edit()).putString(makeKeyForToken(subType, authorizedEntity, scope), tokenStr);
         editor.commit();
      }
   }

   public final synchronized void removeToken(String subType, String authorizedEntity, String scope) {
      String key = makeKeyForToken(subType, authorizedEntity, scope);
      Editor editor;
      (editor = this.prefs.edit()).remove(key);
      editor.commit();
   }

   final synchronized KeyPair generateNewKeyPair(String projectId) {
      KeyPair var2 = KeyPairUtil.generateNewKeyPair();
      long var3 = System.currentTimeMillis();
      Editor var5;
      (var5 = this.prefs.edit()).putString(makeKeyForKeyPair(projectId, "|P|"), FirebaseInstanceId.base64Encode(var2.getPublic().getEncoded()));
      var5.putString(makeKeyForKeyPair(projectId, "|K|"), FirebaseInstanceId.base64Encode(var2.getPrivate().getEncoded()));
      var5.putString(makeKeyForKeyPair(projectId, "cre"), Long.toString(var3));
      var5.commit();
      return var2;
   }

   final synchronized void removeInstanceId(String projectId) {
      this.removeKeyStartWith(String.valueOf(projectId).concat("|"));
   }

   public final synchronized void removeToken(String subType) {
      this.removeKeyStartWith(String.valueOf(subType).concat("|T|"));
   }

   public final synchronized KeyPair restoreKeyPairFromPrefs(String projectId) {
      String var2 = this.prefs.getString(makeKeyForKeyPair(projectId, "|P|"), (String)null);
      String var3 = this.prefs.getString(makeKeyForKeyPair(projectId, "|K|"), (String)null);
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
            FirebaseInstanceId.resetState(this.ctx, this);
            return null;
         }
      } else {
         return null;
      }
   }
}
