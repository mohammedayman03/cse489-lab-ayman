package api

import model.Landmark
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface LandmarkApi {

    @GET("api.php?action=get")
    suspend fun getLandmarks(): Response<List<Landmark>>

    @Multipart
    @POST("api.php?action=create")
    suspend fun createLandmark(
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<Landmark>

    @Multipart
    @POST("api.php?action=update")
    suspend fun updateLandmark(
        @Part("id") id: RequestBody,
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<Landmark>

    @FormUrlEncoded
    @POST("api.php?action=delete")
    suspend fun deleteLandmark(@Field("id") id: Int): Response<Unit>
}