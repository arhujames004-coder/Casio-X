package com.example.model

enum class CalculatorMode(val label: String, val title: String, val description: String) {
  COMP("COMP", "Scientific Compute", "Basic & Advanced Natural V.P.A.M. Calculations"),
  CMPLX("CMPLX", "Complex Numbers", "Rectangular (a+bi) & Polar (r∠θ) Calculations"),
  GRAPH("GRAPH", "Function Grapher", "Interactive 2D Function Plotter & Roots"),
  MATRIX("MATRIX", "Matrix Algebra", "Operations on MatA, MatB, MatC (up to 4x4)"),
  VECTOR("VECTOR", "Vector Algebra", "Dot & Cross Product, 2D/3D Vectors"),
  EQN("EQN", "Equation Solver", "Simultaneous Linear, Quadratic & Cubic Equations"),
  STAT("STAT", "Statistics", "1-Var Stats & 2-Var Linear Regression Analysis"),
  BASE_N("BASE-N", "Base-N & Logic", "DEC, HEX, BIN, OCT & Bitwise Operations"),
  CONV("CONV", "Unit Converter", "Physical Unit Conversion & Constants Table")
}

enum class AngleUnit(val label: String, val badge: String) {
  DEG("Degree", "D"),
  RAD("Radian", "R"),
  GRA("Gradian", "G")
}

enum class ScreenDisplayTheme(val displayName: String) {
  OLED_DARK("OLED Dark"),
  LCD_GREEN("Classic LCD"),
  RETRO_AMBER("Retro Amber"),
  CYBER_NAVY("Cyber Navy")
}

data class CalculationHistory(
  val id: Long = System.currentTimeMillis(),
  val expression: String,
  val result: String,
  val angleUnit: AngleUnit = AngleUnit.DEG,
  val mode: CalculatorMode = CalculatorMode.COMP,
  val timestamp: Long = System.currentTimeMillis()
)

data class MatrixModel(
  val name: String,
  val rows: Int,
  val cols: Int,
  val elements: List<Double>
) {
  operator fun get(row: Int, col: Int): Double {
    val index = row * cols + col
    return if (index in elements.indices) elements[index] else 0.0
  }
}

data class VectorModel(
  val name: String,
  val dimension: Int = 3, // 2 or 3
  val x: Double = 0.0,
  val y: Double = 0.0,
  val z: Double = 0.0
) {
  val magnitude: Double
    get() = kotlin.math.sqrt(x * x + y * y + (if (dimension == 3) z * z else 0.0))

  fun formatString(): String {
    return if (dimension == 2) {
      "[${format(x)}, ${format(y)}]"
    } else {
      "[${format(x)}, ${format(y)}, ${format(z)}]"
    }
  }

  companion object {
    private fun format(v: Double): String {
      if (kotlin.math.abs(v - kotlin.math.round(v)) < 1e-9) return kotlin.math.round(v).toLong().toString()
      return String.format(java.util.Locale.US, "%.3f", v).trimEnd('0').trimEnd('.')
    }
  }
}

enum class BaseNNumberSystem(val label: String, val radix: Int, val prefix: String) {
  DEC("DEC", 10, "d"),
  HEX("HEX", 16, "h"),
  BIN("BIN", 2, "b"),
  OCT("OCT", 8, "o")
}

data class ComplexNumber(
  val real: Double,
  val imag: Double
) {
  val magnitude: Double
    get() = kotlin.math.hypot(real, imag)

  val argumentRad: Double
    get() = kotlin.math.atan2(imag, real)

  fun argumentDeg(): Double = Math.toDegrees(argumentRad)

  fun toRectangularString(precision: Int = 6): String {
    val rStr = formatNum(real, precision)
    val iAbs = kotlin.math.abs(imag)
    val iStr = formatNum(iAbs, precision)
    return when {
      kotlin.math.abs(imag) < 1e-12 -> rStr
      kotlin.math.abs(real) < 1e-12 -> if (imag < 0) "-${iStr}i" else "${iStr}i"
      imag < 0 -> "$rStr - ${iStr}i"
      else -> "$rStr + ${iStr}i"
    }
  }

  fun toPolarString(isDeg: Boolean = true, precision: Int = 4): String {
    val rStr = formatNum(magnitude, precision)
    val angle = if (isDeg) argumentDeg() else argumentRad
    val angleStr = formatNum(angle, precision)
    val symbol = if (isDeg) "°" else " rad"
    return "$rStr ∠ $angleStr$symbol"
  }

  operator fun plus(other: ComplexNumber) = ComplexNumber(real + other.real, imag + other.imag)
  operator fun minus(other: ComplexNumber) = ComplexNumber(real - other.real, imag - other.imag)
  operator fun times(other: ComplexNumber) = ComplexNumber(
    real * other.real - imag * other.imag,
    real * other.imag + imag * other.real
  )
  operator fun div(other: ComplexNumber): ComplexNumber {
    val denom = other.real * other.real + other.imag * other.imag
    if (denom == 0.0) throw ArithmeticException("Division by zero in complex numbers")
    return ComplexNumber(
      (real * other.real + imag * other.imag) / denom,
      (imag * other.real - real * other.imag) / denom
    )
  }

  fun conjugate() = ComplexNumber(real, -imag)

  companion object {
    val ZERO = ComplexNumber(0.0, 0.0)
    val ONE = ComplexNumber(1.0, 0.0)
    val I = ComplexNumber(0.0, 1.0)

    fun formatNum(num: Double, precision: Int): String {
      if (num.isNaN()) return "NaN"
      if (num.isInfinite()) return if (num > 0) "∞" else "-∞"
      if (kotlin.math.abs(num - num.toLong()) < 1e-9) {
        return num.toLong().toString()
      }
      val formatted = String.format(java.util.Locale.US, "%.${precision}f", num)
      return formatted.trimEnd('0').trimEnd('.')
    }
  }
}

data class StatOneVarResult(
  val count: Int,
  val mean: Double,
  val sumX: Double,
  val sumX2: Double,
  val sampleStdDev: Double,
  val popStdDev: Double,
  val variance: Double,
  val min: Double,
  val q1: Double,
  val median: Double,
  val q3: Double,
  val max: Double
)

data class StatTwoVarResult(
  val count: Int,
  val meanX: Double,
  val meanY: Double,
  val slopeA: Double,     // y = A + Bx or y = a + bx
  val interceptB: Double,
  val correlationR: Double,
  val rSquared: Double,
  val covXY: Double
)

enum class EquationType(val title: String, val format: String) {
  SIMULTANEOUS_2("Simultaneous 2 Unknowns", "a₁x + b₁y = c₁\na₂x + b₂y = c₂"),
  SIMULTANEOUS_3("Simultaneous 3 Unknowns", "a₁x + b₁y + c₁z = d₁\na₂x + b₂y + c₂z = d₂\na₃x + b₃y + c₃z = d₃"),
  QUADRATIC("Quadratic Equation", "ax² + bx + c = 0"),
  CUBIC("Cubic Equation", "ax³ + bx² + cx + d = 0"),
  NEWTON_SOLVER("f(x) = 0 Root Finder", "Newton-Raphson Numerical Root")
}

data class ScientificConstant(
  val symbol: String,
  val name: String,
  val value: Double,
  val unit: String,
  val valueString: String,
  val category: String
)
