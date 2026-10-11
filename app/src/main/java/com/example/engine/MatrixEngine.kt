package com.example.engine

import com.example.model.MatrixModel
import kotlin.math.abs

object MatrixEngine {

  fun createZero(name: String, rows: Int, cols: Int): MatrixModel {
    return MatrixModel(name, rows, cols, List(rows * cols) { 0.0 })
  }

  fun createIdentity(name: String, size: Int): MatrixModel {
    val list = MutableList(size * size) { 0.0 }
    for (i in 0 until size) {
      list[i * size + i] = 1.0
    }
    return MatrixModel(name, size, size, list)
  }

  fun add(a: MatrixModel, b: MatrixModel, resultName: String = "MatAns"): Result<MatrixModel> {
    if (a.rows != b.rows || a.cols != b.cols) {
      return Result.failure(IllegalArgumentException("Dimension Mismatch: Both matrices must have same dimensions"))
    }
    val res = a.elements.zip(b.elements) { x, y -> x + y }
    return Result.success(MatrixModel(resultName, a.rows, a.cols, res))
  }

  fun subtract(a: MatrixModel, b: MatrixModel, resultName: String = "MatAns"): Result<MatrixModel> {
    if (a.rows != b.rows || a.cols != b.cols) {
      return Result.failure(IllegalArgumentException("Dimension Mismatch: Both matrices must have same dimensions"))
    }
    val res = a.elements.zip(b.elements) { x, y -> x - y }
    return Result.success(MatrixModel(resultName, a.rows, a.cols, res))
  }

  fun multiply(a: MatrixModel, b: MatrixModel, resultName: String = "MatAns"): Result<MatrixModel> {
    if (a.cols != b.rows) {
      return Result.failure(IllegalArgumentException("Dimension Mismatch: Columns of first (${a.cols}) must match Rows of second (${b.rows})"))
    }
    val rows = a.rows
    val cols = b.cols
    val common = a.cols
    val res = MutableList(rows * cols) { 0.0 }

    for (r in 0 until rows) {
      for (c in 0 until cols) {
        var sum = 0.0
        for (k in 0 until common) {
          sum += a[r, k] * b[k, c]
        }
        res[r * cols + c] = sum
      }
    }
    return Result.success(MatrixModel(resultName, rows, cols, res))
  }

  fun scalarMultiply(a: MatrixModel, scalar: Double, resultName: String = "MatAns"): MatrixModel {
    val res = a.elements.map { it * scalar }
    return MatrixModel(resultName, a.rows, a.cols, res)
  }

  fun transpose(a: MatrixModel, resultName: String = "MatAns"): MatrixModel {
    val res = MutableList(a.rows * a.cols) { 0.0 }
    for (r in 0 until a.rows) {
      for (c in 0 until a.cols) {
        res[c * a.rows + r] = a[r, c]
      }
    }
    return MatrixModel(resultName, a.cols, a.rows, res)
  }

  fun trace(a: MatrixModel): Result<Double> {
    if (a.rows != a.cols) {
      return Result.failure(IllegalArgumentException("Trace is only defined for square matrices"))
    }
    var sum = 0.0
    for (i in 0 until a.rows) {
      sum += a[i, i]
    }
    return Result.success(sum)
  }

  fun determinant(a: MatrixModel): Result<Double> {
    if (a.rows != a.cols) {
      return Result.failure(IllegalArgumentException("Determinant is only defined for square matrices"))
    }
    val det = computeDet(toArray(a))
    return Result.success(det)
  }

  fun inverse(a: MatrixModel, resultName: String = "MatAns"): Result<MatrixModel> {
    if (a.rows != a.cols) {
      return Result.failure(IllegalArgumentException("Inverse only exists for square matrices"))
    }
    val n = a.rows
    val detRes = determinant(a)
    val det = detRes.getOrNull() ?: return Result.failure(IllegalArgumentException("Cannot compute determinant"))
    if (abs(det) < 1e-12) {
      return Result.failure(ArithmeticException("Matrix is Singular (Determinant = 0), Inverse does not exist"))
    }

    val mat = toArray(a)
    val adj = Array(n) { DoubleArray(n) }

    for (r in 0 until n) {
      for (c in 0 until n) {
        val minor = getMinor(mat, r, c)
        val sign = if ((r + c) % 2 == 0) 1.0 else -1.0
        // Transposed for adjugate: adj[c][r]
        adj[c][r] = sign * computeDet(minor)
      }
    }

    val res = MutableList(n * n) { 0.0 }
    for (r in 0 until n) {
      for (c in 0 until n) {
        res[r * n + c] = adj[r][c] / det
      }
    }

    return Result.success(MatrixModel(resultName, n, n, res))
  }

  private fun toArray(a: MatrixModel): Array<DoubleArray> {
    val arr = Array(a.rows) { DoubleArray(a.cols) }
    for (r in 0 until a.rows) {
      for (c in 0 until a.cols) {
        arr[r][c] = a[r, c]
      }
    }
    return arr
  }

  private fun computeDet(mat: Array<DoubleArray>): Double {
    val n = mat.size
    if (n == 1) return mat[0][0]
    if (n == 2) return mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0]
    if (n == 3) {
      return mat[0][0] * (mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1]) -
        mat[0][1] * (mat[1][0] * mat[2][2] - mat[1][2] * mat[2][0]) +
        mat[0][2] * (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0])
    }

    // Laplace expansion along row 0
    var det = 0.0
    for (c in 0 until n) {
      val minor = getMinor(mat, 0, c)
      val sign = if (c % 2 == 0) 1.0 else -1.0
      det += sign * mat[0][c] * computeDet(minor)
    }
    return det
  }

  private fun getMinor(mat: Array<DoubleArray>, skipR: Int, skipC: Int): Array<DoubleArray> {
    val n = mat.size
    val sub = Array(n - 1) { DoubleArray(n - 1) }
    var rSub = 0
    for (r in 0 until n) {
      if (r == skipR) continue
      var cSub = 0
      for (c in 0 until n) {
        if (c == skipC) continue
        sub[rSub][cSub] = mat[r][c]
        cSub++
      }
      rSub++
    }
    return sub
  }
}
