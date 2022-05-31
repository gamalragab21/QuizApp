package com.developer.quizapp.models

data class QuestionList(
    var questionId:String="",
    val question: String="",
    val option1: String="",
    val option2: String="",
    val option3: String="",
    val option4: String="",
    val answer: String="",
    val profId: String="",
    val subject: Subject=Subject(),
    val quizId: String="",
    val quizName: String="",
    val timer: String="",
    var userSelectedAnswer: String="",
)