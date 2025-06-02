package com.frogans.fpandroidlocationservice


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS = 1

    private lateinit var usernameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var currentUsernameTextView: TextView

    private val sharedPrefFile = "com.frogans.fpandroidlocationservice.prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        usernameEditText = findViewById(R.id.usernameEditText)
        saveButton = findViewById(R.id.saveButton)
        currentUsernameTextView = findViewById(R.id.currentUsernameTextView)

        val sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        // Load the saved username
        val savedUsername = sharedPreferences.getString("username", "")
        if (!savedUsername.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            currentUsernameTextView.text = "Current username: $savedUsername"
        }

        // Set click listener for the save button
        saveButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()

            if (enteredUsername.isNotEmpty()) {
                // Save username to SharedPreferences
                with (sharedPreferences.edit()) {
                    putString("username", enteredUsername)
                    apply()
                }

                Toast.makeText(this, "Username saved!", Toast.LENGTH_SHORT).show()
                currentUsernameTextView.text = "Current username: $enteredUsername"
            } else {
                Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT).show()
            }
        }

        if (checkPermissions()) {
            startLocationService()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                REQUEST_PERMISSIONS
            )
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }


    private fun startLocationService() {
        val intent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }
        else {
            startService(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startLocationService()
        }
    }
}

