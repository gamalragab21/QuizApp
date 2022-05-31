package com.developer.quizapp.models

data class Result(
    val CorrectAnswer: Int=0,
    val InCorrectAnswer: Int=0,
    val QuestionsSize: Int=0,
    val timeValue: String="",
    val resultId: String="",
    val username: String="",
    val imageProfile: String="",
)