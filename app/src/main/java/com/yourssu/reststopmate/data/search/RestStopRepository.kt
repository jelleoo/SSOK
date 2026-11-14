package com.yourssu.reststopmate.data.search

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

internal data class RestStop(val id: String, val name: String, val highway: String, val direction: String) {
    val description: String
        get() = listOf(highway, when (direction) {
            "SOUTHBOUND" -> "부산 방향"
            "NORTHBOUND" -> "서울 방향"
            else -> direction
        }).filter { it.isNotBlank() }.joinToString(" · ")
}

internal data class FeaturedMenu(
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val priceWon: Int?,
    val priceLoadFailed: Boolean = false,
)

internal data class MenuItem(val name: String, val description: String?, val imageUrl: String?, val priceWon: Int?)

private const val API_BASE = "https://3.34.5.244"
private val Context.searchHistoryStore by preferencesDataStore(name = "search_history")

internal class RestStopRepository(context: Context) {
    private val store = context.applicationContext.searchHistoryStore
    private val historyKey = stringPreferencesKey("selected_rest_stops")
    val recent = store.data.map { decode(preferences = it[historyKey] ?: "[]") }

    suspend fun fetchRestStops(): List<RestStop> = withContext(Dispatchers.IO) {
        decode(request("/api/rest-stops"))
    }

    suspend fun fetchFeaturedMenu(id: String): FeaturedMenu? = withContext(Dispatchers.IO) {
        val encodedId = URLEncoder.encode(id, "UTF-8")
        val response = JSONObject(request("/api/rest-stops/$encodedId/featured-menus"))
        require(response.getString("restStopId") == id)
        val featured = response.optJSONObject("featuredMenu") ?: return@withContext null
        val name = featured.optString("name").takeIf { it.isNotBlank() } ?: return@withContext null
        var priceLoadFailed = false
        var matchedItem: JSONObject? = null
        val price = try {
            val menusResponse = JSONObject(request("/api/rest-stops/$encodedId/menus"))
            require(menusResponse.getString("restStopId") == id)
            val menus = menusResponse.getJSONArray("menus")
            fun normalize(value: String) = value.filterNot(Char::isWhitespace)
            val matches = (0 until menus.length()).map { menus.getJSONObject(it) }
                .filter { normalize(it.optString("name")) == normalize(name) }
            matchedItem = matches.firstOrNull()
            val prices = matches.mapNotNull { item ->
                if (item.isNull("priceWon")) null else item.optInt("priceWon", -1).takeIf { it >= 0 }
            }.distinct()
            prices.singleOrNull()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            priceLoadFailed = true
            null
        }
        fun nullableText(item: JSONObject?, key: String): String? =
            if (item == null || item.isNull(key)) null else item.optString(key).takeIf { it.isNotBlank() }
        val rawImage = nullableText(featured, "imageUrl") ?: nullableText(matchedItem, "imageUrl")
        val image = rawImage?.let { URL(URL(API_BASE), it) }?.takeIf { it.protocol == "https" }?.toString()
        FeaturedMenu(name, nullableText(featured, "description"), image, price, priceLoadFailed)
    }

    suspend fun fetchMenus(id: String): List<MenuItem> = withContext(Dispatchers.IO) {
        val encodedId = URLEncoder.encode(id, "UTF-8")
        val response = JSONObject(request("/api/rest-stops/$encodedId/menus"))
        require(response.getString("restStopId") == id)
        val menus = response.getJSONArray("menus")
        List(menus.length()) { index ->
            val item = menus.getJSONObject(index)
            fun text(key: String) = if (item.isNull(key)) null else item.optString(key).takeIf { it.isNotBlank() }
            val image = text("imageUrl")?.let { raw ->
                runCatching { URL(URL(API_BASE), raw).takeIf { it.protocol == "https" }?.toString() }.getOrNull()
            }
            val price = if (item.isNull("priceWon")) null else item.optInt("priceWon", -1).takeIf { it >= 0 }
            MenuItem(item.getString("name"), text("description"), image, price)
        }
    }

    private fun request(path: String): String {
        val connection = URL(API_BASE + path).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            connection.setRequestProperty("Accept", "application/json")
            if (connection.responseCode !in 200..299) throw IOException("HTTP ${connection.responseCode}")
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    suspend fun rememberSelection(restStop: RestStop) {
        store.edit { preferences ->
            val previous = decode(preferences[historyKey] ?: "[]")
            val updated = (listOf(restStop) + previous.filterNot { it.id == restStop.id }).take(10)
            preferences[historyKey] = JSONArray().apply {
                updated.forEach { stop ->
                    put(JSONObject().put("id", stop.id).put("name", stop.name)
                        .put("highway", stop.highway).put("direction", stop.direction))
                }
            }.toString()
        }
    }

    private fun decode(preferences: String): List<RestStop> {
        val array = JSONArray(preferences)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            RestStop(item.getString("id"), item.getString("name"), item.optString("highway"), item.optString("direction"))
        }.distinctBy { it.id }
    }
}
