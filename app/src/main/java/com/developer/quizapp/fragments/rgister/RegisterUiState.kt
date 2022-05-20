package com.developer.quizapp.fragments.rgister

import com.developer.quizapp.models.User


data class RegisterUiState(
    val isLoading: Boolean = false,
    val data: User? = null,
    val error: String?=null,
)