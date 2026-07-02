package com.nusiss.wellness

import android.app.Application
import com.nusiss.wellness.data.api.TokenManager

class WellnessApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}
