package com.example.engine

import com.example.model.AngleUnit
import com.example.model.ComplexNumber
import kotlin.math.*

object EquationSolverEngine {

  data class Simultaneous2Result(
    val x: Double,
    val y: Double,
    val isInfiniteSolutions: Boolean = false,
    val isNoSolution: Boolean = false
  )

  data class Simultaneous3Result(
    val x: Double,
    val y: Double,
    val z: Double,
    val isSolvable: Boolean = true
  )

  data class QuadraticResult(
    val discriminant: Double,
    val root1: ComplexNumber,
    val root2: ComplexNumber,
    val vertexX: Double,
    val vertexY: Double,
    val isVertexMin: Boolean
  )

  data class CubicResult(
    val root1: ComplexNumber,
    val root2: ComplexNumber,
    val root3: ComplexNumber
  )

  data class NewtonResult(
    val root: Double,
    val iterations: Int,
    val converged: Boolean,
    val fxAtRoot: Double
  )

  fun solveSimultaneous2(
    a1: Double, b1: Double, c1: Double,
    a2: Double, b2: Double, c2: Double
  ): Simultaneous2Result {
    val d = a1 * b2 - a2 * b1
    val dx = c1 * b2 - c2 * b1
    val dy = a1 * c2 - a2 * c1

    return when {
      abs(d) < 1e-12 -> {
        if (abs(dx) < 1e-12 && abs(dy) < 1e-12) {
          Simultaneous2Result(0.0, 0.0, isInfiniteSolutions = true)
        } else {
          Simultaneous2Result(0.0, 0.0, isNoSolution = true)
        }
      }
      else -> Simultaneous2Result(dx / d, dy / d)
    }
  }

  fun solveSimultaneous3(
    a1: Double, b1: Double, c1: Double, d1: Double,
    a2: Double, b2: Double, c2: Double, d2: Double,
    a3: Double, b3: Double, c3: Double, d3: Double
  ): Simultaneous3Result {
    val det = det3(a1, b1, c1, a2, b2, c2, a3, b3, c3)
    if (abs(det) < 1e-12) {
      return Simultaneous3Result(0.0, 0.0, 0.0, isSolvable = false)
    }
    val detX = det3(d1, b1, c1, d2, b2, c2, d3, b3, c3)
    val detY = det3(a1, d1, c1, a2, d2, c2, a3, d3, c3)
    val detZ = det3(a1, b1, d1, a2, b2, d2, a3, b3, d3)
    return Simultaneous3Result(detX / det, detY / det, detZ / det, isSolvable = true)
  }

  private fun det3(
    a: Double, b: Double, c: Double,
    d: Double, e: Double, f: Double,
    g: Double, h: Double, i: Double
  ): Double {
    return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g)
  }

  fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticResult {
    if (abs(a) < 1e-12) {
      // Linear bx + c = 0 -> x = -c/b
      val root = if (abs(b) < 1e-12) Double.NaN else -c / b
      val r = ComplexNumber(root, 0.0)
      return QuadraticResult(0.0, r, r, root, 0.0, true)
    }

    val delta = b * b - 4 * a * c
    val vertexX = -b / (2 * a)
    val vertexY = c - (b * b) / (4 * a)
    val isMin = a > 0

    val (r1, r2) = when {
      delta > 0 -> {
        val sqrtD = sqrt(delta)
        val x1 = (-b + sqrtD) / (2 * a)
        val x2 = (-b - sqrtD) / (2 * a)
        Pair(ComplexNumber(x1, 0.0), ComplexNumber(x2, 0.0))
      }
      abs(delta) < 1e-12 -> {
        val x = -b / (2 * a)
        Pair(ComplexNumber(x, 0.0), ComplexNumber(x, 0.0))
      }
      else -> {
        val realPart = -b / (2 * a)
        val imagPart = sqrt(-delta) / (2 * a)
        Pair(
          ComplexNumber(realPart, abs(imagPart)),
          ComplexNumber(realPart, -abs(imagPart))
        )
      }
    }

    return QuadraticResult(delta, r1, r2, vertexX, vertexY, isMin)
  }

  fun solveCubic(a: Double, b: Double, c: Double, d: Double): CubicResult {
    if (abs(a) < 1e-12) {
      val q = solveQuadratic(b, c, d)
      return CubicResult(q.root1, q.root2, ComplexNumber.ZERO)
    }

    // Normalized cubic: x³ + B x² + C x + D = 0
    val B = b / a
    val C = c / a
    val D = d / a

    // Depressed cubic: t³ + pt + q = 0, where x = t - B/3
    val p = C - (B * B) / 3.0
    val q = (2.0 * B * B * B) / 27.0 - (B * C) / 3.0 + D
    val delta = (q * q) / 4.0 + (p * p * p) / 27.0

    val shift = -B / 3.0

    if (delta > 0) {
      val sqrtDelta = sqrt(delta)
      val u = cbrt(-q / 2.0 + sqrtDelta)
      val v = cbrt(-q / 2.0 - sqrtDelta)
      val t1 = u + v
      val real23 = -(u + v) / 2.0
      val imag23 = (u - v) * sqrt(3.0) / 2.0

      return CubicResult(
        ComplexNumber(t1 + shift, 0.0),
        ComplexNumber(real23 + shift, abs(imag23)),
        ComplexNumber(real23 + shift, -abs(imag23))
      )
    } else if (abs(delta) < 1e-12) {
      val u = cbrt(-q / 2.0)
      return CubicResult(
        ComplexNumber(2 * u + shift, 0.0),
        ComplexNumber(-u + shift, 0.0),
        ComplexNumber(-u + shift, 0.0)
      )
    } else {
      // 3 real roots (casus irreducibilis)
      val r = sqrt(-p * p * p / 27.0)
      val phi = acos(-q / (2.0 * r))
      val t1 = 2.0 * cbrt(r) * cos(phi / 3.0)
      val t2 = 2.0 * cbrt(r) * cos((phi + 2 * Math.PI) / 3.0)
      val t3 = 2.0 * cbrt(r) * cos((phi + 4 * Math.PI) / 3.0)

      return CubicResult(
        ComplexNumber(t1 + shift, 0.0),
        ComplexNumber(t2 + shift, 0.0),
        ComplexNumber(t3 + shift, 0.0)
      )
    }
  }

  fun solveNewton(
    expr: String,
    initialGuess: Double = 1.0,
    maxIter: Int = 100,
    tolerance: Double = 1e-7
  ): NewtonResult {
    var x = initialGuess
    val h = 1e-5

    fun eval(valX: Double): Double {
      val res = MathEvaluator.evaluate(
        expr,
        angleUnit = AngleUnit.RAD,
        variables = mapOf('X' to valX, 'x' to valX)
      )
      return res.decimalValue
    }

    for (iter in 0 until maxIter) {
      val fx = eval(x)
      if (fx.isNaN() || fx.isInfinite()) break
      if (abs(fx) < tolerance) {
        return NewtonResult(x, iter, converged = true, fxAtRoot = fx)
      }

      val fxh1 = eval(x + h)
      val fxh2 = eval(x - h)
      val dfx = (fxh1 - fxh2) / (2 * h)

      if (abs(dfx) < 1e-12) {
        // Perturb slightly if slope is zero
        x += 0.1
        continue
      }

      val nextX = x - fx / dfx
      if (abs(nextX - x) < tolerance) {
        return NewtonResult(nextX, iter + 1, converged = true, fxAtRoot = eval(nextX))
      }
      x = nextX
    }

    return NewtonResult(x, maxIter, converged = false, fxAtRoot = eval(x))
  }
}
