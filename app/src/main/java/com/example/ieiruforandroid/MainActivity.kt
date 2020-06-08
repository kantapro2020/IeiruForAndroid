package com.example.ieiruforandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun btnSendOnClick(view: View) {
        val txtName: TextView = findViewById(R.id.txtName);
        val txtResult: TextView = findViewById(R.id.txtResult);
        txtResult.setText(txtName.getText());
    }
}