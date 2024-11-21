package com.emsi.retrofitbankingapp.api

import Compte
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiJSONService {

    @GET("/banque/comptes")
    suspend fun getAllComptesJson(
        @Header("Accept") accept: String = "application/json"
    ): Response<List<Compte>>

    @GET("/banque/comptes/{id}")
    suspend fun getCompteByIdJson(
        @Path("id") id: Long,
        @Header("Accept") accept: String = "application/json"
    ): Response<Compte>

    @POST("/banque/comptes")
    suspend fun createCompteJson(
        @Body compte: Compte,
        @Header("Accept") accept: String = "application/json",
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<Compte>

    @PUT("/banque/comptes/{id}")
    suspend fun updateCompteJson(
        @Path("id") id: Long?,
        @Body compte: Compte,
        @Header("Accept") accept: String = "application/json",
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<Compte>

    @DELETE("/banque/comptes/{id}")
    suspend fun deleteCompteJson(
        @Path("id") id: Long,
        @Header("Accept") accept: String = "application/json"
    ): Response<Void>
}