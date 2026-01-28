package model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a Landmark from the REST API.
 */
data class Landmark(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("image") val image: String // URL or base64
)