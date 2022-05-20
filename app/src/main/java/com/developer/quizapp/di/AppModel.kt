package com.developer.quizapp.di

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.developer.quizapp.R
import com.developer.quizapp.data.local.dataStore.DataStoreManager
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModel {


    @Singleton
    @Provides
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ) = context


    // TODO: 11/8/2021  For implementation Glide
    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.holder)
            .error(R.drawable.holder)
            .diskCacheStrategy(DiskCacheStrategy.DATA)

    )
    @Provides
    @Singleton
    fun dataStoreManager(@ApplicationContext appContext: Context): DataStoreManager =
        DataStoreManager(appContext)


   @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }


    @SuppressLint("NewApi")
    @Singleton
    @Provides
    fun provideSimpleDateFormat(): SimpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa", Locale.US)


    @Singleton
    @Provides
    fun provideDate(): Date = Date()




}