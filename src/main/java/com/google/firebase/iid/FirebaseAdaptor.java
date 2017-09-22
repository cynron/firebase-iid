package com.google.firebase.iid;

import android.support.annotation.Nullable;
import com.google.firebase.iid.FirebaseInstanceId;

/* zzi.java */

@Deprecated
public final class FirebaseAdaptor {

   private final FirebaseInstanceId id;


   public static FirebaseAdaptor getInstance() {
      return new FirebaseAdaptor(FirebaseInstanceId.getInstance());
   }

   private FirebaseAdaptor(FirebaseInstanceId id) {
      this.id = id;
   }

   public final String getId() {
      return this.id.getId();
   }

   @Nullable
   public final String getToken() {
      return this.id.getToken();
   }
}
