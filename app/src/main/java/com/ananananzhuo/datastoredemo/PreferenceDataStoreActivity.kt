package com.ananananzhuo.datastoredemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ananananzhuo.datastoredemo.App.Companion.dataStore
import kotlinx.android.synthetic.main.activity_preference_data_store.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun logEE(msg: String) {
    Log.e("安安安安卓", msg)
}

class PreferenceDataStoreActivity : AppCompatActivity() {
    val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference_data_store)

        btn_gaint.setOnClickListener {
            val exampleCounterFlow: Flow<Int> = dataStore.data
                .map { preferences ->
                    logEE(preferences[EXAMPLE_COUNTER].toString())//打印数据
                    preferences[EXAMPLE_COUNTER] ?: 0
                }
        }
        btn_save.setOnClickListener {
            GlobalScope.launch {
                incrementCounter()
            }
        }
    }

    suspend fun incrementCounter() {
        dataStore.edit { settings ->
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
    }
}