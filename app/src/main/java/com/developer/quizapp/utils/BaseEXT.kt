package com.developer.quizapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.developer.quizapp.activites.SetupActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Call
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*




fun isNetworkConnected(@ApplicationContext context: Context): Flow<Boolean> = flow {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    cm.let {
        val activeNetwork = cm.activeNetworkInfo
        emit(activeNetwork != null && activeNetwork.isConnectedOrConnecting)
    }

    emit(false)
}

fun Activity.statusBar(color: Int) {
    window.statusBarColor = ContextCompat.getColor(this, color)
}
fun navigateToSetupActivity(context: Context,activity:Activity) {
    context.startActivity(
        Intent(activity, SetupActivity::class.java)
            .setAction(Constants.ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT)
    )
    activity.finish()
}

fun NavController.navigateSafely(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null,
) {
    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(resId, args, navOptions, navExtras)
    }
}

fun setupTheme(isDarkMode: Boolean) {
    if (isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}

fun startChangeTheme(context: Context) {
    val isNightTheme = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    when (isNightTheme) {
        Configuration.UI_MODE_NIGHT_YES ->
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Configuration.UI_MODE_NIGHT_NO ->
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}

fun startTermsAndConditions(context: Context) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
    context.startActivity(browserIntent)
}

fun errorMessageHandler(call: Call, t: Throwable): Flow<String?> = flow {
    if (t is SocketTimeoutException) {
        emit("Connection timeout, Please try again!")
    } else if (t is HttpException) {
        val body: ResponseBody = (t as HttpException).response()?.errorBody()!!
        try {
            emit(body.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } else if (t is IOException) {
        emit("Request timeout, Please try again!")
    } else {
        //Call was cancelled by user
        if (call.isCanceled()) {
            emit("Call was cancelled forcefully, Please try again!")
        } else {
            emit("Network Problem, Please try again!")
        }
    }
    emit("Network Problem, Please try again!")
}

@SuppressLint("NewApi")
fun encodeKey(key: String): String {
    // encode a string using Base64 encoder
    val encoder: Base64.Encoder = Base64.getEncoder()
    return encoder.encodeToString(key.toByteArray())
}

@SuppressLint("NewApi")
fun decodeByte(byteKey: String): String {
    val decoder: Base64.Decoder = Base64.getDecoder()
    return String(decoder.decode(byteKey))
}








