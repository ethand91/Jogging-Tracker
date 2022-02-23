package com.example.joggingtracker

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.vmadalin.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {
    private val TAG = "MainActivity"

    private lateinit var mMap: GoogleMap
    private lateinit var mStartButton: Button
    private lateinit var mStopButton: Button

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

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mStartButton = findViewById<Button>(R.id.startButton)
        mStopButton = findViewById<Button>(R.id.stopButton)

        mStartButton.setOnClickListener {
            handleStartButtonClicked()
        }

        mStopButton.setOnClickListener {
            handleStopButtonClicked()
        }

        updateButtonStatus()

        if (isTracking) {
            startTracking()
        }
    }

    private fun handleStartButtonClicked() {
        mMap.clear()

        isTracking = true
        updateButtonStatus()

        startTracking()
    }

    private fun handleStopButtonClicked() {
        AlertDialog.Builder(this)
            .setTitle(R.string.stop_alert_dialog_text)
            .setPositiveButton(R.string.alert_dialog_positive_text) {_, _ ->
                isTracking = false
                updateButtonStatus()
                stopTracking()
            }.setNegativeButton(R.string.alert_dialog_negative_text) {_, _ ->

            }
            .create()
            .show()
    }

    private fun updateButtonStatus() {
        mStartButton.isEnabled = !isTracking
        mStopButton.isEnabled = isTracking
    }

    private fun startTracking() {
        // ACTIVITY_RECOGNITION is only needed for version Q+
        val isActivityRecognitionPermissionNeeded = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        val isActivityRecognitionPermissionGranted = EasyPermissions.hasPermissions(this, ACTIVITY_RECOGNITION)

        if (isActivityRecognitionPermissionNeeded || isActivityRecognitionPermissionGranted) {
            // Permission Granted
            initializeStepCounterListener()
        } else {
            // Permission needed
            EasyPermissions.requestPermissions(
                host = this,
                rationale = getString(R.string.activity_recognition_permission_string),
                requestCode = REQUEST_CODE_ACTIVITY_RECOGNITION,
                perms = arrayOf(ACTIVITY_RECOGNITION)
            )
        }
    }

    private fun stopTracking() {

    }

    private fun initializeStepCounterListener() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        stepCounterSensor ?: return

        sensorManager.registerListener(this@MainActivity, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST,)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged: sensor: $sensor; accuracy: $accuracy")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        event.values.firstOrNull()?.let {
            Log.d(TAG, "Steps: $it")
        }
    }
}