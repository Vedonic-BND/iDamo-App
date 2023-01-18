package com.android.vedonic.idamo.model

import com.google.firebase.firestore.PropertyName

data class User (
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("bio") @set:PropertyName("bio") var bio: String = "",
    @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "",
    @get:PropertyName("image") @set:PropertyName("image") var image: String = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String = ""
)
