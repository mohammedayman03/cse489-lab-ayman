package api

import model.Landmark
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class LandmarkRepository {

    private val api = RetrofitClient.api

    suspend fun getAllLandmarks(): Response<List<Landmark>> = api.getLandmarks()

    suspend fun createLandmark(
        title: String,
        lat: Double,
        lon: Double,
        imagePart: okhttp3.MultipartBody.Part
    ): Response<Landmark> {
        val t = title.toRequestBody("text/plain".toMediaType())
        val la = lat.toString().toRequestBody("text/plain".toMediaType())
        val lo = lon.toString().toRequestBody("text/plain".toMediaType())
        return api.createLandmark(t, la, lo, imagePart)
    }

    suspend fun updateLandmark(
        id: Int,
        title: String,
        lat: Double,
        lon: Double,
        imagePart: okhttp3.MultipartBody.Part? = null
    ): Response<Landmark> {
        val i = id.toString().toRequestBody("text/plain".toMediaType())
        val t = title.toRequestBody("text/plain".toMediaType())
        val la = lat.toString().toRequestBody("text/plain".toMediaType())
        val lo = lon.toString().toRequestBody("text/plain".toMediaType())
        return api.updateLandmark(i, t, la, lo, imagePart)
    }

    suspend fun deleteLandmark(id: Int) = api.deleteLandmark(id)
}