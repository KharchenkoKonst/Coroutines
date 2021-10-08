package com.example.coroutines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

const val TAG = "testCoroutines"

class MainActivity : AppCompatActivity() {
    private lateinit var out: TextView
    private lateinit var counter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        out = findViewById(R.id.output)
        counter = findViewById(R.id.counter)

        GlobalScope.launch {
            while (true) {
                delay(5000)
                Log.i(TAG, "global")
            }
        }
        lifecycleScope.launch {
            while (true) {
                delay(5000)
                Log.i(TAG, "local")
            }
        }
    }

    private fun printToScreen(text: String) {
        out.text = "${out.text}$text\n"
    }

    fun runBlockingClick(view: android.view.View) {
        /**
         * runBlocking означает, что вызывающий поток блокируется на время вызова сопрограммы
         */
        runBlocking {
            usefulUnitWork()
            printToScreen("blocking callback")
        }
    }

    fun runAsyncClick(view: android.view.View) {
        lifecycleScope.launch {
            usefulUnitWork()
            printToScreen("async callback")
        }
        lifecycleScope.launch {
            launch {
                val job = launch { usefulResultWork() }
                printToScreen("waiting for result by join: ")
                val x = usefulResultWork()
                job.join()
                printToScreen(x.toString() + "by join")
            }
            delay(1000)
            launch {
                printToScreen("waiting for result by async: ")
                val x = async { usefulResultWork() }
                printToScreen("awaiting")
                printToScreen(x.await().toString() + "by await")
            }
            delay(1000)
            printToScreen("waiting for result by suspend: ")
            val x = usefulResultWork()
            printToScreen(x.toString() + "just by suspend")
        }
        lifecycleScope.launch {
            val lazyJob = launch(start = CoroutineStart.LAZY) {
                printToScreen("lazy job starts")
                delay(1000)
                printToScreen("lazy job completed")
            }
            delay(10000)
            lazyJob.start()
        }
    }

    fun minusClick(view: android.view.View) {
        var count = counter.text.toString().toInt()
        count--
        counter.text = count.toString()
    }

    fun plusClick(view: android.view.View) {
        var count = counter.text.toString().toInt()
        count++
        counter.text = count.toString()
    }

    private suspend fun usefulUnitWork() {
        delay(10000)
    }

    private suspend fun usefulResultWork(): Int {
        delay(5000)
        return 10
    }

    fun nextActivityClick(view: android.view.View) {
        startActivity(Intent(this, MainActivity2::class.java))
        finish()
    }

    fun runDispatchersTest(view: android.view.View) {

    }
}