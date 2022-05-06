package com.example.quizapp.models

import com.developers.healtywise.common.helpers.utils.Constants.HOLDER_ICON

data class User (
    val userId:String="",
    val username:String="",
    val email:String="",
    val admin:Boolean=false,
    val imageProfile:String=HOLDER_ICON
        )