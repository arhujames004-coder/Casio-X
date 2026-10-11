package com.example.engine

import com.example.model.BaseNNumberSystem
import java.util.Locale

object BaseNEngine {

  fun parse(valueStr: String, base: BaseNNumberSystem): Long? {
    val clean = valueStr.trim().replace(" ", "").replace("_", "")
    if (clean.isEmpty()) return 0L
    return try {
      java.lang.Long.parseUnsignedLong(clean, base.radix)
    } catch (e: Exception) {
      try {
        clean.toLong()
      } catch (e2: Exception) {
        null
      }
    }
  }

  fun format(value: Long, base: BaseNNumberSystem): String {
    return when (base) {
      BaseNNumberSystem.DEC -> value.toString()
      BaseNNumberSystem.HEX -> java.lang.Long.toHexString(value).uppercase(Locale.US)
      BaseNNumberSystem.BIN -> {
        val bin = java.lang.Long.toBinaryString(value)
        // Group into nibbles (4 bits) for readability
        bin.reversed().chunked(4).joinToString(" ").reversed()
      }
      BaseNNumberSystem.OCT -> java.lang.Long.toOctalString(value)
    }
  }

  fun bitwiseAnd(a: Long, b: Long): Long = a and b
  fun bitwiseOr(a: Long, b: Long): Long = a or b
  fun bitwiseXor(a: Long, b: Long): Long = a xor b
  fun bitwiseNot(a: Long): Long = a.inv()
  fun bitwiseNand(a: Long, b: Long): Long = (a and b).inv()
  fun bitwiseNor(a: Long, b: Long): Long = (a or b).inv()
  fun bitwiseXnor(a: Long, b: Long): Long = (a xor b).inv()
  fun shiftLeft(a: Long, bits: Int): Long = a shl bits
  fun shiftRight(a: Long, bits: Int): Long = a ushr bits
  fun negate(a: Long): Long = -a
}
