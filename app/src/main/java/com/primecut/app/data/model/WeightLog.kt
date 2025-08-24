package com.primecut.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs")
data class WeightLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val date: String,
    val weightLbs: Float
)
