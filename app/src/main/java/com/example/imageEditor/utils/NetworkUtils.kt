package com.example.imageEditor.utils

import android.net.Uri
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

fun getMethodHttp(
    endPoint: String,
    params: Map<String, String?>,
): String {
    var conn: HttpURLConnection? = null
    try {
        val builtURI = Uri.parse(BASE_URL_API + endPoint).buildUpon()
        params.forEach { (key, value) ->
            builtURI.appendQueryParameter(key, value)
        }

        val url = URL(builtURI.build().toString())
        conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty(
            CONTENT_TYPE,
            "application/json",
        )
        conn.setRequestProperty(ACCEPT, "application/json")
        conn.setRequestProperty(
            AUTHORIZATION,
            CLIENT_ID,
        )
        conn.connectTimeout = 10000
        conn.connect()

        return convertInputStreamToString(conn.inputStream)
    } catch (e: Exception) {
        throw e
    } finally {
        conn?.disconnect()
    }
}

fun postMethodHttp(
    endPoint: String,
    body: String,
): String {
    var conn: HttpURLConnection? = null
    try {
        val builtURI = Uri.parse(BASE_URL + endPoint).buildUpon()

        val url = URL(builtURI.build().toString())
        conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty(
            CONTENT_TYPE,
            "application/json",
        )
        conn.setRequestProperty(ACCEPT, "application/json")
        conn.connectTimeout = 10000
        conn.doOutput = true

        BufferedWriter(OutputStreamWriter(conn.outputStream)).use { writer ->
            writer.write(body)
            writer.flush()
        }
        conn.connect()

        return convertInputStreamToString(conn.inputStream)
    } catch (e: Exception) {
        throw e
    } finally {
        conn?.disconnect()
    }
}

fun postMethodHttpWithBearToken(
    endPoint: String,
    body: String,
    bearerToken: String,
    onFailure: () -> Unit,
): String {
    var conn: HttpURLConnection? = null
    try {
        val builtURI = Uri.parse(BASE_URL_API + endPoint).buildUpon()

        val url = URL(builtURI.build().toString())
        conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty(
            CONTENT_TYPE,
            "application/json",
        )
        conn.connectTimeout = 10000
        conn.setRequestProperty(ACCEPT, "application/json")
        conn.setRequestProperty(AUTHORIZATION, "Bearer $bearerToken")
        conn.doOutput = true

        if (body.isNotBlank()) {
            BufferedWriter(OutputStreamWriter(conn.outputStream)).use { writer ->
                writer.write(body)
                writer.flush()
            }
        }
        conn.connect()

        return if (conn.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            onFailure()
            ""
        } else {
            convertInputStreamToString(conn.inputStream)
        }
    } catch (e: Exception) {
        throw e
    } finally {
        conn?.disconnect()
    }
}

fun getMethodHttpWithBearToken(
    endPoint: String,
    params: Map<String, String?>,
    bearerToken: String,
    onFailure: () -> Unit,
): String {
    var conn: HttpURLConnection? = null
    try {
        val builtURI = Uri.parse(BASE_URL_API + endPoint).buildUpon()
        params.forEach { (key, value) ->
            builtURI.appendQueryParameter(key, value)
        }

        val url = URL(builtURI.build().toString())
        conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty(
            CONTENT_TYPE,
            "application/json",
        )
        conn.connectTimeout = 10000
        conn.setRequestProperty(ACCEPT, "application/json")
        conn.setRequestProperty(
            AUTHORIZATION,
            "Bearer $bearerToken",
        )
        conn.connect()

        return if (conn.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            onFailure()
            ""
        } else {
            convertInputStreamToString(conn.inputStream)
        }
    } catch (e: Exception) {
        throw e
    } finally {
        conn?.disconnect()
    }
}

fun deleteMethodHttpWithBearToken(
    endPoint: String,
    bearerToken: String,
    onFailure: () -> Unit,
): String {
    var conn: HttpURLConnection? = null
    try {
        val builtURI = Uri.parse(BASE_URL_API + endPoint).buildUpon()

        val url = URL(builtURI.build().toString())
        conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "DELETE"
        conn.setRequestProperty(
            CONTENT_TYPE,
            "application/json",
        )
        conn.setRequestProperty(ACCEPT, "application/json")
        conn.setRequestProperty(
            AUTHORIZATION,
            "Bearer $bearerToken",
        )
        conn.connectTimeout = 10000
        conn.connect()

        return if (conn.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            onFailure()
            ""
        } else {
            convertInputStreamToString(conn.inputStream)
        }
    } catch (e: Exception) {
        throw e
    } finally {
        conn?.disconnect()
    }
}

fun convertInputStreamToString(inputStream: InputStream): String {
    val stringBuilder = StringBuilder()
    BufferedReader(InputStreamReader(inputStream)).use { reader ->
        var line: String? = reader.readLine()
        while (line != null) {
            stringBuilder.append(line)
            line = reader.readLine()
        }
    }
    return stringBuilder.toString()
}
