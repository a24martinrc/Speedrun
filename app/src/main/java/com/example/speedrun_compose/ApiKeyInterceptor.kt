package com.example.speedrun_compose

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer 8dc4b2a2106922dfb271c362c1b71950bdbbf6e8")  // Asegúrate de que este sea el formato correcto
            .build()
        return chain.proceed(request)
    }
}


