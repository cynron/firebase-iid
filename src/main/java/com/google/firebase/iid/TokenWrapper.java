package com.google.firebase.iid;

import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

final class TokenWrapper {

   private static final long tokenInvalid = TimeUnit.DAYS.toMillis(7L);
   final String token;
   private String appVersion;
   private long timestamp;


   private TokenWrapper(String token, String appVersion, long timestamp) {
      this.token = token;
      this.appVersion = appVersion;
      this.timestamp = timestamp;
   }

   static TokenWrapper decodeToken(String jsonStr) {
      if(TextUtils.isEmpty(jsonStr)) {
         return null;
      } else if(jsonStr.startsWith("{")) {
         try {
            JSONObject var1 = new JSONObject(jsonStr);
            return new TokenWrapper(var1.getString("token"), var1.getString("appVersion"), var1.getLong("timestamp"));
         } catch (JSONException var3) {
            String var2 = String.valueOf(var3);
            Log.w("InstanceID/Store", (new StringBuilder(23 + String.valueOf(var2).length())).append("Failed to parse token: ").append(var2).toString());
            return null;
         }
      } else {
         return new zzs(jsonStr, (String)null, 0L);
      }
   }

   static String encodeToken(String token, String appVer, long ts) {
      try {
         JSONObject var4;
         (var4 = new JSONObject()).put("token", token);
         var4.put("appVersion", appVer);
         var4.put("timestamp", ts);
         return var4.toString();
      } catch (JSONException var6) {
         String var5 = String.valueOf(var6);
         Log.w("InstanceID/Store", (new StringBuilder(24 + String.valueOf(var5).length())).append("Failed to encode token: ").append(var5).toString());
         return null;
      }
   }

   final boolean shouldRefresh(String version) {
      return System.currentTimeMillis() > this.timestamp + tokenInvalid || !version.equals(this.appVersion);
   }

}
