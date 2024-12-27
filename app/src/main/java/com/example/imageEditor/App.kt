package com.example.imageEditor

import android.app.Application
import com.example.imageEditor.cipher.MyKeystore

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        MyKeystore.createAESKey()
    }

    companion object {
        lateinit var instance: App
    }
}
