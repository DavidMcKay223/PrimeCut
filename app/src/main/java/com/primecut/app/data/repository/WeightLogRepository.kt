package com.primecut.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import com.primecut.app.data.dao.WeightLogDao
import com.primecut.app.data.model.WeightLog
import kotlin.random.Random

class WeightLogRepository(private val dao: WeightLogDao) {

    suspend fun getUserLogs(userId: String, showProjectionWeight: Boolean = false): List<WeightLog> =
        withContext(Dispatchers.IO) {
            val results = dao.getUserLogs(userId).toMutableList()

            if (showProjectionWeight && results.isNotEmpty()) {
                val last7Days = LocalDate.now().minusDays(6).toString()
                val recentLogs = dao.getRecentLogs(userId, last7Days)

                if (recentLogs.isNotEmpty()) {
                    val totalWeight = recentLogs.sumOf { it.weightLbs.toDouble() }.toFloat()
                    val daysTracked = (LocalDate.now().toEpochDay() - LocalDate.parse(recentLogs.minOf { it.date }).toEpochDay() + 1).toInt()
                    val avgDailyWeight = totalWeight / daysTracked

                    val estimatedMaintenanceCalories = 2800f
                    val caloriesPerPound = 3500f
                    val dailyCalorieDelta = avgDailyWeight - estimatedMaintenanceCalories
                    val dailyWeightChange = dailyCalorieDelta / caloriesPerPound

                    var currentWeight = results.last().weightLbs
                    val weeksToProject = 2

                    repeat(weeksToProject) { week ->
                        val daysToAdd = (week + 1) * 7
                        var projectedWeight = currentWeight + (dailyWeightChange * daysToAdd)
                        projectedWeight += Random.nextDouble(-1.0, 1.0).toFloat()
                        projectedWeight = projectedWeight.coerceAtLeast(50f)

                        results.add(
                            WeightLog(
                                userId = userId,
                                date = LocalDate.now().plusDays(daysToAdd.toLong()).toString(),
                                weightLbs = projectedWeight
                            )
                        )
                    }
                }
            }

            results
        }

    suspend fun addOrUpdateLog(userId: String, date: String, weightLbs: Float) =
        withContext(Dispatchers.IO) {
            dao.addOrUpdate(userId, date, weightLbs)
        }
}
