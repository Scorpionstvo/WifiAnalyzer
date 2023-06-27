package garipolesya.com.example.wifianalyzer.di

import android.content.Context
import android.net.wifi.WifiManager
import garipolesya.com.example.wifianalyzer.*
import garipolesya.com.example.wifianalyzer.data.db.WifiDatabase
import garipolesya.com.example.wifianalyzer.data.repository.*
import garipolesya.com.example.wifianalyzer.manager.WifiPasswordManager
import garipolesya.com.example.wifianalyzer.presentation.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {

    single<SettingsWifiChecker> {
        SettingsWifiCheckerImpl(context = get())
    }

    single {
        App.applicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    single {
        WifiDatabase.getInstance(App.applicationContext()).wifiDao()
    }

    single<WifiRepository> {
        WifiRepositoryImpl(wifiManager = get(), wifiDao = get(), wifiPasswordManager = WifiPasswordManager())
    }

    single<ConnectionRepository> { ConnectionRepositoryImpl(wifiManager = get()) }

    viewModel {
        WifiViewModel(repository = get(), settingsWifiChecker = get())
    }

    viewModel {
        DetailViewModel(passwordRepository = get())
    }

}
