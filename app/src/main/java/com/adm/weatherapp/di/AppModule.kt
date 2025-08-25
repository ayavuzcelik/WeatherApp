package com.adm.weatherapp.di

import android.content.Context
import com.adm.weatherapp.repository.DataStoreRepository
import com.adm.weatherapp.repository.WeatherRepository
import com.adm.weatherapp.service.DataStoreAPI
import com.adm.weatherapp.service.WeatherAPI
import com.adm.weatherapp.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        api: WeatherAPI
    ): WeatherRepository {
        return WeatherRepository(api)
    }

    @Singleton
    @Provides
    fun provideWeatherAPI():  WeatherAPI {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build().create(WeatherAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideDataStoreAPI(
        @ApplicationContext context: Context
    ): DataStoreAPI {
        return DataStoreRepository(context)
    }
}