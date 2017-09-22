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
import com.google.firebase.iid.KeyPairStore;
import com.google.firebase.iid.TopicOpQueue;
import com.google.firebase.iid.TokenWrapper;
import com.google.firebase.iid.SharedPrefsHelper;
import com.google.firebase.iid.TokenWrapper;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class FirebaseInstanceId {

   private static Map instMap = new ArrayMap();
   private static TopicOpQueue topicOpQueue;
   private final FirebaseApp app;
   private final KeyPairStore keyPairStore;
   private final String projectId;

   public static FirebaseInstanceId getInstance() {
      return getInstance(FirebaseApp.getInstance());
   }

   @Keep
   public static synchronized FirebaseInstanceId getInstance(@NonNull FirebaseApp var0) {
      FirebaseInstanceId var1;
      if((var1 = (FirebaseInstanceId)instMap.get(var0.getOptions().getApplicationId())) == null) {
         KeyPairStore var2 = KeyPairStore.createOrGetKeyPairStoreForBundle(var0.getApplicationContext(), (Bundle)null);
         if(topicOpQueue == null) {
            topicOpQueue = new TopicOpQueue(KeyPairStore.getPrefs());
         }

         var1 = new FirebaseInstanceId(var0, var2);
         instMap.put(var0.getOptions().getApplicationId(), var1);
      }

      return var1;
   }

   private FirebaseInstanceId(FirebaseApp app, KeyPairStore keyPairStore) {
      this.app = app;
      this.keyPairStore = keyPairStore;
      String var4;
      String var5;
      String[] var6;
      String var7;
      this.projectId = (var4 = this.app.getOptions().getGcmSenderId()) != null?var4:((var5 = this.app.getOptions().getApplicationId()).startsWith("1:")?((var6 = var5.split(":")).length < 2?null:((var7 = var6[1]).isEmpty()?null:var7)):var5);
      if(this.projectId == null) {
         throw new IllegalStateException("IID failing to initialize, FirebaseApp is missing project ID");
      } else {
         FirebaseInstanceIdService.reset(this.app.getApplicationContext(), this);
      }
   }

   public String getId() {
      return generateIdFromKeyPair(this.keyPairStore.getKeyPair());
   }

   public long getCreationTime() {
      return this.keyPairStore.getCreationTime();
   }

   public void deleteInstanceId() throws IOException {
      this.keyPairStore.deleteTokenRequest("*", "*", (Bundle)null);
      this.keyPairStore.removeInstanceId();
   }

   @Nullable
   public String getToken() {
      TokenWrapper tokenWrapper;
      if((tokenWrapper = this.getTokenWrapper()) == null || tokenWrapper.shouldRefresh(KeyPairStore.appVersion)) {
         FirebaseInstanceIdService.startRetryRequest(this.app.getApplicationContext());
      }

      return tokenWrapper != null?tokenWrapper.token:null;
   }

   @Nullable
   final TokenWrapper getTokenWrapper() {
      return KeyPairStore.getPrefs().zzo("", this.projectId, "*");
   }

   final String getTokenMasterScope() throws IOException {
      return this.getToken(this.projectId, "*");
   }

   @WorkerThread
   public String getToken(String authorizedEntity, String scope) throws IOException {
      Bundle bundle = new Bundle();
      this.setAppIdToBundle(bundle);
      return this.keyPairStore.getTokenRequest(authorizedEntity, scope, bundle);
   }

   @WorkerThread
   public void deleteToken(String authorizedEntity, String scope) throws IOException {
      Bundle bundle = new Bundle();
      this.setAppIdToBundle(bundle);
      this.keyPairStore.deleteTokenRequest(authorizedEntity, scope, bundle);
   }

   public final void addToTopicOpQueue(String var1) {
      topicOpQueue.append(var1);
      FirebaseInstanceIdService.startRetryRequest(this.app.getApplicationContext());
   }

   static TopicOpQueue getTopicOpQueue() {
      return topicOpQueue;
   }

   final void requestTopicToken(String topic) throws IOException {
      TokenWrapper var2;
      if((var2 = this.getTokenWrapper()) != null && !var2.shouldRefresh(KeyPairStore.appVersion)) {
         Bundle bundle;
         Bundle var10000 = bundle = new Bundle();
         String var10002 = String.valueOf("/topics/");
         String var10003 = String.valueOf(topic);
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
         String token = var2.token;
         var10002 = String.valueOf("/topics/");
         var10003 = String.valueOf(topic);
         if(var10003.length() != 0) {
            var10002 = var10002.concat(var10003);
         } else {
            var10004 = new String;
            var10003 = var10002;
            var10002 = var10004;
            var10004.<init>(var10003);
         }

         String var6 = var10002;
         String var5 = token;
         this.setAppIdToBundle(bundle);
         this.keyPairStore.tokenRequest(var5, var6, bundle);
      } else {
         throw new IOException("token not available");
      }
   }

   final void deleteTopicToken(String topic) throws IOException {
      TokenWrapper var2;
      if((var2 = this.getTokenWrapper()) != null && !var2.shouldRefresh(KeyPairStore.appVersion)) {
         Bundle var3;
         Bundle var10000 = var3 = new Bundle();
         String var10002 = String.valueOf("/topics/");
         String var10003 = String.valueOf(topic);
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
         KeyPairStore var4 = this.keyPairStore;
         String var10001 = var2.token;
         var10002 = String.valueOf("/topics/");
         var10003 = String.valueOf(topic);
         if(var10003.length() != 0) {
            var10002 = var10002.concat(var10003);
         } else {
            var10004 = new String;
            var10003 = var10002;
            var10002 = var10004;
            var10004.<init>(var10003);
         }

         var4.deleteTokenRequest(var10001, var10002, var3);
      } else {
         throw new IOException("token not available");
      }
   }

   private final void setAppIdToBundle(Bundle var1) {
      var1.putString("gmp_app_id", this.app.getOptions().getApplicationId());
   }

   static String base64Encode(byte[] var0) {
      return Base64.encodeToString(var0, 11);
   }

   static String generateIdFromKeyPair(KeyPair var0) {
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

   static int getVersionCode(Context ctx) {
      return getPkgVersionCode(ctx, ctx.getPackageName());
   }

   static int getPkgVersionCode(Context ctx, String pkgName) {
      try {
         return ctx.getPackageManager().getPackageInfo(pkgName, 0).versionCode;
      } catch (NameNotFoundException var4) {
         String var3 = String.valueOf(var4);
         Log.w("FirebaseInstanceId", (new StringBuilder(23 + String.valueOf(var3).length())).append("Failed to find package ").append(var3).toString());
         return 0;
      }
   }

   static String getVersionName(Context ctx) {
      try {
         return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
      } catch (NameNotFoundException var3) {
         String var2 = String.valueOf(var3);
         Log.w("FirebaseInstanceId", (new StringBuilder(38 + String.valueOf(var2).length())).append("Never happens: can\'t find own package ").append(var2).toString());
         return null;
      }
   }

   static void resetState(Context ctx, SharePrefsHelper pref) {
      pref.clear();
      Intent var2;
      (var2 = new Intent()).putExtra("CMD", "RST");
      ReceiverUtil.getInstance().zze(ctx, var2);
   }

   static void syncCommand(Context ctx) {
      Intent var1;
      (var1 = new Intent()).putExtra("CMD", "SYNC");
      ReceiverUtil.getInstance().zze(ctx, var1);
   }

}
