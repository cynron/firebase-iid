package com.google.firebase.iid;

import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

final class zzs {

   private static final long zzmjs = TimeUnit.DAYS.toMillis(7L);
   final String zzkmz;
   private String zzhtl;
   private long timestamp;


   private zzs(String var1, String var2, long var3) {
      this.zzkmz = var1;
      this.zzhtl = var2;
      this.timestamp = var3;
   }

   static zzs zzpz(String var0) {
      if(TextUtils.isEmpty(var0)) {
         return null;
      } else if(var0.startsWith("{")) {
         try {
            JSONObject var1 = new JSONObject(var0);
            return new zzs(var1.getString("token"), var1.getString("appVersion"), var1.getLong("timestamp"));
         } catch (JSONException var3) {
            String var2 = String.valueOf(var3);
            Log.w("InstanceID/Store", (new StringBuilder(23 + String.valueOf(var2).length())).append("Failed to parse token: ").append(var2).toString());
            return null;
         }
      } else {
         return new zzs(var0, (String)null, 0L);
      }
   }

   static String zzc(String var0, String var1, long var2) {
      try {
         JSONObject var4;
         (var4 = new JSONObject()).put("token", var0);
         var4.put("appVersion", var1);
         var4.put("timestamp", var2);
         return var4.toString();
      } catch (JSONException var6) {
         String var5 = String.valueOf(var6);
         Log.w("InstanceID/Store", (new StringBuilder(24 + String.valueOf(var5).length())).append("Failed to encode token: ").append(var5).toString());
         return null;
      }
   }

   final boolean zzqa(String var1) {
      return System.currentTimeMillis() > this.timestamp + zzmjs || !var1.equals(this.zzhtl);
   }

}
