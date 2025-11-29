
package com.ucb.helpet.features.time.data.remote

import retrofit2.http.GET

interface TimeApi {
    @GET("api/ip")
    suspend fun getCurrentTime(): TimeResponse
}
