package com.example.engine

import com.example.model.AngleUnit
import java.util.Locale
import kotlin.math.*

object MathEvaluator {

  const val PI_VALUE = Math.PI
  const val E_VALUE = Math.E

  data class EvaluationResult(
    val decimalValue: Double,
    val formattedDecimal: String,
    val fractionRepresentation: String?,
    val scientificNotation: String,
    val engineeringNotation: String,
    val isError: Boolean = false,
    val errorMessage: String? = null
  )

  /**
   * Main evaluate entry point
   */
  fun evaluate(
    expression: String,
    angleUnit: AngleUnit = AngleUnit.DEG,
    ansValue: Double = 0.0,
    variables: Map<Char, Double> = emptyMap()
  ): EvaluationResult {
    val clean = preprocessExpression(expression, ansValue, variables)
    if (clean.isBlank()) {
      return EvaluationResult(0.0, "0", "0", "0", "0")
    }

    return try {
      val parser = Parser(clean, angleUnit)
      val value = parser.parseExpression()
      if (value.isNaN()) {
        EvaluationResult(Double.NaN, "Math ERROR", null, "Math ERROR", "Math ERROR", isError = true, errorMessage = "Math ERROR: Undefined")
      } else if (value.isInfinite()) {
        EvaluationResult(value, "Math ERROR", null, "Math ERROR", "Math ERROR", isError = true, errorMessage = "Math ERROR: Division by Zero / Overflow")
      } else {
        val formatted = formatDecimal(value)
        val fraction = toFraction(value)
        val sci = toScientific(value)
        val eng = toEngineering(value, 0)
        EvaluationResult(value, formatted, fraction, sci, eng)
      }
    } catch (e: Exception) {
      EvaluationResult(Double.NaN, "Syntax ERROR", null, "Syntax ERROR", "Syntax ERROR", isError = true, errorMessage = e.message ?: "Syntax ERROR")
    }
  }

  fun preprocessExpression(
    expr: String,
    ansValue: Double,
    variables: Map<Char, Double>
  ): String {
    var s = expr
      .replace("×10^", "*10^")
      .replace("×10ˣ", "*10^")
      .replace("×", "*")
      .replace("÷", "/")
      .replace("−", "-")
      .replace("–", "-")
      .replace("(-)", "-")
      .replace("π", "PI")
      .replace("Ans", "($ansValue)")
      .replace("ans", "($ansValue)")
      .replace("√", "sqrt")
      .replace("∛", "cbrt")
      .replace("sin⁻¹", "asin")
      .replace("cos⁻¹", "acos")
      .replace("tan⁻¹", "atan")
      .replace("sinh⁻¹", "asinh")
      .replace("cosh⁻¹", "acosh")
      .replace("tanh⁻¹", "atanh")
      .replace("²", "^2")
      .replace("³", "^3")
      .replace("⁻¹", "^(-1)")
      .replace("°", "")
      .replace("Ran#", "${(Math.random() * 1000).toInt() / 1000.0}")

    // Replace variable symbols A..F, X, Y, M only when standalone
    variables.forEach { (ch, value) ->
      s = s.replace(Regex("(?<![a-zA-Z])$ch(?![a-zA-Z])"), "($value)")
    }

    // Auto-close missing parentheses like a real Casio calculator
    val openCount = s.count { it == '(' }
    val closeCount = s.count { it == ')' }
    if (openCount > closeCount) {
      s += ")".repeat(openCount - closeCount)
    }

    return s.trim()
  }

  fun formatDecimal(value: Double, maxDecimals: Int = 10): String {
    if (abs(value - round(value)) < 1e-11) {
      val l = round(value).toLong()
      if (abs(value) < 1e14) return l.toString()
    }
    if (abs(value) >= 1e12 || (abs(value) > 0 && abs(value) < 1e-6)) {
      return toScientific(value)
    }
    val s = String.format(Locale.US, "%.${maxDecimals}f", value)
    return s.trimEnd('0').trimEnd('.')
  }

  fun toScientific(value: Double): String {
    if (value == 0.0) return "0"
    val exp = floor(log10(abs(value))).toInt()
    val mantissa = value / 10.0.pow(exp.toDouble())
    val mStr = String.format(Locale.US, "%.6f", mantissa).trimEnd('0').trimEnd('.')
    return "$mStr×10^$exp"
  }

  fun toEngineering(value: Double, shiftSteps: Int = 0): String {
    if (value == 0.0) return "0"
    var exp = floor(log10(abs(value))).toInt()
    var engExp = exp - (exp % 3)
    if (exp < 0 && exp % 3 != 0) {
      engExp -= 3
    }
    engExp += (shiftSteps * 3)
    val mantissa = value / 10.0.pow(engExp.toDouble())
    val mStr = String.format(Locale.US, "%.4f", mantissa).trimEnd('0').trimEnd('.')
    return "$mStr×10^$engExp"
  }

  /**
   * Converts a double to an exact or best-fit fraction representation (e.g. 3/4 or 2 1/3)
   */
  fun toFraction(value: Double, maxDenominator: Long = 10000): String? {
    if (value.isNaN() || value.isInfinite() || abs(value) > 1e7) return null
    if (abs(value - round(value)) < 1e-9) return null // Already an integer

    val isNegative = value < 0
    val x = abs(value)

    var h1: Long = 1; var h2: Long = 0
    var k1: Long = 0; var k2: Long = 1
    var b = x

    do {
      val a = b.toLong()
      var aux = h1
      h1 = a * h1 + h2
      h2 = aux
      aux = k1
      k1 = a * k1 + k2
      k2 = aux
      b = 1.0 / (b - a.toDouble())
    } while (abs(x - h1.toDouble() / k1.toDouble()) > x * 1e-8 && k1 <= maxDenominator && !b.isNaN() && !b.isInfinite())

    if (k1 <= maxDenominator && abs(x - h1.toDouble() / k1.toDouble()) < 1e-7 && k1 > 1) {
      val sign = if (isNegative) "-" else ""
      return if (h1 > k1) {
        val whole = h1 / k1
        val rem = h1 % k1
        if (rem == 0L) "$sign$whole" else "$sign$h1/$k1 ($whole $rem/$k1)"
      } else {
        "$sign$h1/$k1"
      }
    }
    return null
  }

  // Recursive Descent Parser for robust mathematical grammar
  private class Parser(private val input: String, private val angleUnit: AngleUnit) {
    private var pos = -1
    private var ch = ' '

    private fun nextChar() {
      pos++
      ch = if (pos < input.length) input[pos] else '\u0000'
    }

    private fun eat(charToEat: Char): Boolean {
      while (ch == ' ') nextChar()
      if (ch == charToEat) {
        nextChar()
        return true
      }
      return false
    }

    fun parseExpression(): Double {
      nextChar()
      val x = parseAddSub()
      while (ch == ' ') nextChar()
      if (pos < input.length) {
        throw IllegalArgumentException("Unexpected: '$ch'")
      }
      return x
    }

    private fun parseAddSub(): Double {
      var x = parseMulDiv()
      while (true) {
        when {
          eat('+') -> x += parseMulDiv()
          eat('-') -> x -= parseMulDiv()
          else -> return x
        }
      }
    }

    private fun parseMulDiv(): Double {
      var x = parseFactorialAndPower()
      while (true) {
        when {
          eat('*') -> x *= parseFactorialAndPower()
          eat('/') -> {
            val d = parseFactorialAndPower()
            if (d == 0.0) throw ArithmeticException("Division by Zero")
            x /= d
          }
          eat('%') -> {
            // Modulo or percentage
            val d = parseFactorialAndPower()
            x %= d
          }
          else -> return x
        }
      }
    }

    private fun parseFactorialAndPower(): Double {
      var x = parseFactor()
      while (true) {
        when {
          eat('^') -> {
            val exp = parseFactor()
            x = x.pow(exp)
          }
          eat('!') -> {
            x = factorial(x)
          }
          else -> return x
        }
      }
    }

    private fun parseFactor(): Double {
      while (ch == ' ') nextChar()

      if (eat('+')) return +parseFactor()
      if (eat('-')) return -parseFactor()

      var x: Double
      val startPos = pos

      if (eat('(')) {
        x = parseAddSub()
        eat(')')
      } else if ((ch in '0'..'9') || ch == '.') {
        while ((ch in '0'..'9') || ch == '.') nextChar()
        // Check for scientific notation like 1e5 or 2.5E-3
        if (ch == 'e' || ch == 'E') {
          nextChar()
          if (ch == '+' || ch == '-') nextChar()
          while (ch in '0'..'9') nextChar()
        }
        val numStr = input.substring(startPos, pos)
        x = numStr.toDouble()
      } else if (ch in 'a'..'z' || ch in 'A'..'Z') {
        while (ch in 'a'..'z' || ch in 'A'..'Z' || ch in '0'..'9' || ch == '_') nextChar()
        val func = input.substring(startPos, pos)

        if (func == "PI") {
          x = PI_VALUE
        } else if (func == "e" || func == "E") {
          x = E_VALUE
        } else if (eat('(')) {
          // Function with arguments e.g. sin(x) or log(x)
          val arg1 = parseAddSub()
          var arg2: Double? = null
          if (eat(',')) {
            arg2 = parseAddSub()
          }
          eat(')')
          x = applyFunction(func, arg1, arg2)
        } else {
          // Function applied without parentheses e.g. sqrt 9
          val arg = parseFactor()
          x = applyFunction(func, arg, null)
        }
      } else {
        throw IllegalArgumentException("Unexpected char: '$ch'")
      }

      // Check for implicit multiplication e.g. 2sin(30), (2)(3), or 2PI
      while (ch == ' ') nextChar()
      if (ch == '(' || ((ch in 'a'..'z' || ch in 'A'..'Z') && !input.startsWith("+", pos) && !input.startsWith("-", pos) && !input.startsWith("*", pos) && !input.startsWith("/", pos) && !input.startsWith("^", pos))) {
        x *= parseFactor()
      }

      return x
    }

    private fun applyFunction(func: String, arg: Double, arg2: Double?): Double {
      return when (func.lowercase(Locale.US)) {
        "sqrt" -> {
          if (arg < 0) throw ArithmeticException("Domain ERROR: √ of negative")
          sqrt(arg)
        }
        "cbrt" -> cbrt(arg)
        "sin" -> {
          if (angleUnit == AngleUnit.DEG) {
            val mod = ((arg % 360.0) + 360.0) % 360.0
            when {
              abs(mod) < 1e-9 || abs(mod - 180.0) < 1e-9 || abs(mod - 360.0) < 1e-9 -> 0.0
              abs(mod - 30.0) < 1e-9 || abs(mod - 150.0) < 1e-9 -> 0.5
              abs(mod - 210.0) < 1e-9 || abs(mod - 330.0) < 1e-9 -> -0.5
              abs(mod - 90.0) < 1e-9 -> 1.0
              abs(mod - 270.0) < 1e-9 -> -1.0
              else -> sin(toRad(arg))
            }
          } else sin(toRad(arg))
        }
        "cos" -> {
          if (angleUnit == AngleUnit.DEG) {
            val mod = ((arg % 360.0) + 360.0) % 360.0
            when {
              abs(mod - 90.0) < 1e-9 || abs(mod - 270.0) < 1e-9 -> 0.0
              abs(mod - 60.0) < 1e-9 || abs(mod - 300.0) < 1e-9 -> 0.5
              abs(mod - 120.0) < 1e-9 || abs(mod - 240.0) < 1e-9 -> -0.5
              abs(mod) < 1e-9 || abs(mod - 360.0) < 1e-9 -> 1.0
              abs(mod - 180.0) < 1e-9 -> -1.0
              else -> cos(toRad(arg))
            }
          } else cos(toRad(arg))
        }
        "tan" -> {
          if (angleUnit == AngleUnit.DEG) {
            val mod = ((arg % 180.0) + 180.0) % 180.0
            when {
              abs(mod) < 1e-9 || abs(mod - 180.0) < 1e-9 -> 0.0
              abs(mod - 45.0) < 1e-9 -> 1.0
              abs(mod - 135.0) < 1e-9 -> -1.0
              abs(mod - 90.0) < 1e-9 -> throw ArithmeticException("Math ERROR: tan asymptote")
              else -> tan(toRad(arg))
            }
          } else {
            val rad = toRad(arg)
            if (abs(cos(rad)) < 1e-14) throw ArithmeticException("Math ERROR: tan asymptote")
            tan(rad)
          }
        }
        "asin" -> {
          if (abs(arg) > 1.0) throw ArithmeticException("Domain ERROR: |x| > 1 for asin")
          fromRad(asin(arg))
        }
        "acos" -> {
          if (abs(arg) > 1.0) throw ArithmeticException("Domain ERROR: |x| > 1 for acos")
          fromRad(acos(arg))
        }
        "atan" -> fromRad(atan(arg))
        "sinh" -> sinh(arg)
        "cosh" -> cosh(arg)
        "tanh" -> tanh(arg)
        "asinh" -> asinh(arg)
        "acosh" -> {
          if (arg < 1.0) throw ArithmeticException("Domain ERROR: acosh for x < 1")
          acosh(arg)
        }
        "atanh" -> {
          if (abs(arg) >= 1.0) throw ArithmeticException("Domain ERROR: atanh for |x| >= 1")
          atanh(arg)
        }
        "log", "log10" -> {
          if (arg <= 0) throw ArithmeticException("Domain ERROR: log(x) <= 0")
          log10(arg)
        }
        "ln" -> {
          if (arg <= 0) throw ArithmeticException("Domain ERROR: ln(x) <= 0")
          ln(arg)
        }
        "logab" -> {
          val base = arg
          val value = arg2 ?: throw IllegalArgumentException("log_a(b) requires two arguments")
          if (base <= 0 || base == 1.0 || value <= 0) throw ArithmeticException("Domain ERROR")
          ln(value) / ln(base)
        }
        "abs" -> abs(arg)
        "exp" -> exp(arg)
        "floor" -> floor(arg)
        "ceil" -> ceil(arg)
        "round", "rnd" -> round(arg)
        "pol" -> {
          val y = arg2 ?: 0.0
          hypot(arg, y)
        }
        "rec" -> {
          val theta = arg2 ?: 0.0
          arg * cos(toRad(theta))
        }
        "npr" -> {
          val n = arg.toLong()
          val r = (arg2 ?: 0.0).toLong()
          nPr(n, r)
        }
        "ncr" -> {
          val n = arg.toLong()
          val r = (arg2 ?: 0.0).toLong()
          nCr(n, r)
        }
        else -> throw IllegalArgumentException("Unknown function: $func")
      }
    }

    private fun toRad(angle: Double): Double {
      return when (angleUnit) {
        AngleUnit.DEG -> Math.toRadians(angle)
        AngleUnit.RAD -> angle
        AngleUnit.GRA -> angle * Math.PI / 200.0
      }
    }

    private fun fromRad(rad: Double): Double {
      return when (angleUnit) {
        AngleUnit.DEG -> Math.toDegrees(rad)
        AngleUnit.RAD -> rad
        AngleUnit.GRA -> rad * 200.0 / Math.PI
      }
    }

    private fun factorial(n: Double): Double {
      if (n < 0 || n != floor(n)) throw ArithmeticException("Domain ERROR: Factorial requires non-negative integer")
      if (n > 170) return Double.POSITIVE_INFINITY
      var res = 1.0
      for (i in 2..n.toInt()) {
        res *= i
      }
      return res
    }

    private fun nPr(n: Long, r: Long): Double {
      if (n < 0 || r < 0 || r > n) throw ArithmeticException("Domain ERROR: nPr invalid")
      var res = 1.0
      for (i in 0 until r) {
        res *= (n - i)
      }
      return res
    }

    private fun nCr(n: Long, r: Long): Double {
      if (n < 0 || r < 0 || r > n) throw ArithmeticException("Domain ERROR: nCr invalid")
      val k = min(r, n - r)
      var res = 1.0
      for (i in 1..k) {
        res = res * (n - i + 1) / i
      }
      return res
    }
  }
}
