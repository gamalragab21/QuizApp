package com.example.quizapp.fragments.rgister

import com.example.quizapp.models.User


data class RegisterUiState(
    val isLoading: Boolean = false,
    val data: User? = null,
    val error: String?=null,
)