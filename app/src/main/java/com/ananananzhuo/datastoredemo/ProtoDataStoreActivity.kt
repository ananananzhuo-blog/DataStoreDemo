package com.ananananzhuo.datastoredemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_proto_data_store.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class ProtoDataStoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proto_data_store)
        btn_proto_save.setOnClickListener {
            GlobalScope.launch {
                incrementCounter()
            }
        }
        btn_proto_gaint.setOnClickListener {
            val exampleCounterFlow: Flow<Int> = settingsDataStore.data
                .map { settings ->
                    logEE(settings.exampleCounter.toString())//获取数据并打印
                    settings.exampleCounter
                }

        }
    }

    /**
     * 存储数据，聚聚加一
     */
    suspend fun incrementCounter() {
        settingsDataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setExampleCounter(currentSettings.exampleCounter + 1)
                .build()
        }
    }
}

