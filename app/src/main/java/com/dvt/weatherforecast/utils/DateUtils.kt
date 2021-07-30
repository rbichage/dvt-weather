package com.dvt.weatherforecast.utils

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


fun Long.convertToDay(): String {
    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    return dateFormat.format(Date(this))
}

fun convertTimeStamp(currentTime: Long): String {

//    2020-05-27
    val epoch = currentTime * 1000
    Timber.d("convertTimeStamp: $epoch")
    Timber.e("current time ${System.currentTimeMillis()}")

    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    return dateFormat.format(Date(epoch))
}