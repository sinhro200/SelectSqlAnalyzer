package selectSqlAnalyzer.main.core

class Field(
        private val value: String,
        private val isStatic: Boolean,
) : IField {
    override fun value(): String = value

    override fun isStatic(): Boolean = isStatic

    companion object {
        val ALL = Field("*", false)
    }
}