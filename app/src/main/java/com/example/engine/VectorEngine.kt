package com.example.engine

import com.example.model.AngleUnit
import com.example.model.VectorModel
import kotlin.math.acos
import kotlin.math.sqrt

object VectorEngine {

  fun dotProduct(u: VectorModel, v: VectorModel): Double {
    return u.x * v.x + u.y * v.y + (if (u.dimension == 3 && v.dimension == 3) u.z * v.z else 0.0)
  }

  fun crossProduct(u: VectorModel, v: VectorModel, resultName: String = "VctAns"): Result<VectorModel> {
    if (u.dimension != 3 || v.dimension != 3) {
      return Result.failure(IllegalArgumentException("Cross product requires 3-dimensional vectors"))
    }
    val rx = u.y * v.z - u.z * v.y
    val ry = u.z * v.x - u.x * v.z
    val rz = u.x * v.y - u.y * v.x
    return Result.success(VectorModel(resultName, 3, rx, ry, rz))
  }

  fun unitVector(u: VectorModel, resultName: String = "VctAns"): Result<VectorModel> {
    val mag = u.magnitude
    if (mag < 1e-12) {
      return Result.failure(ArithmeticException("Zero vector has no unit vector direction"))
    }
    return Result.success(
      VectorModel(
        name = resultName,
        dimension = u.dimension,
        x = u.x / mag,
        y = u.y / mag,
        z = if (u.dimension == 3) u.z / mag else 0.0
      )
    )
  }

  fun angleBetween(u: VectorModel, v: VectorModel, angleUnit: AngleUnit = AngleUnit.DEG): Result<Double> {
    val magU = u.magnitude
    val magV = v.magnitude
    if (magU < 1e-12 || magV < 1e-12) {
      return Result.failure(ArithmeticException("Cannot calculate angle with a zero vector"))
    }
    val dot = dotProduct(u, v)
    val cosTheta = (dot / (magU * magV)).coerceIn(-1.0, 1.0)
    val rad = acos(cosTheta)
    val angle = when (angleUnit) {
      AngleUnit.DEG -> Math.toDegrees(rad)
      AngleUnit.RAD -> rad
      AngleUnit.GRA -> rad * 200.0 / Math.PI
    }
    return Result.success(angle)
  }

  fun add(u: VectorModel, v: VectorModel, resultName: String = "VctAns"): Result<VectorModel> {
    if (u.dimension != v.dimension) {
      return Result.failure(IllegalArgumentException("Dimension mismatch: Both vectors must be ${u.dimension}D"))
    }
    return Result.success(
      VectorModel(
        resultName,
        u.dimension,
        u.x + v.x,
        u.y + v.y,
        if (u.dimension == 3) u.z + v.z else 0.0
      )
    )
  }

  fun subtract(u: VectorModel, v: VectorModel, resultName: String = "VctAns"): Result<VectorModel> {
    if (u.dimension != v.dimension) {
      return Result.failure(IllegalArgumentException("Dimension mismatch: Both vectors must be ${u.dimension}D"))
    }
    return Result.success(
      VectorModel(
        resultName,
        u.dimension,
        u.x - v.x,
        u.y - v.y,
        if (u.dimension == 3) u.z - v.z else 0.0
      )
    )
  }

  fun scalarMultiply(u: VectorModel, scalar: Double, resultName: String = "VctAns"): VectorModel {
    return VectorModel(
      resultName,
      u.dimension,
      u.x * scalar,
      u.y * scalar,
      if (u.dimension == 3) u.z * scalar else 0.0
    )
  }
}
