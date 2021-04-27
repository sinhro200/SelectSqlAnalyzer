package selectSqlAnalyzer.main.core

interface ITable {
    fun tableName(): String?
    fun fields(): List<String>
    fun data(): List<List<String>>
}