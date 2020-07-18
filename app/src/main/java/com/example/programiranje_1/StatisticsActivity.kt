package com.example.programiranje_1

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_statistics.*

class StatisticsActivity : AppCompatActivity() {
    private var results: Array<String?> = arrayOfNulls(6)
    private var allStats = true
    private var quizzes = 0
    private var asked = 13
    private var correct = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        getExtraData()

        if (savedInstanceState == null) {
            getStats()
        } else {
            restoreStats(savedInstanceState)
        }

        updateInterface()

        backToMenuButton.setOnClickListener {
            val intent = Intent(this@StatisticsActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getExtraData() {
        val b = intent.extras
        allStats = true
        allStats = b == null || b.getBoolean("allStats")
    }

    private fun getStats() {
        /*val db = DbAdapter(this)
        val c: Cursor
        c = if (allStats) {
            db.fetchStats()
        } else {
            db.fetchLastStats()
        }
        if(c.moveToFirst() != null) {

            quizzes = c.getInt(0)
            correct = c.getInt(1)
            asked = c.getInt(2)
            println(quizzes.toString() + "  " + correct + "  " + asked)
        }

        c.close()
        db.close()*/

        val score: Float = if (asked != 0) {
            correct.toFloat() / asked.toFloat() * 100
        } else {
            0F
        }
        val incorrect = asked.toInt() - correct.toInt()
        val average: Float = if (asked.toInt() != 0) {
            asked.toFloat()
        } else {
            0F
        }

        results[0] = if (allStats) this.getString(R.string.total_stat) else this.getString(
            R.string.current_stat
        )
        results[1] = quizzes.toString()
        results[2] = correct.toString()
        results[3] = incorrect.toString()
        results[4] = roundFloatToDecimalPlaces(score, 1).toString()
        results[5] = average.toString()
    }

    private fun roundFloatToDecimalPlaces(value: Float, places: Int): Float {
        val p = Math.pow(10.0, places.toDouble()).toFloat()
        return Math.round(value * p).toFloat() / p
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putStringArray("results", results)
    }

    private fun restoreStats(s: Bundle) {
        results = s.getStringArray("results")!!
    }

    private fun updateInterface() {
        statsTitleView.text = results[0]

        row1Label.text = this.getString(R.string.taken)
        row1Result.text = results[1]

        row3Label.text = this.getString(R.string.correct)
        row3Result.text = results[2]

        row4Label.text = this.getString(R.string.incorrect)
        row4Result.text = results[3]

        row2Label.text = this.getString(R.string.score)
        row2Result.text = String.format("%s%%", results[4])

        val vis = if (allStats) View.VISIBLE else View.GONE
        firstRow.visibility = vis
        firstHor.visibility = vis
    }
}
