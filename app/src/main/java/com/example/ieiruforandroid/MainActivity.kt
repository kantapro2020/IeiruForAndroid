package com.example.ieiruforandroid

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        requestPermission()
        createNotificationChannel()
        val textView = findViewById<TextView>(R.id.ieiruTextView)
        val queue = Volley.newRequestQueue(this)
        val url = "http://18.176.193.22/users"

        startButton.setOnClickListener {
            val intent = Intent(this, LocationService::class.java)
            intent.putExtra("nameField", nameField.getText().toString())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
        }

        finishButton.setOnClickListener {
            val intent = Intent(this, LocationService::class.java)
            stopService(intent)
        }

        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                val jsonObj = JSONObject(response.toString())
                val users = jsonObj.getJSONArray("data")
                val mapper = jacksonObjectMapper()

                for (i in 0 until (users.length())) {
                    val user = mapper.readValue<User>(users[i].toString())
                    val ieiru = if (user.is_home) {"家いる"} else {"家いない"}
                    val usersTxt = textView.text
                    val userTxt = "${user.name} : ${ieiru}"
                    textView.text = "${usersTxt} \n ${userTxt}"
                    Log.d("あああああ", "${usersTxt} \n ${userTxt}")
                }
            },
            Response.ErrorListener { error ->
//                textView.text = "That didn't work!"
                Log.d("あああああああ", error.toString())
            })

        queue.add(stringRequest)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                LocationService.CHANNEL_ID,
                "お知らせ",
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "お知らせを通知します。"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestPermission() {
        val permissionAccessCoarseLocationApproved =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        if (permissionAccessCoarseLocationApproved) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

            if (backgroundLocationPermissionApproved) {
                // フォアグラウンドとバックグランドのバーミッションがある
            } else {
                // フォアグラウンドのみOKなので、バックグラウンドの許可を求める
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        } else {
            // 位置情報の権限が無いため、許可を求める
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }
}