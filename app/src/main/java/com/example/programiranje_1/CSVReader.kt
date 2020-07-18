package com.example.programiranje_1

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Vector

internal class CSVReader(input: InputStream) {
    private val reader: BufferedReader = BufferedReader(InputStreamReader(input))
    private val line: Vector<String> = Vector()

    operator fun next(): Boolean {
        line.clear()
        try {
            val currentLine = reader.readLine()
            var currentElement = StringBuilder()
            var inQuote = false
            if (currentLine == null)
                return false
            for (i in 0 until currentLine.length) {
                if (currentLine[i] == ' ' && !inQuote)
                    continue
                if (currentLine[i] == '"' && !inQuote) {
                    inQuote = true
                    continue
                }
                if (currentLine[i] == ',' && !inQuote) {
                    line.add(currentElement.toString())
                    currentElement = StringBuilder()
                    continue
                }
                if (currentLine[i] == '"' && inQuote) {
                    inQuote = false
                    continue
                }
                if (currentLine[i] != '"')
                    currentElement.append(currentLine[i])
            }
            line.add(currentElement.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true

    }

    fun getLine(): Array<String?> {
        val ret = arrayOfNulls<String>(line.size)
        line.toArray(ret)
        return ret
    }

}
