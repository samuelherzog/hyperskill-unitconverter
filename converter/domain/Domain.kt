package converter.domain

import java.lang.Exception

interface UnitDomain {
    fun checkDomain(value: Double) {
        if (!inDomain(value)) throw domainException
    }

    fun inDomain(value: Double): Boolean
    fun getDomainError() : String
    val domainException get() = UnitDomainException(getDomainError())
}

interface FullDomain: UnitDomain {
    override fun inDomain(value: Double) = true
    override fun getDomainError() = ""
}

interface LowerBoundDomain : UnitDomain {
    val lowerBound: Double
    override fun inDomain(value: Double) = value >= lowerBound
}

interface PositiveDomain: LowerBoundDomain {
    override val lowerBound: Double
        get() = 0.0
}

class UnitDomainException(message: String) : Exception(message)
