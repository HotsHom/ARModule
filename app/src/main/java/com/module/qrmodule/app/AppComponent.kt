package com.module.qrmodule.app

import android.app.Application
import com.module.qrmodule.di.CommonAppModule
import com.module.qrmodule.domain.ImageDatabase
import com.module.qrmodule.presentation.main.MainActivity
import com.module.qrmodule.presentation.main.MainPresenter
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        CommonAppModule::class
    ]
)
interface AppComponent: AndroidInjector<App> {

    override fun inject(application: App)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun create(app: Application): Builder
        fun build(): AppComponent
    }
}