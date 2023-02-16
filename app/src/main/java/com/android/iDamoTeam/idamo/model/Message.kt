package com.android.iDamoTeam.idamo.model

import com.google.firebase.firestore.PropertyName

data class Message(
    @get:PropertyName("messageid") @set:PropertyName("messageid") var messageid: String = "",
    @get:PropertyName("message") @set:PropertyName("message") var message: String = "",
    @get:PropertyName("senderid") @set:PropertyName("senderid") var senderid: String = "",
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl") var imageUrl: String = "",
    @get:PropertyName("timeStamp") @set:PropertyName("timeStamp") var timeStamp: Long = 0
)