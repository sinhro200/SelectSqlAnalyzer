package selectSqlAnalyzer.main.core

interface IField {
    fun value(): String
    fun isStatic(): Boolean
}