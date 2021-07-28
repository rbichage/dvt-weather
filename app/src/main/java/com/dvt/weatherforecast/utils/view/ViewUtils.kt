package com.dvt.weatherforecast.utils.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun Context.toast(message: String, length: Int) {
    Toast.makeText(this, message, length).show()
}


fun View.showSnackbar(message: String, length: Int) {
    val snackbar = Snackbar.make(this, message, length)

    snackbar.apply {
        setTextColor(ContextCompat.getColor(this.context, android.R.color.white))
        this.setBackgroundTint(ContextCompat.getColor(context, R.color.colorPrimary))
        show()

    }
}

internal fun View.setBackgroundTint(context: Context, color: Int) {
    backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(context, color)
    )
}


fun View.showErrorSnackbar(message: String, length: Int) {
    val snackbar = Snackbar.make(this, message, length)

    snackbar.apply {
        this.setBackgroundTint(ContextCompat.getColor(this.context, android.R.color.holo_red_light))
        this.setTextColor(ContextCompat.getColor(this.context, android.R.color.white))
        show()
    }
}

fun View.showSuccessSnackbar(message: String, length: Int) {
    val snackbar = Snackbar.make(this, message, length)

    snackbar.apply {
        this.setBackgroundTint(
            ContextCompat.getColor(view.context, R.color.colorPrimary)
        )
        this.setTextColor(ContextCompat.getColor(this.context, android.R.color.white))
        show()
    }
}

fun Activity.hideSoftInput() {
    val inputmethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputmethodManager.hideSoftInputFromWindow(getView().windowToken, 0)
}

fun Activity.getView(): View {
    return window.decorView.rootView
}

fun bitMapFromDrawable(drawable: Drawable): Bitmap? {

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun View.showRetrySnackBar(message: String, action: ((View) -> Unit)?) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)

    snackbar.apply {
        this.setBackgroundTint(ContextCompat.getColor(this.context, android.R.color.holo_red_light))

        val colorWhite = ContextCompat.getColor(this.context, android.R.color.white)
        this.setTextColor(colorWhite)
        this.setActionTextColor(colorWhite)
        setAction("RETRY") {
            dismiss()
            action?.invoke(this@showRetrySnackBar)
        }
        show()

    }
}

internal inline fun <reified T> Activity.navigateTo(
    clearTask: Boolean = false,
    noinline intentExtras: ((Intent) -> Unit)? = null
) {

    val intent = Intent(this, T::class.java)

    intentExtras?.run {
        intentExtras(intent)
    }

    if (clearTask) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    startActivity(intent)
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

private fun ActivityMainBinding.updateBackgrounds(thisColor: Int, drawable: Drawable) {
    root.setBackgroundColor(
        ContextCompat.getColor(this.root.context, thisColor)
    )

    weatherLayout.background = drawable
}

fun ActivityMainBinding.changeBackground(locationEntity: LocationEntity) {
    val id = locationEntity.weatherCondition
    val context = root.context

    when {

        //Thunderstorm
        id.startsWith("2", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_rainy)
            updateBackgrounds(R.color.colorRainy, cloudyBackground!!)
        }

        //drizzle
        id.startsWith("3", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_rainy)

            updateBackgrounds(R.color.colorRainy, cloudyBackground!!)
        }

        // Rain
        id.startsWith("5", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_rainy)

            updateBackgrounds(R.color.colorRainy, cloudyBackground!!)
        }

        //Snow
        id.startsWith("6", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_rainy)

            updateBackgrounds(R.color.colorRainy, cloudyBackground!!)
        }

        //Atmosphere
        id.startsWith("7", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_cloudy)

            updateBackgrounds(R.color.colorCloudy, cloudyBackground!!)
        }

        //sunny/clear

        id.equals("800", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_sunny)

            updateBackgrounds(R.color.colorSunny, cloudyBackground!!)
        }

        // cloudy
        id.toInt() > 800 -> {

            val cloudy = ContextCompat.getDrawable(context, R.drawable.forest_cloudy)

            updateBackgrounds(R.color.colorCloudy, cloudy!!)

        }


    }
}
