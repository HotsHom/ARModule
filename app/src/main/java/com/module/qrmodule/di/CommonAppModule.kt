package com.module.qrmodule.di

import android.content.Context
import android.os.Handler
import com.google.ar.core.Session
import com.module.qrmodule.domain.ImageDatabase
import com.module.qrmodule.presentation.main.MainActivity
import com.module.qrmodule.presentation.main.MainPresenter
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Named
import javax.inject.Scope
import javax.inject.Singleton

@Module
abstract class CommonAppModule {

    @ActivityScope
    @ContributesAndroidInjector//(modules = [SomeModule::class])
    abstract fun mainActivity(): MainActivity

}