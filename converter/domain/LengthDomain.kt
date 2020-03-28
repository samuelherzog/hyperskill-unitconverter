package converter.domain

interface LengthDomain: PositiveDomain {
    override fun getDomainError() = "Length shouldn't be negative"
}