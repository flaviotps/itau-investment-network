package br.com.itau.app.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * Loads JSON data from a file in the assets folder of the provided [context] and deserializes it into an object of type [T].
 *
 * @param context The context to access the application's assets.
 * @param fileName The name of the JSON file to load from the assets folder.
 * @param typeToken The TypeToken representing the type of the object to deserialize from JSON.
 * @return An object of type [T] containing the deserialized JSON data, or null if an error occurs during loading or deserialization.
 * @throws IOException If an I/O error occurs while reading the file.
 */
inline fun <reified T> loadFromAssets(context: Context, fileName: String, typeToken: TypeToken<T>): T? {
    val json: String
    try {
        val inputStream = context.assets.open(fileName)
        json = inputStream.bufferedReader().use { it.readText() }
    } catch (exception: IOException) {
        exception.printStackTrace()
        throw exception
    }
    val gson = Gson()
    return gson.fromJson(json, typeToken.type)
}
