package com.example.joggingtracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    companion object {
        private const val KEY_SHARED_PREFERENCES = "com.example.joggingtracker.sharedPreferences"
        private const val KEY_IS_TRACKING = "com.example.joggingTracking.isTracking"

        private const val REQUEST_CODE_FIND_LOCATION = 1
        private const val REQUEST_CODE_ACTIVITY_RECOGNITION = 2
    }

    private var isTracking: Boolean
        get() = this.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE).getBoolean(
            KEY_IS_TRACKING, false)
        set(value) = this.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(
            KEY_IS_TRACKING, value).apply()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }
}