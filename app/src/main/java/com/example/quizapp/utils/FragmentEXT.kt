package com.example.quizapp.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.google.android.material.snackbar.Snackbar


fun Fragment.snackbar(message: String) {
    requireView().hideKeyboard()
    Snackbar.make(
        requireView(),
        message,
        Snackbar.LENGTH_LONG
    ).show()

}

infix fun View.snackbar(message: String) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).show()

}
 fun Float.fess():Float =(  this* 10) / 100
 fun Float.toEGP():Float =(this) * 100
 fun Float.toCent():Float =(  this) / 100


fun deleteBackStakeAfterNavigate(fragmentId: Int) = NavOptions.Builder()
        .setPopUpTo(fragmentId, true)
        .build()

fun deleteBackStakeAfterNavigate2(fragmentId1: Int,fragmentId2: Int) = NavOptions.Builder()
    .setPopUpTo(fragmentId1, true)
    .setPopUpTo(fragmentId2, true)
    .build()

fun View.shareImageWithText() {
    val msg = StringBuilder()
    msg.append("Hey, Download this awesome app!")
    msg.append("\n")
    msg.append("https://play.google.com/store/apps/details?id=${context.packageName}") //example :com.package.name
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "*/*"
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg.toString())
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context,
                "No App Available",
                Toast.LENGTH_SHORT).show()
        }

}




