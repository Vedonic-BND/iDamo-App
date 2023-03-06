package com.android.iDamoTeam.idamo.utils

import android.content.Context
import android.util.Log
import okhttp3.*
import okio.IOException
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class ProfanityCheckerUtils {
    companion object {
        fun checkForProfanity(text: String, profanityList: List<String>, callback: (String) -> Unit) {
            if (checkForCustomProfanity(text, profanityList)) {
                callback("true")
            }else{
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

        fun loadProfanityListFromFile(context: Context, resourceId: Int): List<String> {
            val inputStream = context.resources.openRawResource(resourceId)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val words = mutableListOf<String>()
            bufferedReader.forEachLine { line ->
                words.addAll(line.split("\\s+".toRegex()))
            }
            return words.map { it.trim() }
        }

        fun checkForCustomProfanity(text: String, profanityList: List<String>): Boolean {
            val pattern = "\\b(${profanityList.joinToString("|")})\\b".toRegex(RegexOption.IGNORE_CASE)
            return pattern.containsMatchIn(text)
        }
    }
}