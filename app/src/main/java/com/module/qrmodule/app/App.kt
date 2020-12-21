package com.module.qrmodule.app

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App: DaggerApplication(), HasActivityInjector {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        lateinit var instance: App
            private set
    }

    private var component: AppComponent? = null

    fun getComponent(): AppComponent? {
        return component
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    fun init() {
        instance = this
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        init()
        instance = this
        component =  DaggerAppComponent.builder().create(this).build()
        return component as AppComponent
    }

    fun getIn() = this

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}