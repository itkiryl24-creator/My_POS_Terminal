package com.example.myposterminal.data.di

import com.example.myposterminal.data.api.PosApiService
import com.example.myposterminal.data.repository.PosRepositoryImpl
import com.example.myposterminal.domain.repository.PosRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = " http://192.168.1.69:8080"

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    companion object{
        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun providePosApiService(retrofit: Retrofit): PosApiService {
            return retrofit.create(PosApiService::class.java)
        }
    }
    @Binds
    @Singleton
    abstract fun bindsRepository(impl: PosRepositoryImpl) : PosRepository
}