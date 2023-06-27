package garipolesya.com.example.wifianalyzer

import android.app.Application
import android.content.Context
import garipolesya.com.example.wifianalyzer.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    companion object {
        private lateinit var instance: App
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidContext(applicationContext())
            modules(dataModule)
        }
    }
}