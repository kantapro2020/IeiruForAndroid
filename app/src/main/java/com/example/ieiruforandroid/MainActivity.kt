package com.example.ieiruforandroid

import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log.d
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    // 位置情報を取得できるクラス
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var location : Location? = null
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
                location = locationResult?.lastLocation ?: return
                val loc = location
                Toast.makeText(this@MainActivity,
                    "緯度:${loc?.latitude}, 経度:${loc?.longitude}", Toast.LENGTH_LONG).show()
            }
        }

        // 位置情報を更新
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        setContentView(R.layout.activity_main)
    }
    fun btnSendOnClick(view: View) {
        val txtResult: TextView = findViewById(R.id.txtResult);
        val queue = Volley.newRequestQueue(this)
        val url = "http://18.176.193.22/users"
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
//                txtResult.text = "Response is: ${response.substring(0, 500)}"

                d("うんち", response.toString())
                val parentJsonObj = JSONObject(response.toString())
                val dataJsonObj = parentJsonObj.getJSONArray("data")
                txtResult.text = dataJsonObj.toString()
            },
            Response.ErrorListener { error ->
                d("うんち", error.toString())
                txtResult.text = "うんこぴーや"
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params = JSONObject() // Host object
                val requiredParams = JSONObject()
                requiredParams.put("name", txtName.getText().toString())
                requiredParams.put("latitude", location?.latitude.toString())
                requiredParams.put("longitude", location?.longitude.toString())
                params.put("user", requiredParams)
                return params.toString().toByteArray()
            }
//            override fun getParams(): Map<String, String> {
//                val txtName: TextView = findViewById(R.id.txtName);
//                val params: MutableMap<String, String> = HashMap()
//                params["name"] = txtName.getText().toString()
//                params["latitude"] = location?.latitude.toString()
//                params["longitude"] = location?.longitude.toString()
//                return params
//            }
        }
        queue.add(stringRequest)
    }
}
//            Response.ErrorListener {
//                txtResult.text = "That didn't work!"
//            })

        // Add the request to the RequestQueue.
//        queue.add(stringRequest)


        // Request a string response from the provided URL.
//        val stringRequest = StringRequest(
//            Request.Method.GET, url,
//            Response.Listener<String> { response ->
//                // Display the first 500 characters of the response string.
////                txtResult.text = "Response is: ${response.substring(0, 500)}"
//
//                d("うんち", response.toString())
//                val parentJsonObj = JSONObject(response.toString())
//                var today_article = parentJsonObj.getString("status")
////                d("うんち", today_article)
//                txtResult.text = today_article
//            },
//            Response.ErrorListener { error ->
//                d("うんち", error.toString())
//                txtResult.text = "うんこぴーや"
//            })
////            Response.ErrorListener {
////                txtResult.text = "That didn't work!"
////            })
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest)

//        val txtName: TextView = findViewById(R.id.txtName);
//        txtResult.setText(txtName.getText());



