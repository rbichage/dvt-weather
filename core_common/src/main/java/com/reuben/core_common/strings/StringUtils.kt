package com.reuben.core_common.strings

import java.util.Locale

object StringUtils {
    fun String.capitalizeWords(): String {

        val initial = StringBuilder()

        val words = this.split(" ")

        for (word in words) {

            initial.append("${
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }
            } "
            )

        }

        return initial.toString()
    }
}