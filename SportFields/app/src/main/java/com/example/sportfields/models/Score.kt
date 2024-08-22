package com.example.sportfields.models


import com.google.firebase.firestore.DocumentId

data class Score (
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val fieldId: String = "",
    var score: Int = 0,
    var comment: String = ""
)