package com.example.gamestore

import android.app.Application
import com.google.firebase.FirebaseApp

class Initializer : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Other initialization code for your app can go here
    }
}