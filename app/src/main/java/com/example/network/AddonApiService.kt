package com.example.network

import com.example.model.AddonManifest
import com.example.model.CatalogResponse
import com.example.model.MetaDetailResponse
import com.example.model.StreamResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface AddonApiService {
    @GET
    suspend fun getManifest(@Url url: String): AddonManifest

    @GET
    suspend fun getCatalog(@Url url: String): CatalogResponse

    @GET
    suspend fun getMeta(@Url url: String): MetaDetailResponse

    @GET
    suspend fun getStreams(@Url url: String): StreamResponse
}
