package com.example.ieiruforandroid

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File

class LocationService : Service() {
    companion object {
        const val CHANNEL_ID = "777"
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var updatedCount = 0
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    updatedCount++
                    Log.d(this.javaClass.name, "[${updatedCount}] ${location.latitude} , ${location.longitude}, ${intent?.getStringExtra("nameField")}")

                    val readFile = File(applicationContext.filesDir, "nameFile.txt")
                    if(readFile.exists()){
                        val contents = readFile.bufferedReader().use(BufferedReader::readText)
                        Log.d("名前", contents)

                        val queue = Volley.newRequestQueue(applicationContext)
                        val url = "http://18.176.193.22/users"
                        val stringRequest = object : StringRequest(
                            Method.POST, url,
                            Response.Listener { response ->
                                val parentJsonObj = JSONObject(response.toString())
                                val dataJsonObj = parentJsonObj.getJSONArray("data")
                                Log.d("レスポンスjson", dataJsonObj.toString())
                            },
                            Response.ErrorListener { error ->
                                Log.d("レスポンスエラー", error.toString())
                            }) {
                            override fun getBodyContentType(): String {
                                return "application/json"
                            }
                            @Throws(AuthFailureError::class)
                            override fun getBody(): ByteArray {
                                val params = JSONObject() // Host object
                                val requiredParams = JSONObject()
                                requiredParams.put("name", contents)
                                requiredParams.put("latitude", location.latitude)
                                requiredParams.put("longitude", location.longitude)
                                params.put("user", requiredParams)
                                return params.toString().toByteArray()
                            }
                        }
                        queue.add(stringRequest)
                    }
                }
            }
        }

        val openIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        val file = "nameFile.txt"
        val readFile = File(applicationContext.filesDir, file)
        if(!readFile.exists()) {
            val contents = intent?.getStringExtra("nameField")
            applicationContext.openFileOutput(file, Context.MODE_PRIVATE).use {
                it.write(contents?.toByteArray())
            }
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("位置情報テスト")
            .setContentText(intent?.getStringExtra("nameField") + "の位置情報を取得してるで")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openIntent)
            .build()

        startForeground(9999, notification)

        startLocationUpdates()

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        stopSelf()
    }

    private fun startLocationUpdates() {
        val locationRequest = createLocationRequest() ?: return
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}