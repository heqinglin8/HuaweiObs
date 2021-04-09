package com.quwan.tt.huaweiobs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val postObjectSample = findViewById<TextView>(R.id.PostObjectSample)
        postObjectSample.setOnClickListener {
            val intent = Intent(this,PostObjectSample::class.java)
            startActivity(intent)
        }
    }
}