package com.example

import com.example.engine.*
import com.example.model.AngleUnit
import com.example.model.BaseNNumberSystem
import com.example.model.MatrixModel
import com.example.model.VectorModel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testMathEvaluatorBasicArithmetic() {
    val res1 = MathEvaluator.evaluate("2 + 3 * 4")
    assertEquals(14.0, res1.decimalValue, 1e-9)

    val res2 = MathEvaluator.evaluate("(10 - 4) / 2")
    assertEquals(3.0, res2.decimalValue, 1e-9)

    // Negation key test: (-)5 + 10 -> 5
    val res3 = MathEvaluator.evaluate("(-)5 + 10")
    assertEquals(5.0, res3.decimalValue, 1e-9)

    // Implicit multiplication with PI
    val res4 = MathEvaluator.evaluate("2π")
    assertEquals(2.0 * Math.PI, res4.decimalValue, 1e-7)
  }

  @Test
  fun testMathEvaluatorTrigDegrees() {
    val sin30 = MathEvaluator.evaluate("sin(30)", angleUnit = AngleUnit.DEG)
    assertEquals(0.5, sin30.decimalValue, 1e-7)

    val cos60 = MathEvaluator.evaluate("cos(60)", angleUnit = AngleUnit.DEG)
    assertEquals(0.5, cos60.decimalValue, 1e-7)

    val tan45 = MathEvaluator.evaluate("tan(45)", angleUnit = AngleUnit.DEG)
    assertEquals(1.0, tan45.decimalValue, 1e-7)

    // Asin domain check error
    val asinErr = MathEvaluator.evaluate("sin⁻¹(2.0)", angleUnit = AngleUnit.DEG)
    assertTrue(asinErr.isError)
  }

  @Test
  fun testPowersRootsAndFactorial() {
    val sqrtVal = MathEvaluator.evaluate("√(16)")
    assertEquals(4.0, sqrtVal.decimalValue, 1e-9)

    val powVal = MathEvaluator.evaluate("2^3")
    assertEquals(8.0, powVal.decimalValue, 1e-9)

    val factVal = MathEvaluator.evaluate("5!")
    assertEquals(120.0, factVal.decimalValue, 1e-9)
  }

  @Test
  fun testFractionConversion() {
    val frac = MathEvaluator.toFraction(0.75)
    assertEquals("3/4", frac)

    val frac2 = MathEvaluator.toFraction(2.5)
    assertTrue(frac2?.startsWith("5/2") == true)
  }

  @Test
  fun testMatrixDeterminantAndMultiplication() {
    val matA = MatrixModel("MatA", 2, 2, listOf(1.0, 2.0, 3.0, 4.0))
    val det = MatrixEngine.determinant(matA).getOrNull()
    assertNotNull(det)
    assertEquals(-2.0, det!!, 1e-9)

    val ident = MatrixEngine.createIdentity("I", 2)
    val mult = MatrixEngine.multiply(matA, ident).getOrNull()
    assertNotNull(mult)
    assertEquals(matA.elements, mult!!.elements)
  }

  @Test
  fun testVectorOperations() {
    val u = VectorModel("u", 3, 1.0, 2.0, 3.0)
    val v = VectorModel("v", 3, 4.0, -5.0, 6.0)

    // Dot product: 1*4 + 2*(-5) + 3*6 = 4 - 10 + 18 = 12
    val dot = VectorEngine.dotProduct(u, v)
    assertEquals(12.0, dot, 1e-9)

    // Magnitude of [3, 4, 0] = 5
    val w = VectorModel("w", 3, 3.0, 4.0, 0.0)
    assertEquals(5.0, w.magnitude, 1e-9)

    // Cross product of [1, 0, 0] and [0, 1, 0] is [0, 0, 1]
    val i = VectorModel("i", 3, 1.0, 0.0, 0.0)
    val j = VectorModel("j", 3, 0.0, 1.0, 0.0)
    val kRes = VectorEngine.crossProduct(i, j).getOrNull()
    assertNotNull(kRes)
    assertEquals(0.0, kRes!!.x, 1e-9)
    assertEquals(0.0, kRes.y, 1e-9)
    assertEquals(1.0, kRes.z, 1e-9)
  }

  @Test
  fun testBaseNEngine() {
    // 255 in DEC is FF in HEX, 377 in OCT, 11111111 in BIN
    val hex = BaseNEngine.format(255L, BaseNNumberSystem.HEX)
    assertEquals("FF", hex)

    val oct = BaseNEngine.format(255L, BaseNNumberSystem.OCT)
    assertEquals("377", oct)

    val parsedHex = BaseNEngine.parse("FF", BaseNNumberSystem.HEX)
    assertEquals(255L, parsedHex)

    // Bitwise AND of 12 (1100) and 10 (1010) = 8 (1000)
    val andVal = BaseNEngine.bitwiseAnd(12L, 10L)
    assertEquals(8L, andVal)
  }

  @Test
  fun testCalculusEngine() {
    // Integral of x^2 from 0 to 3 = [x^3 / 3] = 9.0
    val integral = CalculusEngine.definiteIntegral("X^2", 0.0, 3.0, 1000)
    assertEquals(9.0, integral, 1e-3)

    // Derivative of x^2 at x = 2 is 2*x = 4.0
    val deriv = CalculusEngine.derivative("X^2", 2.0)
    assertEquals(4.0, deriv, 1e-3)
  }

  @Test
  fun testQuadraticEquationSolver() {
    val res = EquationSolverEngine.solveQuadratic(1.0, -5.0, 6.0)
    assertEquals(1.0, res.discriminant, 1e-9)
    val r1 = res.root1.real
    val r2 = res.root2.real
    assertTrue((r1 == 3.0 && r2 == 2.0) || (r1 == 2.0 && r2 == 3.0))
  }

  @Test
  fun testUnitConversion() {
    val lengthCat = UnitConverterEngine.categories.first { it.id == "length" }
    val km = lengthCat.units.first { it.symbol == "km" }
    val m = lengthCat.units.first { it.symbol == "m" }

    val converted = UnitConverterEngine.convert(1.0, km, m, "length")
    assertEquals(1000.0, converted, 1e-9)

    val tempCat = UnitConverterEngine.categories.first { it.id == "temp" }
    val c = tempCat.units.first { it.symbol == "°C" }
    val f = tempCat.units.first { it.symbol == "°F" }
    val fVal = UnitConverterEngine.convert(100.0, c, f, "temp")
    assertEquals(212.0, fVal, 1e-9)
  }

  @Test
  fun testSimultaneous3Solver() {
    // 1x + 2y + 3z = 14; 2x - 1y + 1z = 3; 3x + 1y - 1z = 2
    // Solution is x=1, y=2, z=3
    val res = EquationSolverEngine.solveSimultaneous3(
      1.0, 2.0, 3.0, 14.0,
      2.0, -1.0, 1.0, 3.0,
      3.0, 1.0, -1.0, 2.0
    )
    assertTrue(res.isSolvable)
    assertEquals(1.0, res.x, 1e-6)
    assertEquals(2.0, res.y, 1e-6)
    assertEquals(3.0, res.z, 1e-6)
  }

  @Test
  fun testMathEvaluatorAutoClosingParenthesesAndPol() {
    // Unclosed parenthesis: sin(30 should auto-close to sin(30)
    val autoClose = MathEvaluator.evaluate("sin(30", angleUnit = AngleUnit.DEG)
    assertEquals(0.5, autoClose.decimalValue, 1e-7)

    // Pol(3, 4) -> 5.0
    val polVal = MathEvaluator.evaluate("pol(3, 4)")
    assertEquals(5.0, polVal.decimalValue, 1e-7)

    // Rec(5, 0) -> 5.0 (with DEG)
    val recVal = MathEvaluator.evaluate("rec(5, 0)", angleUnit = AngleUnit.DEG)
    assertEquals(5.0, recVal.decimalValue, 1e-7)
  }
}
