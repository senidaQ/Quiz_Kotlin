package com.example.programiranje_1

import android.content.Intent
import android.os.Bundle
import android.support.v4.text.HtmlCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.toast_layout.*
import kotlinx.android.synthetic.main.toast_layout.view.*
import java.util.concurrent.BlockingQueue

class QuizActivity : AppCompatActivity() {
    @Volatile
    private var elapsed: Long = 0
    @Volatile
    private var running: Boolean = false
    private lateinit var answers: Array<String?>
    private lateinit var questions: Array<String?>
    private lateinit var correctAns: Array<String?>
    private var answer: Int = 0
    private var score: Int = 0
    private var total: Int = 0
    private var asked: Int = 0
    private var j: Int = 0
    private var k : Int = 0

    private lateinit var queue: BlockingQueue<Bundle>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        queue = (applicationContext as App).queue

        if (savedInstanceState == null) {

            total = 13//(editTextBrojPitanja.getText().toString()).toInt()

            questions = arrayOfNulls(total)
            answers = arrayOfNulls(4*total)
            correctAns = arrayOfNulls(4*total)
            elapsed = 0
            running = true
            asked = 0
            answer = -1
            score = 0
            j = asked*4
            k = j
            generateQuiz()
        } else {
            loadDataBundle(savedInstanceState)
            loadStateBundle(savedInstanceState)
        }

        setQuizButtonOnClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putLong("elapsed", elapsed)
        outState?.putBoolean("running", running)
        outState?.putStringArray("questions", questions)
        outState?.putStringArray("buttons", answers)
        outState?.putStringArray("correctAns", correctAns)
        outState?.putInt("score", score)
        outState?.putInt("total", total)
        outState?.putInt("j", j)
        outState?.putInt("k", k)
        outState?.putInt("asked", asked)
    }

    override fun onResume() {
        super.onResume()
        running = true
    }

    override fun onPause() {
        super.onPause()
        running = false
    }


    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun loadDataBundle(b: Bundle) {
        questions = b.getStringArray("questions")!!
        answers = b.getStringArray("buttons")!!
        correctAns = b.getStringArray("correctAns")!!
    }

    private fun loadStateBundle(b: Bundle) {
        elapsed = b.getLong("elapsed")
        running = b.getBoolean("running")
        score = b.getInt("score")
        total = b.getInt("total")
        j = b.getInt("j")
        k = b.getInt("k")
        asked = b.getInt("asked")
    }

    private fun saveStats() {
        val db = DbAdapter(this)
        db.insertQuizStat(score, total)
        db.close()
    }

    private fun generateQuiz() {
        try {
            loadDataBundle(queue.take())
            displayQuiz()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun displayQuiz() {
        scoreView.text = String.format("Score: %d of %d", score, total)
        quizView.text = HtmlCompat.fromHtml(questions[asked].toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        buttonA.text = answers[j]
        buttonB.text = answers[j+1]
        buttonC.text = answers[j+2]
        buttonD.text = answers[j+3]

    }

    private fun setQuizButtonOnClickListeners() {
        buttonA.setOnClickListener(ButtonClickHelper(0))
        buttonB.setOnClickListener(ButtonClickHelper(1))
        buttonC.setOnClickListener(ButtonClickHelper(2))
        buttonD.setOnClickListener(ButtonClickHelper(3))
    }

    private inner class ButtonClickHelper internal constructor(i: Int) : OnClickListener {
        private var id = -1

        init {
            id = i

        }

        override fun onClick(v: View) {
            val layout = layoutInflater.inflate(R.layout.toast_layout, toast_layout_root)
            val toast = Toast(applicationContext)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout

            if (correctAns[id+k] != null && correctAns[id+k]!!.toInt() == 1) {
                score++

                layout.image.setImageResource(R.drawable.correct)
                layout.text.text = resources.getString(R.string.correct_response)

                toast.setGravity(Gravity.CENTER_VERTICAL, -175, 75) // offset

            } else {

                layout.image.setImageResource(R.drawable.incorrect)
                layout.text.text = resources.getString(R.string.incorrect_response)

                toast.setGravity(Gravity.CENTER_VERTICAL, 175, 75) // offset

            }
            asked++
            j = asked*4
            k = j

            if(asked < questions.size) displayQuiz()
            else {
                saveStats()
                /* setResult(RESULT_OK)
                finish()*/
                val intent = Intent(this@QuizActivity, StatisticsActivity::class.java)

                intent.putExtra("correct", score)
                startActivity(intent)
            }
        }
    }
}