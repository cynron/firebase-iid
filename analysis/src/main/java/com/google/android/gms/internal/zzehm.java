package com.google.android.gms.internal;

import com.google.android.gms.internal.zzegf;
import com.google.android.gms.internal.zzegg;
import com.google.android.gms.internal.zzegn;
import com.google.android.gms.internal.zzego;
import com.google.android.gms.internal.zzegr;
import com.google.android.gms.internal.zzehl;
import java.io.IOException;

public final class zzehm extends zzego {

   public String zzngv = "";
   public String zzngw = "";
   public long zzngx = 0L;
   public String zzngy = "";
   public long zzngz = 0L;
   public long zzgbv = 0L;
   public String zznha = "";
   public String zznhb = "";
   public String zznhc = "";
   public String zznhd = "";
   public String zznhe = "";
   public int zznhf = 0;
   public zzehl[] zznhg = zzehl.zzcer();


   public zzehm() {
      this.zzndd = -1;
   }

   public final void zza(zzegg var1) throws IOException {
      if(this.zzngv != null && !this.zzngv.equals("")) {
         var1.zzl(1, this.zzngv);
      }

      if(this.zzngw != null && !this.zzngw.equals("")) {
         var1.zzl(2, this.zzngw);
      }

      if(this.zzngx != 0L) {
         var1.zzb(3, this.zzngx);
      }

      if(this.zzngy != null && !this.zzngy.equals("")) {
         var1.zzl(4, this.zzngy);
      }

      if(this.zzngz != 0L) {
         var1.zzb(5, this.zzngz);
      }

      if(this.zzgbv != 0L) {
         var1.zzb(6, this.zzgbv);
      }

      if(this.zznha != null && !this.zznha.equals("")) {
         var1.zzl(7, this.zznha);
      }

      if(this.zznhb != null && !this.zznhb.equals("")) {
         var1.zzl(8, this.zznhb);
      }

      if(this.zznhc != null && !this.zznhc.equals("")) {
         var1.zzl(9, this.zznhc);
      }

      if(this.zznhd != null && !this.zznhd.equals("")) {
         var1.zzl(10, this.zznhd);
      }

      if(this.zznhe != null && !this.zznhe.equals("")) {
         var1.zzl(11, this.zznhe);
      }

      if(this.zznhf != 0) {
         var1.zzu(12, this.zznhf);
      }

      if(this.zznhg != null && this.zznhg.length > 0) {
         for(int var2 = 0; var2 < this.zznhg.length; ++var2) {
            zzehl var3;
            if((var3 = this.zznhg[var2]) != null) {
               var1.zza(13, var3);
            }
         }
      }

      super.zza(var1);
   }

   protected final int zzn() {
      int var1 = super.zzn();
      if(this.zzngv != null && !this.zzngv.equals("")) {
         var1 += zzegg.zzm(1, this.zzngv);
      }

      if(this.zzngw != null && !this.zzngw.equals("")) {
         var1 += zzegg.zzm(2, this.zzngw);
      }

      if(this.zzngx != 0L) {
         var1 += zzegg.zze(3, this.zzngx);
      }

      if(this.zzngy != null && !this.zzngy.equals("")) {
         var1 += zzegg.zzm(4, this.zzngy);
      }

      if(this.zzngz != 0L) {
         var1 += zzegg.zze(5, this.zzngz);
      }

      if(this.zzgbv != 0L) {
         var1 += zzegg.zze(6, this.zzgbv);
      }

      if(this.zznha != null && !this.zznha.equals("")) {
         var1 += zzegg.zzm(7, this.zznha);
      }

      if(this.zznhb != null && !this.zznhb.equals("")) {
         var1 += zzegg.zzm(8, this.zznhb);
      }

      if(this.zznhc != null && !this.zznhc.equals("")) {
         var1 += zzegg.zzm(9, this.zznhc);
      }

      if(this.zznhd != null && !this.zznhd.equals("")) {
         var1 += zzegg.zzm(10, this.zznhd);
      }

      if(this.zznhe != null && !this.zznhe.equals("")) {
         var1 += zzegg.zzm(11, this.zznhe);
      }

      if(this.zznhf != 0) {
         var1 += zzegg.zzv(12, this.zznhf);
      }

      if(this.zznhg != null && this.zznhg.length > 0) {
         for(int var2 = 0; var2 < this.zznhg.length; ++var2) {
            zzehl var3;
            if((var3 = this.zznhg[var2]) != null) {
               var1 += zzegg.zzb(13, var3);
            }
         }
      }

      return var1;
   }

   public static zzehm zzay(byte[] var0) throws zzegn {
      return (zzehm)zzego.zza(new zzehm(), var0);
   }

   // $FF: synthetic method
   public final zzego zza(zzegf var1) throws IOException {
      zzegf var3 = var1;
      zzehm var2 = this;

      while(true) {
         int var4;
         int var6;
         zzehl[] var7;
         switch(var4 = var3.zzcbr()) {
         case 0:
            return var2;
         case 10:
            var2.zzngv = var3.readString();
            continue;
         case 18:
            var2.zzngw = var3.readString();
            continue;
         case 24:
            var2.zzngx = var3.zzcdr();
            continue;
         case 34:
            var2.zzngy = var3.readString();
            continue;
         case 40:
            var2.zzngz = var3.zzcdr();
            continue;
         case 48:
            var2.zzgbv = var3.zzcdr();
            continue;
         case 58:
            var2.zznha = var3.readString();
            continue;
         case 66:
            var2.zznhb = var3.readString();
            continue;
         case 74:
            var2.zznhc = var3.readString();
            continue;
         case 82:
            var2.zznhd = var3.readString();
            continue;
         case 90:
            var2.zznhe = var3.readString();
            continue;
         case 96:
            var2.zznhf = var3.zzcbs();
            continue;
         case 106:
            int var5 = zzegr.zzb(var3, 106);
            var7 = new zzehl[(var6 = var2.zznhg == null?0:var2.zznhg.length) + var5];
            if(var6 != 0) {
               System.arraycopy(var2.zznhg, 0, var7, 0, var6);
            }
            break;
         default:
            if(!var3.zzgl(var4)) {
               return var2;
            }
            continue;
         }

         while(var6 < var7.length - 1) {
            var7[var6] = new zzehl();
            var3.zza(var7[var6]);
            var3.zzcbr();
            ++var6;
         }

         var7[var6] = new zzehl();
         var3.zza(var7[var6]);
         var2.zznhg = var7;
      }
   }
}
