package com.google.android.gms.internal;

import com.google.android.gms.internal.zzegf;
import com.google.android.gms.internal.zzegg;
import com.google.android.gms.internal.zzegm;
import com.google.android.gms.internal.zzego;
import java.io.IOException;

public final class zzehl extends zzego {

   private static volatile zzehl[] zzngu;
   public String zzngv = "";


   public static zzehl[] zzcer() {
      if(zzngu == null) {
         Object var0 = zzegm.zzndc;
         synchronized(zzegm.zzndc) {
            if(zzngu == null) {
               zzngu = new zzehl[0];
            }
         }
      }

      return zzngu;
   }

   public zzehl() {
      this.zzndd = -1;
   }

   public final void zza(zzegg var1) throws IOException {
      if(this.zzngv != null && !this.zzngv.equals("")) {
         var1.zzl(1, this.zzngv);
      }

      super.zza(var1);
   }

   protected final int zzn() {
      int var1 = super.zzn();
      if(this.zzngv != null && !this.zzngv.equals("")) {
         var1 += zzegg.zzm(1, this.zzngv);
      }

      return var1;
   }

   // $FF: synthetic method
   public final zzego zza(zzegf var1) throws IOException {
      zzegf var3 = var1;
      zzehl var2 = this;

      while(true) {
         int var4;
         switch(var4 = var3.zzcbr()) {
         case 0:
            return var2;
         case 10:
            var2.zzngv = var3.readString();
            break;
         default:
            if(!var3.zzgl(var4)) {
               return var2;
            }
         }
      }
   }
}
