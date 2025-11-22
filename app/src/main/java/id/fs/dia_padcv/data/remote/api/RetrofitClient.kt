package id.fs.dia_padcv.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL par défaut (online)
    private const val BASE_URL = "https://padcvfsrdc.com/api/"
    private const val BASE_URL_ONLINE = "https://padcvfsrdc.com/api/"
    private const val BASE_URL_LOCAL = "http://192.168.120.92/pdcv-rdc/api/"

    // Fabrique Retrofit générique
    private fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Online explicite
    val apiOnline: ApiService by lazy {
        buildRetrofit(BASE_URL_ONLINE).create(ApiService::class.java)
    }

    // API Local explicite
    val apiLocal: ApiService by lazy {
        buildRetrofit(BASE_URL_LOCAL).create(ApiService::class.java)
    }

    // API par défaut (online)
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}