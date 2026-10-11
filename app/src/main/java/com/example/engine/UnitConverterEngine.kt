package com.example.engine

import com.example.model.ScientificConstant

object UnitConverterEngine {

  data class UnitItem(
    val name: String,
    val symbol: String,
    val toBaseFactor: Double, // value in base = value * factor
    val offset: Double = 0.0 // for temperature: (value + offset) * factor
  )

  data class Category(
    val id: String,
    val name: String,
    val baseUnitSymbol: String,
    val units: List<UnitItem>
  )

  val categories = listOf(
    Category(
      id = "length",
      name = "Length",
      baseUnitSymbol = "m",
      units = listOf(
        UnitItem("Meter", "m", 1.0),
        UnitItem("Kilometer", "km", 1000.0),
        UnitItem("Centimeter", "cm", 0.01),
        UnitItem("Millimeter", "mm", 0.001),
        UnitItem("Micrometer", "µm", 1e-6),
        UnitItem("Nanometer", "nm", 1e-9),
        UnitItem("Mile", "mi", 1609.344),
        UnitItem("Yard", "yd", 0.9144),
        UnitItem("Foot", "ft", 0.3048),
        UnitItem("Inch", "in", 0.0254),
        UnitItem("Nautical Mile", "NM", 1852.0),
        UnitItem("Light Year", "ly", 9.4607e15)
      )
    ),
    Category(
      id = "mass",
      name = "Mass & Weight",
      baseUnitSymbol = "kg",
      units = listOf(
        UnitItem("Kilogram", "kg", 1.0),
        UnitItem("Gram", "g", 0.001),
        UnitItem("Milligram", "mg", 1e-6),
        UnitItem("Metric Ton", "t", 1000.0),
        UnitItem("Pound", "lb", 0.45359237),
        UnitItem("Ounce", "oz", 0.028349523),
        UnitItem("Stone", "st", 6.35029),
        UnitItem("Carat", "ct", 0.0002)
      )
    ),
    Category(
      id = "temp",
      name = "Temperature",
      baseUnitSymbol = "K",
      units = listOf(
        UnitItem("Celsius", "°C", 1.0, 273.15),
        UnitItem("Fahrenheit", "°F", 5.0 / 9.0, 459.67),
        UnitItem("Kelvin", "K", 1.0, 0.0),
        UnitItem("Rankine", "°R", 5.0 / 9.0, 0.0)
      )
    ),
    Category(
      id = "area",
      name = "Area",
      baseUnitSymbol = "m²",
      units = listOf(
        UnitItem("Square Meter", "m²", 1.0),
        UnitItem("Square Kilometer", "km²", 1e6),
        UnitItem("Hectare", "ha", 10000.0),
        UnitItem("Acre", "ac", 4046.856),
        UnitItem("Square Foot", "sq ft", 0.092903),
        UnitItem("Square Inch", "sq in", 0.00064516),
        UnitItem("Square Mile", "sq mi", 2.58999e6)
      )
    ),
    Category(
      id = "volume",
      name = "Volume",
      baseUnitSymbol = "L",
      units = listOf(
        UnitItem("Liter", "L", 1.0),
        UnitItem("Milliliter", "mL", 0.001),
        UnitItem("Cubic Meter", "m³", 1000.0),
        UnitItem("US Gallon", "gal", 3.78541),
        UnitItem("US Quart", "qt", 0.946353),
        UnitItem("US Pint", "pt", 0.473176),
        UnitItem("US Cup", "cup", 0.236588),
        UnitItem("Fluid Ounce", "fl oz", 0.0295735),
        UnitItem("Tablespoon", "tbsp", 0.0147868),
        UnitItem("Teaspoon", "tsp", 0.00492892)
      )
    ),
    Category(
      id = "speed",
      name = "Speed",
      baseUnitSymbol = "m/s",
      units = listOf(
        UnitItem("Meter/sec", "m/s", 1.0),
        UnitItem("Kilometer/hr", "km/h", 1.0 / 3.6),
        UnitItem("Miles/hr", "mph", 0.44704),
        UnitItem("Knot", "kn", 0.514444),
        UnitItem("Feet/sec", "ft/s", 0.3048),
        UnitItem("Speed of Light", "c", 299792458.0)
      )
    ),
    Category(
      id = "pressure",
      name = "Pressure",
      baseUnitSymbol = "Pa",
      units = listOf(
        UnitItem("Pascal", "Pa", 1.0),
        UnitItem("Kilopascal", "kPa", 1000.0),
        UnitItem("Bar", "bar", 100000.0),
        UnitItem("Atmosphere", "atm", 101325.0),
        UnitItem("Pound/sq inch", "psi", 6894.757),
        UnitItem("Millimeter of Mercury", "mmHg", 133.322)
      )
    ),
    Category(
      id = "energy",
      name = "Energy",
      baseUnitSymbol = "J",
      units = listOf(
        UnitItem("Joule", "J", 1.0),
        UnitItem("Kilojoule", "kJ", 1000.0),
        UnitItem("Calorie", "cal", 4.184),
        UnitItem("Kilocalorie", "kcal", 4184.0),
        UnitItem("Watt-hour", "Wh", 3600.0),
        UnitItem("Kilowatt-hour", "kWh", 3.6e6),
        UnitItem("Electron-volt", "eV", 1.602176634e-19),
        UnitItem("British Thermal Unit", "BTU", 1055.06)
      )
    ),
    Category(
      id = "power",
      name = "Power",
      baseUnitSymbol = "W",
      units = listOf(
        UnitItem("Watt", "W", 1.0),
        UnitItem("Kilowatt", "kW", 1000.0),
        UnitItem("Megawatt", "MW", 1e6),
        UnitItem("Horsepower (metric)", "hp", 735.49875),
        UnitItem("Foot-pound/sec", "ft·lb/s", 1.355818)
      )
    ),
    Category(
      id = "data",
      name = "Digital Data",
      baseUnitSymbol = "B",
      units = listOf(
        UnitItem("Byte", "B", 1.0),
        UnitItem("Kilobyte (KB)", "KB", 1000.0),
        UnitItem("Megabyte (MB)", "MB", 1e6),
        UnitItem("Gigabyte (GB)", "GB", 1e9),
        UnitItem("Terabyte (TB)", "TB", 1e12),
        UnitItem("Kibibyte (KiB)", "KiB", 1024.0),
        UnitItem("Mebibyte (MiB)", "MiB", 1048576.0),
        UnitItem("Gibibyte (GiB)", "GiB", 1073741824.0),
        UnitItem("Bit", "b", 0.125)
      )
    ),
    Category(
      id = "angle",
      name = "Angle",
      baseUnitSymbol = "deg",
      units = listOf(
        UnitItem("Degree", "°", 1.0),
        UnitItem("Radian", "rad", 180.0 / Math.PI),
        UnitItem("Gradian", "grad", 0.9),
        UnitItem("Arcminute", "arcmin", 1.0 / 60.0),
        UnitItem("Arcsecond", "arcsec", 1.0 / 3600.0)
      )
    )
  )

  fun convert(value: Double, from: UnitItem, to: UnitItem, categoryId: String): Double {
    if (from.symbol == to.symbol) return value

    if (categoryId == "temp") {
      // Temperature conversion
      // 1. Convert to Kelvin
      val kelvin = when (from.symbol) {
        "°C" -> value + 273.15
        "°F" -> (value + 459.67) * 5.0 / 9.0
        "K" -> value
        "°R" -> value * 5.0 / 9.0
        else -> value
      }
      // 2. Convert from Kelvin to target
      return when (to.symbol) {
        "°C" -> kelvin - 273.15
        "°F" -> kelvin * 9.0 / 5.0 - 459.67
        "K" -> kelvin
        "°R" -> kelvin * 9.0 / 5.0
        else -> kelvin
      }
    }

    val baseValue = value * from.toBaseFactor
    return baseValue / to.toBaseFactor
  }

  val scientificConstants = listOf(
    ScientificConstant("c", "Speed of light in vacuum", 299792458.0, "m/s", "2.99792458×10⁸", "Physics"),
    ScientificConstant("h", "Planck constant", 6.62607015e-34, "J·s", "6.62607015×10⁻³⁴", "Quantum"),
    ScientificConstant("ℏ", "Reduced Planck constant", 1.054571817e-34, "J·s", "1.0545718×10⁻³⁴", "Quantum"),
    ScientificConstant("G", "Newtonian gravitational constant", 6.67430e-11, "m³/(kg·s²)", "6.67430×10⁻¹¹", "Gravity"),
    ScientificConstant("e", "Elementary charge", 1.602176634e-19, "C", "1.6021766×10⁻¹⁹", "Electromagnetism"),
    ScientificConstant("m_e", "Electron rest mass", 9.1093837015e-31, "kg", "9.1093837×10⁻³¹", "Particles"),
    ScientificConstant("m_p", "Proton rest mass", 1.67262192369e-27, "kg", "1.6726219×10⁻²⁷", "Particles"),
    ScientificConstant("m_n", "Neutron rest mass", 1.67492749804e-27, "kg", "1.6749275×10⁻²⁷", "Particles"),
    ScientificConstant("N_A", "Avogadro constant", 6.02214076e23, "mol⁻¹", "6.02214076×10²³", "Chemistry"),
    ScientificConstant("k_B", "Boltzmann constant", 1.380649e-23, "J/K", "1.380649×10⁻²³", "Thermodynamics"),
    ScientificConstant("R", "Molar gas constant", 8.314462618, "J/(mol·K)", "8.3144626", "Chemistry"),
    ScientificConstant("σ", "Stefan-Boltzmann constant", 5.670374419e-8, "W/(m²·K⁴)", "5.670374×10⁻⁸", "Thermodynamics"),
    ScientificConstant("g", "Standard gravity on Earth", 9.80665, "m/s²", "9.80665", "Gravity"),
    ScientificConstant("ε₀", "Vacuum electric permittivity", 8.8541878128e-12, "F/m", "8.8541878×10⁻¹²", "Electromagnetism"),
    ScientificConstant("μ₀", "Vacuum magnetic permeability", 1.25663706212e-6, "N/A²", "1.256637×10⁻⁶", "Electromagnetism"),
    ScientificConstant("α", "Fine-structure constant", 0.0072973525693, "", "7.2973525×10⁻³ (≈ 1/137)", "Quantum")
  )
}
