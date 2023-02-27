package com.android.iDamoTeam.idamo.utils

import android.util.Log
import okhttp3.*
import okio.IOException

class ProfanityCheckerUtils {
    companion object {
        fun checkForProfanity(text: String, callback: (String) -> Unit) {
            val client = OkHttpClient()
            val url = "https://www.purgomalum.com/service/containsprofanity?text=$text"
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Handle network or API errors
                    callback("Error: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string()
                    if (result == "true") {
                        // The text contains profanity
                        callback("true")
                    } else {
                        // The text does not contain profanity
                        callback("false")
                    }
                }
            })
        }
    }
}