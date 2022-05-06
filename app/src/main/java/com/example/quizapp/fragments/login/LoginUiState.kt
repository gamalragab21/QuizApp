package com.example.quizapp.fragments.login

import com.example.quizapp.models.User

data class LoginUiState(
    val isLoading: Boolean = false,
    val data: User? = null,
    val error: String?=null,
)