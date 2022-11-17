package com.android.vedonic.idamo.model

import com.google.firebase.firestore.PropertyName

data class User (
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("bio") @set:PropertyName("bio") var bio: String = "",
    @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "",
    @get:PropertyName("image") @set:PropertyName("image") var image: String = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String = ""
        )

//firebase database
//class User {
//    private var name: String = ""
//    private var bio: String = ""
//    private var uid: String = ""
//    private var image: String = ""
//    private var email: String = ""
//
//
//
//    constructor()
//
//    constructor(name: String, bio: String, uid: String, image: String, email: String){
//        this.name = name
//        this.bio = bio
//        this.uid = uid
//        this.image = image
//        this.email = email
//    }
//
//    fun getName(): String {
//        return name
//    }
//
//    fun setName(name: String) {
//        this.name = name
//    }
//
//
//    fun getBio(): String {
//        return bio
//    }
//
//    fun setBio(bio: String) {
//        this.bio = bio
//    }
//
//    fun getUid(): String {
//        return uid
//    }
//
//    fun setUid(uid: String) {
//        this.uid = uid
//    }
//
//    fun getImage(): String {
//        return image
//    }
//
//    fun setImage(image: String) {
//        this.image = image
//    }
//
//    fun getEmail(): String {
//        return email
//    }
//
//    fun setEmail(email: String) {
//        this.email = email
//    }
//}