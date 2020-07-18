package com.example.programiranje_1

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var g: QuestionGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startQuizButton.setOnClickListener(StartListener())
        statisticsButton.setOnClickListener(StatsListener())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            QUIZ_START -> when (resultCode) {
                RESULT_OK -> {
                    val intent = Intent(this@MainActivity, StatisticsActivity::class.java)
                    intent.putExtra("allStats", false)
                    startActivity(intent)
                }
                RESULT_CANCELED -> {
                }
            }
        }
    }

    private inner class StartListener : OnClickListener {
        override fun onClick(v: View) {
            g = QuestionGenerator(
                (applicationContext as App).queue,
                applicationContext
            )
            g.start()
            val intent = Intent(this@MainActivity, QuizActivity::class.java)
            startActivityForResult(intent, QUIZ_START)
        }
    }

    private inner class StatsListener : OnClickListener {
        override fun onClick(v: View) {
            val intent = Intent(this@MainActivity, StatisticsActivity::class.java)
            intent.putExtra("allStats", true)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        g.done = true
    }


    companion object {
        const val QUIZ_START = 1
    }
}
