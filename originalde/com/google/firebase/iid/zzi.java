package com.google.firebase.iid;

import android.support.annotation.Nullable;
import com.google.firebase.iid.FirebaseInstanceId;

@Deprecated
public final class zzi {

   private final FirebaseInstanceId zzmiv;


   public static zzi zzbyh() {
      return new zzi(FirebaseInstanceId.getInstance());
   }

   private zzi(FirebaseInstanceId var1) {
      this.zzmiv = var1;
   }

   public final String getId() {
      return this.zzmiv.getId();
   }

   @Nullable
   public final String getToken() {
      return this.zzmiv.getToken();
   }
}
