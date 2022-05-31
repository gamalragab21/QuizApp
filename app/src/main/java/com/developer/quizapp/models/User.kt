package com.developer.quizapp.models

import com.developer.quizapp.utils.Constants.HOLDER_ICON

data class User(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val role: Int = 0,
    val imageProfile: String = HOLDER_ICON,
    var ifProfIsAccept:Boolean=false
)