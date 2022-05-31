package com.developer.quizapp.models

import com.developer.quizapp.fragments.admin.Department
import com.google.firebase.database.Exclude

data class Level(
    var id: String="",
    var name: String="",
    @get:Exclude
    var products:List<Department> = emptyList()
)