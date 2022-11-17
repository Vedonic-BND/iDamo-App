package com.android.vedonic.idamo.model

import com.google.firebase.firestore.PropertyName

data class Post(
    @get:PropertyName("postid") @set:PropertyName("postid") var postid: String = "",
    @get:PropertyName("postimage") @set:PropertyName("postimage") var postimage: String = "",
    @get:PropertyName("publisher") @set:PropertyName("publisher") var publisher: String = "",
    @get:PropertyName("description") @set:PropertyName("description") var description: String = ""
)


//firebase database
//class Post {
//
//    private var postid: String = ""
//    private var postimage: String = ""
//    private var publisher: String = ""
//    private var description: String = ""
//
//    constructor()
//
//
//    constructor(postid: String, postimage: String, publisher: String, description: String) {
//        this.postid = postid
//        this.postimage = postimage
//        this.publisher = publisher
//        this.description = description
//    }
//
//
//    fun getPostid(): String{
//        return postid
//    }
//
//    fun setPostid(postid: String) {
//        this.postid = postid
//    }
//
//
//    fun getPostimage(): String{
//        return postimage
//    }
//
//    fun setPostimage(postimage: String) {
//        this.postimage = postimage
//    }
//
//
//    fun getPublisher(): String{
//        return publisher
//    }
//
//    fun setPublisher(publisher: String) {
//        this.publisher = publisher
//    }
//
//
//    fun getDescription(): String{
//        return description
//    }
//
//    fun setDescription(description: String) {
//        this.description = description
//    }
//
//
//}