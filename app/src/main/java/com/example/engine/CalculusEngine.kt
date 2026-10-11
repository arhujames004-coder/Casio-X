package com.example.engine

import com.example.model.AngleUnit
import kotlin.math.abs

object CalculusEngine {

  fun definiteIntegral(
    expr: String,
    a: Double,
    b: Double,
    intervals: Int = 1000,
    angleUnit: AngleUnit = AngleUnit.RAD
  ): Double {
    if (a == b) return 0.0
    val n = if (intervals % 2 != 0) intervals + 1 else intervals
    val h = (b - a) / n

    fun eval(x: Double): Double {
      val res = MathEvaluator.evaluate(
        expression = expr,
        angleUnit = angleUnit,
        variables = mapOf('X' to x, 'x' to x)
      )
      return if (res.decimalValue.isNaN()) 0.0 else res.decimalValue
    }

    var sum = eval(a) + eval(b)

    for (i in 1 until n) {
      val x = a + i * h
      val fx = eval(x)
      sum += if (i % 2 == 1) 4.0 * fx else 2.0 * fx
    }

    return (h / 3.0) * sum
  }

  fun derivative(
    expr: String,
    x: Double,
    h: Double = 1e-5,
    angleUnit: AngleUnit = AngleUnit.RAD
  ): Double {
    fun eval(valX: Double): Double {
      val res = MathEvaluator.evaluate(
        expression = expr,
        angleUnit = angleUnit,
        variables = mapOf('X' to valX, 'x' to valX)
      )
      return res.decimalValue
    }

    // 5-point central difference stencil
    val fxh2 = eval(x + 2 * h)
    val fxh1 = eval(x + h)
    val fxmh1 = eval(x - h)
    val fxmh2 = eval(x - 2 * h)

    return (-fxh2 + 8 * fxh1 - 8 * fxmh1 + fxmh2) / (12 * h)
  }

  fun summation(
    expr: String,
    start: Long,
    end: Long,
    angleUnit: AngleUnit = AngleUnit.RAD
  ): Double {
    if (start > end) return 0.0
    var sum = 0.0
    for (i in start..end) {
      val res = MathEvaluator.evaluate(
        expression = expr,
        angleUnit = angleUnit,
        variables = mapOf('X' to i.toDouble(), 'x' to i.toDouble())
      )
      sum += if (res.decimalValue.isNaN()) 0.0 else res.decimalValue
    }
    return sum
  }
}
