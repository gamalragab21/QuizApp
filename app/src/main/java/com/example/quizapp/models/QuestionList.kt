package com.example.quizapp.models

data class QuestionList(
    var questionId:String="",
    val question: String="",
    val option1: String="",
    val option2: String="",
    val option3: String="",
    val option4: String="",
    val answer: String="",
    var userSelectedAnswer: String="",
)