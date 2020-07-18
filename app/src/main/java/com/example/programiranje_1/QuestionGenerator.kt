package com.example.programiranje_1

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import java.util.concurrent.BlockingQueue

internal class QuestionGenerator(private val queue: BlockingQueue<Bundle>, private val ctx: Context) : Thread() {
    @Volatile
    var done = false

    private lateinit var correctAns: Array<String?>
    private lateinit var answers: Array<String?>
    private lateinit var questions: Array<String?>

    override fun run() {

            val db = DbAdapter(ctx)
            var cursorBrojac: Cursor
            cursorBrojac = db.Count()
            cursorBrojac.moveToFirst()
            var ukupnoPitanja = ((cursorBrojac.getString(0).trim()).toInt())

            questions = arrayOfNulls(ukupnoPitanja)
            answers = arrayOfNulls(4 * ukupnoPitanja)
            correctAns = arrayOfNulls(4*ukupnoPitanja)

            var cursorQ: Cursor
            var cursorA: Cursor
            var cursorQA: Cursor
            var QuestionId = 0
            var AnswerId = 0

            cursorQ = db.fetchQuestions()
            var i = 0 //index niza questions
            var j = 0 //index niza answers
            cursorQ.moveToFirst()

            while (ukupnoPitanja != 0) {

                QuestionId = cursorQ.getString(0).trim().toInt()
                questions[i] = cursorQ.getString(1)
                println(questions[i])

                cursorQA = db.fetchpitanjaOdgovori(QuestionId)
                cursorQA.moveToFirst()
                do {
                    AnswerId = cursorQA.getString(0).toInt()
                    cursorA = db.fetchAnswers(AnswerId)
                    cursorA.moveToFirst()
                    answers[j] = cursorA.getString(1).trim()
                    correctAns[j] = cursorQA.getString(1).trim()
                    println(answers[j].toString() + "  " + correctAns[j])
                    j++
                    cursorA.close()
                } while (cursorQA.moveToNext())
                i++
                j = i*4
                cursorQA.close()
                cursorQ.moveToNext()
                ukupnoPitanja--
            }
            cursorQ.close()
            cursorBrojac.close()

            db.close()

            val b = Bundle()
            b.putStringArray("questions", questions)
            b.putStringArray("buttons", answers)
            b.putStringArray("correctAns", correctAns)

            try {
                queue.put(b)
                println("USPJESNP")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }


