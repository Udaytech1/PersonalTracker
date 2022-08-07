package com.psnltracker

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private val LOCATION_REQUEST_CODE: Int =10
    private lateinit var latLongText :TextView
    private val URL ="https://prsntracker-default-rtdb.firebaseio.com/"

    var tracker: LocationTracker = LocationTracker(
        minTimeBetweenUpdates = 1000L, // one second
        minDistanceBetweenUpdates = 1F, // one meter
        shouldUseGPS = true,
        shouldUseNetwork = true,
        shouldUsePassive = true
    ).also {
        it.addListener(object : LocationTracker.Listener {
            override fun onLocationFound(location: Location) {
                val mobileumber = PrefrenceHelper.getSavedValue(this@MainActivity,PrefrenceHelper.mobileNumber)
                val database: FirebaseDatabase = FirebaseDatabase.getInstance("$URL")
                val myRef: DatabaseReference = database.getReference("Devices").child("${mobileumber}")
                val json = JSONObject()
                json.put("Brand Name", Build.BRAND)
                json.put("Device", Build.DEVICE)
                json.put("Model", Build.MODEL)
                json.put("Login Time", Build.TIME)

                val latlong = JSONObject()
                latlong.put("latitude = ",location.latitude)
                latlong.put("longitude = ",location.longitude)
                latlong.put("latlong","${location.latitude},${location.longitude}")
                json.put("Locations",latlong)
                myRef.setValue("$json")


                latLongText.setText("${location.latitude},${location.longitude}")
                System.out.print("Location LatLang ========= onLocationFound: ${location.latitude},${location.longitude}")
                Toast.makeText(this@MainActivity, ""+location.latitude.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onProviderError(providerError: ProviderError) {
                TODO("Not yet implemented")
                latLongText.setText("${providerError.message}")
                Toast.makeText(this@MainActivity, ""+providerError.message.toString(), Toast.LENGTH_SHORT).show()

            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latLongText = findViewById(R.id.latLongText)

        requestPermissionsToUser()

    }

    private fun requestPermissionsToUser() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }else{
            tracker.startListening(this)
        }
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            true
        } else false
    }

    private fun OnGPS() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}