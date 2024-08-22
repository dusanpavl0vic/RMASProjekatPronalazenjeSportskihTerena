package com.example.sportfields.repositories

import com.example.sportfields.models.Field
import com.example.sportfields.models.Score

interface ScoreRepository {
    suspend fun getScores(
        fieldId: String
    ) : Resource<List<Score>>

    suspend fun getUserScore() : Resource<List<Score>>
    suspend fun addScore(
        fieldId: String,
        score: Int,
        comment: String,
        field: Field
    ) : Resource<String>

    suspend fun updateScore(
        scoreId: String,
        score: Int,
    ) : Resource<String>

    suspend fun updateComment(
        scoreId: String,
        comment: String,
    ) : Resource<String>
}