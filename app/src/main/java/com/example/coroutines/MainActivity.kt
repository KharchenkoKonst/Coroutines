package com.example.coroutines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

const val TAG = "testCoroutines"

class MainActivity : AppCompatActivity() {

    private lateinit var out: TextView
    private lateinit var counter: TextView

    private suspend fun coroutineWithException() {
        delay(100)
        printToScreen("in")
        throw Exception("some error")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        out = findViewById(R.id.output)
        out.movementMethod = ScrollingMovementMethod()
        counter = findViewById(R.id.counter)

        val scope = CoroutineScope(Job() + Dispatchers.Main)

        lifecycleScope.launch {
            try {
                launch {
                    coroutineWithException()
                }
            } catch (e: Exception) {
                printToScreen("catch")
            }

        }

        /**
         * Глобальная корутина между активити
         */
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
        // runBlocking означает, что вызывающий поток блокируется на время вызова сопрограммы
        // Здесь выполнение идёт на main-thread, значит он будет приостановлен - вывод внутри блока произойдёт раньше
        runBlocking {
            delay(5000)
            printToScreen("${Thread.currentThread().name} - in blocking")
        }
        printToScreen("${Thread.currentThread().name} - out of blocking")
        printToScreen("")

        // withContext - аналог async/await, приостанавливающий внешний scope без вызова await
        MainScope().launch {
            withContext(Dispatchers.IO) {
                delay(2000)
                printToScreen("${Thread.currentThread().name} - in new context")
            }
            printToScreen("${Thread.currentThread().name} - out of new context")
        }
    }

    fun runAsyncClick(view: android.view.View) {

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
                printToScreen(x.await().toString() + "by await")
            }
            delay(1000)
            printToScreen("waiting for result by suspend: ")
            val x = usefulResultWork()
            printToScreen(x.toString() + "just by suspend")
        }

        // LazyJob
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

    fun runDispatchersTestClick(view: android.view.View) {
        lifecycleScope.launch {
            printToScreen("Starts ${Thread.currentThread().name}")
            printToScreen("Starts 8 Dispatchers.Default")
            repeat(8) {
                launch(Dispatchers.Default) {
                    printToScreen(Thread.currentThread().name)
                }
            }
            delay(100)

            coroutineScope {}

            val res = async { usefulResultWork() }
            launch(Dispatchers.IO) {
                out.text = "${out.text}${Thread.currentThread().name} change UI from IO\n"
            }
//                printToScreen(res.await().toString())
            repeat(8) {
                launch(Dispatchers.IO) {
                    printToScreen(Thread.currentThread().name)
                }
            }
        }
    }
}

