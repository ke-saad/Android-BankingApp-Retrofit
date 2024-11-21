package com.emsi.retrofitbankingapp.api

import Compte
import com.emsi.retrofitbankingapp.beans.ComptesWrapper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiXMLService {
    @GET("/banque/comptes")
    suspend fun getAllComptesXml(
        @Header("Accept") accept: String = "application/xml"
    ): Response<ComptesWrapper>

    @GET("/banque/comptes/{id}")
    suspend fun getCompteByIdXml(
        @Path("id") id: Long,
        @Header("Accept") accept: String = "application/xml"
    ): Response<Compte>

    @POST("/banque/comptes")
    suspend fun createCompteXml(
        @Body compte: Compte,
        @Header("Accept") accept: String = "application/xml",
        @Header("Content-Type") contentType: String = "application/xml"
    ): Response<Compte>

    @PUT("/banque/comptes/{id}")
    suspend fun updateCompteXml(
        @Path("id") id: Long?,
        @Body compte: Compte,
        @Header("Accept") accept: String = "application/xml",
        @Header("Content-Type") contentType: String = "application/xml"
    ): Response<Compte>

    @DELETE("/banque/comptes/{id}")
    suspend fun deleteCompteXml(
        @Path("id") id: Long,
        @Header("Accept") accept: String = "application/xml"
    ): Response<Void>
}