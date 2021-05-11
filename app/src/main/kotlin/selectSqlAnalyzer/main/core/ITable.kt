package selectSqlAnalyzer.main.core

interface ITable {
    fun fields(): List<String>
    fun data(): List<List<String>>
}