package com.appnroll.box.utils

import java.util.*


class RandomStringGenerator {
    private val source = "0123 4567 89AB CDEF GHIJ KLMN OPQR STUV WXYZ abcd efgh ijkl mnop qrst uvwx yz"
    private var random = Random()

    fun randomText(textLength: Int, randomizeLength: Int = 0): String {
        val sb = StringBuilder(textLength)
        val lengthDiff = if (randomizeLength > 0) {
            random.nextInt(randomizeLength)
        } else {
            0
        }
        for (i in 0 until textLength - lengthDiff)
            sb.append(source[random.nextInt(source.length)])
        return sb.toString()
    }
}