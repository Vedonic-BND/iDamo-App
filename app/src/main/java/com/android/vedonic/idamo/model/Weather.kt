package com.android.vedonic.idamo.model

import com.google.firebase.firestore.PropertyName

data class Weather(
    @get:PropertyName("time") @set:PropertyName("time") var time: String = "",
    @get:PropertyName("temperature") @set:PropertyName("temperature") var temperature: String = "",
    @get:PropertyName("icon") @set:PropertyName("icon") var icon: String = "",
    @get:PropertyName("condition") @set:PropertyName("condition") var condition: String = ""
)
