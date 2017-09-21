package com.google.firebase.iid;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.firebase.iid.zzr;

public final class zzk {

   private static final Object zzaqm = new Object();
   private final zzr zzmji;


   zzk(zzr var1) {
      this.zzmji = var1;
   }

   final void zzpq(String var1) {
      Object var2 = zzaqm;
      synchronized(zzaqm) {
         String var4 = this.zzmji.zzhud.getString("topic_operaion_queue", "");
         String var5 = ",";
         String var3 = (new StringBuilder(String.valueOf(var4).length() + String.valueOf(var5).length() + String.valueOf(var1).length())).append(var4).append(var5).append(var1).toString();
         this.zzmji.zzhud.edit().putString("topic_operaion_queue", var3).apply();
      }
   }

   @Nullable
   final String zzbyn() {
      Object var1 = zzaqm;
      synchronized(zzaqm) {
         String var2;
         String[] var3;
         return (var2 = this.zzmji.zzhud.getString("topic_operaion_queue", (String)null)) != null && (var3 = var2.split(",")).length > 1 && !TextUtils.isEmpty(var3[1])?var3[1]:null;
      }
   }

   final boolean zzpu(String var1) {
      Object var2 = zzaqm;
      synchronized(zzaqm) {
         String var3;
         String var10000 = var3 = this.zzmji.zzhud.getString("topic_operaion_queue", "");
         String var10001 = String.valueOf(",");
         String var10002 = String.valueOf(var1);
         if(var10002.length() != 0) {
            var10001 = var10001.concat(var10002);
         } else {
            String var10003 = new String;
            var10002 = var10001;
            var10001 = var10003;
            var10003.<init>(var10002);
         }

         if(var10000.startsWith(var10001)) {
            var10000 = String.valueOf(",");
            var10001 = String.valueOf(var1);
            if(var10001.length() != 0) {
               var10000 = var10000.concat(var10001);
            } else {
               var10002 = new String;
               var10001 = var10000;
               var10000 = var10002;
               var10002.<init>(var10001);
            }

            int var4 = var10000.length();
            var3 = var3.substring(var4);
            this.zzmji.zzhud.edit().putString("topic_operaion_queue", var3).apply();
            return true;
         } else {
            return false;
         }
      }
   }

}
