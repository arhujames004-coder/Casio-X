package com.example.engine

import com.example.model.StatOneVarResult
import com.example.model.StatTwoVarResult
import kotlin.math.sqrt

object StatisticsEngine {

  fun calculateOneVar(dataList: List<Pair<Double, Double>>): StatOneVarResult? {
    if (dataList.isEmpty()) return null

    var totalN = 0.0
    var sumX = 0.0
    var sumX2 = 0.0
    val expanded = mutableListOf<Double>()

    for ((x, freq) in dataList) {
      val f = if (freq <= 0.0) 1.0 else freq
      totalN += f
      sumX += x * f
      sumX2 += x * x * f
      // Store expanded for percentile calculations
      val countInt = f.toInt().coerceAtLeast(1)
      repeat(countInt) {
        expanded.add(x)
      }
    }

    if (totalN < 1) return null

    val mean = sumX / totalN
    val popVar = (sumX2 - (sumX * sumX) / totalN) / totalN
    val popStdDev = sqrt(popVar.coerceAtLeast(0.0))

    val sampleVar = if (totalN > 1) {
      (sumX2 - (sumX * sumX) / totalN) / (totalN - 1)
    } else 0.0
    val sampleStdDev = sqrt(sampleVar.coerceAtLeast(0.0))

    expanded.sort()
    val minVal = expanded.firstOrNull() ?: 0.0
    val maxVal = expanded.lastOrNull() ?: 0.0
    val medianVal = getPercentile(expanded, 0.5)
    val q1Val = getPercentile(expanded, 0.25)
    val q3Val = getPercentile(expanded, 0.75)

    return StatOneVarResult(
      count = totalN.toInt(),
      mean = mean,
      sumX = sumX,
      sumX2 = sumX2,
      sampleStdDev = sampleStdDev,
      popStdDev = popStdDev,
      variance = sampleVar,
      min = minVal,
      q1 = q1Val,
      median = medianVal,
      q3 = q3Val,
      max = maxVal
    )
  }

  fun calculateTwoVar(dataList: List<Pair<Double, Double>>): StatTwoVarResult? {
    if (dataList.size < 2) return null

    val n = dataList.size.toDouble()
    var sumX = 0.0
    var sumY = 0.0
    var sumX2 = 0.0
    var sumY2 = 0.0
    var sumXY = 0.0

    for ((x, y) in dataList) {
      sumX += x
      sumY += y
      sumX2 += x * x
      sumY2 += y * y
      sumXY += x * y
    }

    val denomB = n * sumX2 - sumX * sumX
    if (kotlin.math.abs(denomB) < 1e-12) return null

    val slopeB = (n * sumXY - sumX * sumY) / denomB
    val meanX = sumX / n
    val meanY = sumY / n
    val interceptA = meanY - slopeB * meanX

    val denomR = sqrt((denomB * (n * sumY2 - sumY * sumY)).coerceAtLeast(0.0))
    val r = if (denomR > 1e-12) (n * sumXY - sumX * sumY) / denomR else 0.0
    val r2 = r * r
    val cov = (sumXY - (sumX * sumY) / n) / (n - 1)

    return StatTwoVarResult(
      count = dataList.size,
      meanX = meanX,
      meanY = meanY,
      slopeA = slopeB, // slope
      interceptB = interceptA, // intercept
      correlationR = r,
      rSquared = r2,
      covXY = cov
    )
  }

  private fun getPercentile(sortedList: List<Double>, p: Double): Double {
    if (sortedList.isEmpty()) return 0.0
    if (sortedList.size == 1) return sortedList[0]

    val index = p * (sortedList.size - 1)
    val lower = index.toInt()
    val upper = (lower + 1).coerceAtMost(sortedList.size - 1)
    val fraction = index - lower
    return sortedList[lower] + fraction * (sortedList[upper] - sortedList[lower])
  }
}
