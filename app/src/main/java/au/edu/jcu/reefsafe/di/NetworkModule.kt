package au.edu.jcu.reefsafe.di

import au.edu.jcu.reefsafe.data.api.OpenMeteoForecastService
import au.edu.jcu.reefsafe.data.api.OpenMeteoMarineService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val FORECAST_BASE_URL = "https://api.open-meteo.com/v1/"
    private const val MARINE_BASE_URL = "https://marine-api.open-meteo.com/v1/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    @Provides
    @Singleton
    fun provideForecastService(
        moshi: Moshi,
        client: OkHttpClient
    ): OpenMeteoForecastService = Retrofit.Builder()
        .baseUrl(FORECAST_BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(OpenMeteoForecastService::class.java)

    @Provides
    @Singleton
    fun provideMarineService(
        moshi: Moshi,
        client: OkHttpClient
    ): OpenMeteoMarineService = Retrofit.Builder()
        .baseUrl(MARINE_BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(OpenMeteoMarineService::class.java)
}
