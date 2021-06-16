package com.ananananzhuo.datastoredemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_sharepreference.setOnClickListener {
            startActivity(Intent(this,PreferenceDataStoreActivity::class.java))
        }

        btn_proto.setOnClickListener {
            startActivity(Intent(this,ProtoDataStoreActivity::class.java))
        }
    }
}