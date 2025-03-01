package com.koco.personalfinancetracker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object AppModule {

/*
    @Provides
    fun provideDatePickDialog(@ActivityContext context: Context): DatePickDialog {
        return DatePickDialog(context)
    }
*/

}