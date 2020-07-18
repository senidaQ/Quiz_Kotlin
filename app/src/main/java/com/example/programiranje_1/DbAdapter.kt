package com.example.programiranje_1

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement

internal class DbAdapter(private val mContext: Context) :
    SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {

    private val mDb = writableDatabase

    init {
        onUpgrade(mDb, 0, 0)
    }

    override fun onCreate(db: SQLiteDatabase) {
        var read: CSVReader
        var stmt: SQLiteStatement
        db.beginTransaction()
        try {

            //bosanski - pitanja/odgovori

            db.execSQL(pitanjaSchema)

            stmt = db.compileStatement(
                "INSERT INTO pitanja "
                        + "(_id, pitanje, tezina, tip_odgovora) "
                        + "VALUES(?, ?, ?, ?)"
            )

            read = CSVReader(mContext.assets.open("pitanja.csv"))

            while (read.next()) {
                val line = read.getLine()
                stmt.bindLong(1, line[0]!!.toLong())
                stmt.bindString(2, line[1])
                stmt.bindLong(3, line[2]!!.toLong())
                stmt.bindLong(4, line[3]!!.toLong())
                stmt.executeInsert()
            }

            db.execSQL(odgovoriSchema)
            stmt = db.compileStatement(
                "INSERT INTO odgovori "
                        + "(_id, odgovor) "
                        + "VALUES(?, ?)"
            )
            read =
                CSVReader(mContext.assets.open("odgovori.csv"))
            while (read.next()) {
                val line = read.getLine()
                stmt.bindLong(1, line[0]!!.toLong())
                stmt.bindString(2, line[1])
                stmt.executeInsert()
            }

            db.execSQL(pitanjaOdgovoriSchema)

            stmt = db.compileStatement(
                "INSERT INTO pitanja_odgovori "
                        + "(pitanje_id, odgovor_id, tacno) "
                        + "VALUES(?, ?, ?)"
            )

            read = CSVReader(
                mContext.assets.open(
                    "pitanjaOdgovori.csv"
                )
            )

            while (read.next()) {
                val line = read.getLine()

                stmt.bindLong(1, line[0]!!.toLong())
                stmt.bindLong(2, line[1]!!.toLong())
                stmt.bindLong(3, line[2]!!.toLong())
                stmt.executeInsert()
            }

            //engleski - pitanja/odgovori

            db.execSQL(questionsSchema)
            stmt = db.compileStatement(
                "INSERT INTO questions "
                        + "(_id, pitanje, tezina, tip_odgovora) "
                        + "VALUES(?, ?, ?, ?)"
            )
            read = CSVReader(mContext.assets.open("questions.csv"))

            while (read.next()) {
                val line = read.getLine()
                stmt.bindLong(1, line[0]!!.toLong())
                stmt.bindString(2, line[1])
                stmt.bindLong(3, line[2]!!.toLong())
                stmt.bindLong(4, line[3]!!.toLong())
                stmt.executeInsert()
            }

            db.execSQL(answersSchema)
            stmt = db.compileStatement(
                "INSERT INTO answers "
                        + "(_id, odgovor) "
                        + "VALUES(?,?)"
            )
            read =
                CSVReader(mContext.assets.open("answers.csv"))
            while (read.next()) {
                val line = read.getLine()
                stmt.bindLong(1, line[0]!!.toLong())
                stmt.bindString(2, line[1])
                stmt.executeInsert()
            }

            db.execSQL(statsSchema)
            db.setTransactionSuccessful()
        } catch (e: Exception) {

            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) {
        for (table in tables) {
            db.execSQL("DROP TABLE IF EXISTS $table")
        }
        onCreate(db)
    }

    fun fetchQuestions(): Cursor {
        return mDb.query(
            "questions",
            arrayOf("_id", "pitanje", "tezina", "tip_odgovora"),
            null,
            null,
            null,
            null,
            "_id"
        )
    }

    fun fetchPitanja(): Cursor {
        return mDb.query(
            "pitanja",
            arrayOf("_id", "pitanje", "tezina", "tip_odgovora"),
            null,
            null,
            null,
            null,
            "_id"
        )
    }

    fun Count(): Cursor {
        return mDb.rawQuery(
            ""
                    + "SELECT count(*) FROM pitanja",
            arrayOf<String>()
        )
    }

    fun fetchAnswers(id: Int): Cursor {
        return mDb.query(
            true,
            "answers",
            arrayOf("_id", "odgovor"),
            "_id = $id",
            null,
            null,
            null,
            null,
            "1"
        )
    }

    fun fetchOdgovori(id: Int): Cursor {
        return mDb.query(
            true,
            "answers",
            arrayOf("_id", "odgovor"),
            "_id = $id",
            null,
            null,
            null,
            null,
            "1"
        )
    }

    fun fetchpitanjaOdgovori(id: Int): Cursor {
        return mDb.query(
            "pitanja_odgovori",
            arrayOf("odgovor_id", "tacno"),
            "pitanje_id = $id",
            null,
            null,
            null,
            "pitanje_id"
        )
    }

    fun insertQuizStat(correct: Int, asked: Int): Boolean {
        val stmt = mDb.compileStatement(
                "INSERT INTO stats "
                        + "(questions_correct, questions_asked) "
                        + "VALUES (?,?)"
            )
        stmt.bindLong(1, correct.toLong())
        stmt.bindLong(2, asked.toLong())
        return stmt.executeInsert() != -1L
    }

    fun fetchStats(): Cursor {
        return mDb.query(
            "stats",
            arrayOf(
                "COUNT(questions_asked)",
                "SUM(questions_correct)",
                "SUM(questions_asked)"
            ),
            null,
            null,
            null,
            null,
            null
        )

    }

    fun fetchLastStats(): Cursor {
        return mDb.query(
            "stats",
            arrayOf(
                "COUNT(questions_asked) AS total_quizzes",
                "questions_correct AS total_correct",
                "questions_asked AS total_asked"
            ), null, null, null, null,
            "1", "1"
        )

    }

    companion object {
        private const val DATABASE_NAME = "quiz"
        private const val DATABASE_VERSION = 1

        private const val pitanjaSchema = ("CREATE TABLE \"pitanja\" "
                + "(\"_id\" INTEGER PRIMARY KEY  NOT NULL , "
                + "\"pitanje\" VARCHAR NOT NULL, "
                + "\"tezina\" INTEGER NOT NULL, "
                + "\"tip_odgovora\" INTEGER NOT NULL)" )
        private const val odgovoriSchema = ("CREATE TABLE \"odgovori\" "
                + "(\"_id\" INTEGER PRIMARY KEY  NOT NULL , "
                + "\"odgovor\" TEXT NOT NULL)" )
        private const val pitanjaOdgovoriSchema = ("CREATE TABLE \"pitanja_odgovori\" "
                + "(\"pitanje_id\" INTEGER NOT NULL , "
                + "\"odgovor_id\" INTEGER NOT NULL , "
                + "\"tacno\" INTEGER NOT NULL,"
                + "FOREIGN KEY (\"pitanje_id\") REFERENCES pitanja(\"_id\"), "
                + "FOREIGN KEY (\"odgovor_id\") REFERENCES odgovori(\"_id\"))" )
        private const val questionsSchema = ("CREATE TABLE \"questions\" "
                + "(\"_id\" INTEGER PRIMARY KEY NOT NULL, "
                + "\"pitanje\" VARCHAR NOT NULL, "
                + "\"tezina\" INTEGER NOT NULL, "
                + "\"tip_odgovora\" INTEGER NOT NULL)" )
        private const val answersSchema = ("CREATE TABLE \"answers\" "
                + "(\"_id\" INTEGER PRIMARY KEY  NOT NULL , "
                + "\"odgovor\" TEXT NOT NULL)" )
        private const val statsSchema = ("CREATE TABLE \"stats\" "
                + "(\"questions_correct\" INTEGER NOT NULL, "
                + "\"questions_asked\" INTEGER NOT NULL)")
        private val tables = arrayOf("pitanja", "odgovori", "pitanja_odgovori", "questions", "answers", "stats")
    }

}
