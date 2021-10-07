package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

const val TAG = "COROUTINES"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG, "Hello, ")

        runBlocking {
            suspendedFun1()
        }
    }

    private suspend fun suspendedFun1() {
        delay(1000)
        Log.i(TAG, "World!")
    }
}