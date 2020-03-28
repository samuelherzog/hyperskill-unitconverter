package converter

import converter.conversion.ConversionException
import converter.conversion.ConvertibleValue
import converter.domain.UnitDomainException
import converter.measurements.ConvertibleUnit

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readLine()!!.trim().toLowerCase()

        if (input == "exit") break

        try {
            val (baseScalar, baseUnit, _, rawTargetUnit) =
                    input.replace("degree[s]? ".toRegex(), "").split(" ")

            val convertible = ConvertibleValue(baseScalar, baseUnit)
            val targetUnit = ConvertibleUnit.fromName(rawTargetUnit)
            val target = convertible.convertTo(targetUnit)

            println("$convertible is $target")

        } catch (e: UnitDomainException) {
            println(e.message)
        } catch (e: ConversionException) {
            println("Conversion from ${e.base} to ${e.target} is impossible")
        } catch (e: Throwable) {
            println("Parse error.")
        }
        println()
    }
}
