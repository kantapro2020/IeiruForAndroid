package com.example.ieiruforandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.*
import android.os.Looper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // 位置情報を取得できるクラス
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = FusedLocationProviderClient(this)

        // どのような取得方法を要求
        val locationRequest = LocationRequest().apply {
            // 精度重視(電力大)と省電力重視(精度低)を両立するため2種類の更新間隔を指定
            // 今回は公式のサンプル通りにする。
            interval = 10000                                   // 最遅の更新間隔(但し正確ではない。)
            fastestInterval = 5000                             // 最短の更新間隔
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY  // 精度重視
        }

        // コールバック
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                // 更新直後の位置が格納されているはず
                val location = locationResult?.lastLocation ?: return
                Toast.makeText(this@MainActivity,
                    "緯度:${location.latitude}, 経度:${location.longitude}", Toast.LENGTH_LONG).show()
            }
        }

        // 位置情報を更新
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())


        setContentView(R.layout.activity_main)
    }
    fun btnSendOnClick(view: View) {
        val txtName: TextView = findViewById(R.id.txtName);
        val txtResult: TextView = findViewById(R.id.txtResult);
        txtResult.setText(txtName.getText());
    }
}