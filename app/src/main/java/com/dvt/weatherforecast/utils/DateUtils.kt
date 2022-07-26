package com.dvt.weatherforecast.utils

import java.text.SimpleDateFormat
import java.util.*


fun Long.convertToDay(): String {
    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    return dateFormat.format(Date(this))
}

fun convertTimeStamp(currentTime: Long): String {

    val epoch = currentTime * 1000

    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    return dateFormat.format(Date(epoch))
}

fun convertToDateTime(currentTime: Long): String {

    val dateFormat = SimpleDateFormat("yyyy MMM dd', ' HH:mm", Locale.getDefault())

    return dateFormat.format(Date(currentTime))
}