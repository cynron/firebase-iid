package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.ServiceHelper;
import com.google.firebase.iid.TokenWrapper;
import com.google.firebase.iid.SharedPrefsHelper;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;

/* zzj */

public final class KeyPairStore {

   private static Map keyPairCacheMap = new ArrayMap();
   private Context mContext;
   private static SharePrefsHelper prefs;
   private static ServiceHelper serviceHelper;
   private KeyPair keyPair;
   private String subType = "";
   static String appVersion;


   private KeyPairStore(Context ctx, String subType, Bundle bundle) {
      this.mContext = ctx.getApplicationContext();
      this.subType = subType;
   }

   public static synchronized KeyPairStore createOrGetKeyPairStoreForBundle(Context ctx, Bundle bundle) {
      String subType;
      if((subType = bundle == null?"":bundle.getString("subtype")) == null) {
         subType = "";
      }

      ctx = ctx.getApplicationContext();
      if(prefs == null) {
         prefs = new SharePrefsHelper(ctx);
         serviceHelper = new ServiceHelper(ctx);
      }

      appVersion = Integer.toString(FirebaseInstanceId.getVersionCode(ctx));
      KeyPairStore keyPairStore;
      if((keyPairStore = (KeyPairStore)keyPairCacheMap.get(subType)) == null) {
         keyPairStore = new KeyPairStore(ctx, subType, bundle);
         keyPairCacheMap.put(subType, keyPairStore);
      }

      return keyPairStore;
   }

   final KeyPair getKeyPair() {
      if(this.keyPair == null) {
         this.keyPair = prefs.restoreKeyPairFromPrefs(this.subType);
      }

      if(this.keyPair == null) {
         this.keyPair = prefs.generateNewKeyPair(this.subType);
      }

      return this.keyPair;
   }

   public final long getCreationTime() {
      return prefs.getCreationTime(this.subType);
   }

   public final void removeInstanceId() {
      prefs.removeInstanceId(this.subType);
      this.keyPair = null;
   }

   public final void deleteTokenRequest(String authorizedEntity, String scope, Bundle bundle) throws IOException {
      if(Looper.getMainLooper() == Looper.myLooper()) {
         throw new IOException("MAIN_THREAD");
      } else {
         prefs.removeToken(this.subType, authorizedEntity, scope);
         if(bundle == null) {
            bundle = new Bundle();
         }

         bundle.putString("delete", "1");
         this.tokenRequest(authorizedEntity, scope, bundle);
      }
   }

   public static SharePrefsHelper getPrefs() {
      return prefs;
   }

   public static ServiceHelper getServiceHelper() {
      return serviceHelper;
   }

   public final String getTokenRequest(String authorizedEntity, String scope, Bundle bundle) throws IOException {
      if(Looper.getMainLooper() == Looper.myLooper()) {
         throw new IOException("MAIN_THREAD");
      } else {
         boolean var4 = true;
         if(bundle.getString("ttl") == null && !"jwt".equals(bundle.getString("type"))) {
            TokenWrapper tokenWrapper;
            if((tokenWrapper = prefs.getTokenWrapper(this.subType, authorizedEntity, scope)) != null && !tokenWrapper.shouldRefresh(appVersion)) {
               return tokenWrapper.token;
            }
         } else {
            var4 = false;
         }

         String token;
         if((token = this.tokenRequest(authorizedEntity, scope, bundle)) != null && var4) {
            prefs.saveToken(this.subType, authorizedEntity, scope, token, appVersion);
         }

         return token;
      }
   }

   public final String tokenRequest(String authorizedEntity, String scope, Bundle bundle) throws IOException {
      if(scope != null) {
         bundle.putString("scope", scope);
      }

      bundle.putString("sender", authorizedEntity);
      String subType = "".equals(this.subType)?authorizedEntity:this.subType;
      bundle.putString("subtype", subType);
      bundle.putString("X-subtype", subType);
      Intent respIntent;
      if((respIntent = serviceHelper.sendTokenRequest(bundle, this.getKeyPair())) == null) {
         throw new IOException("SERVICE_NOT_AVAILABLE");
      } else {
         String var6;
         if((var6 = respIntent.getStringExtra("registration_id")) == null) {
            var6 = respIntent.getStringExtra("unregistered");
         }

         if(var6 == null) {
            if((var6 = respIntent.getStringExtra("error")) != null) {
               throw new IOException(var6);
            } else {
               String var7 = String.valueOf(respIntent.getExtras());
               Log.w("InstanceID/Rpc", (new StringBuilder(29 + String.valueOf(var7).length())).append("Unexpected response from GCM ").append(var7).toString(), new Throwable());
               throw new IOException("SERVICE_NOT_AVAILABLE");
            }
         } else {
            return var6;
         }
      }
   }

}
