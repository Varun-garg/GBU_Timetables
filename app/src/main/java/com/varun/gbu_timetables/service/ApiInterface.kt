package com.varun.gbu_timetables.service

import com.varun.gbu_timetables.data.model.Notice
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiInterface {

    @GET("index.json")
    fun getNotices(): Call<List<Notice>>

    companion object {

        var BASE_URL = "https://gbuweb.github.io/data/notices/"

        fun create(): ApiInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}

