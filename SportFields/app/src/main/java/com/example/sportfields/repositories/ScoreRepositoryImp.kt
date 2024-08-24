package com.example.sportfields.repositories

import com.example.sportfields.models.Field
import com.example.sportfields.models.Score
import com.example.sportfields.services.DatabaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScoreRepositoryImp : ScoreRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val databaseService = DatabaseService(firestoreInstance)
    override suspend fun getScores(
        fieldId: String
    ): Resource<List<Score>> {
        return try {
            val scoreDocRef = firestoreInstance.collection("score")
            val querySnapshot = scoreDocRef.get().await()
            val scoresList = mutableListOf<Score>()
            for (document in querySnapshot.documents) {
                val fId = document.getString("fieldId") ?: ""
                if (fId == fieldId) {
                    scoresList.add(
                        Score(
                            id = document.id,
                            userId = document.getString("userId") ?: "",
                            fieldId = fieldId,
                            score = document.getLong("score")?.toInt() ?: 0,
                        )
                    )
                }
            }
            Resource.Success(scoresList)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


    override suspend fun getUserScore(): Resource<List<Score>> {
        return try{
            val scoreDocRef = firestoreInstance.collection("score")
            val querySnapshot = scoreDocRef.get().await()
            val scoresList = mutableListOf<Score>()
            for(document in querySnapshot.documents){
                val userId = document.getString("userId") ?: ""
                if(userId == firebaseAuth.currentUser?.uid){
                    scoresList.add(Score(
                        id = document.id,
                        fieldId = document.getString("fieldId") ?: "",
                        userId = userId,
                        score = document.getLong("score")?.toInt() ?: 0,
                    ))
                }
            }
            Resource.Success(scoresList)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


    override suspend fun addScore(
        fieldId: String,
        score: Int,
        field: Field
    ): Resource<String> {
        return try{
            val myScore = Score(
                userId = firebaseAuth.currentUser!!.uid,
                fieldId = fieldId,
                score = score,
            )
            databaseService.addScore(field.userId, score)
            databaseService.addScore(firebaseAuth.currentUser!!.uid, 2)
            val result = databaseService.saveScore(myScore)
            result
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }



    override suspend fun updateScore(
        scoreId: String,
        score: Int,
    ): Resource<String> {
        return try{
            val result = databaseService.updateScore(scoreId, score)
            result
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


}