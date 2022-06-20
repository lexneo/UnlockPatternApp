package com.lexneoapps.unlockpatternapp.utils

object PwUtil {
    //transform string to Integer list for comparing purposes
    fun transformStringToIntList(string: String): List<Int> {
        var currentString = string
        val intList = mutableListOf<Int>()
        while (currentString.isNotEmpty()) {
            val check = currentString.takeWhile { it != ',' }
            intList.add(check.toInt())
            currentString = currentString.removeRange(0..check.length)
        }
        return intList
    }

    //check if two password are the same with 0.5 +/- ponder
     fun checkIfSamePassword(f: List<Int>, s: List<Int>): Boolean {
        if (f.size != s.size) return false
        for (i in f.indices) {
            if ((f[i] < (s[i] - s[i] * 0.5).toInt()) || (f[i] > (s[i] + s[i] * 0.5).toInt())) {
                return false
            }
        }
        return true
    }
}