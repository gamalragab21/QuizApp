package com.developer.quizapp.fragments.login

import com.developer.quizapp.models.User

data class LoginUiState(
    val isLoading: Boolean = false,
    val data: User? = null,
    val error: String?=null,
)