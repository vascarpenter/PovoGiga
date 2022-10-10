package com.hatenablog.gikoha.povogiga

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// API GET

data class PovoGiga(
    val date: String,
    val gigaleft: String,
    val memo: String?,
)

interface PovoGigaGet
{
    @GET(BuildConfig.povogetapi)
    suspend fun getItems(): Response<Array<PovoGiga>>
}

// API POST

data class PovoGigaPostJson(
    val apikey: String,
    val date: String,
    val gigaleft: String,
    val memo: String,
)

interface PovoGigaPost
{
    @POST(BuildConfig.povopostapi)
    suspend fun postItem(
        @Body postdata: PovoGigaPostJson
    ): Response<Void>
}
