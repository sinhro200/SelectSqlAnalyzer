package selectSqlAnalyzer.main.core

class Context(
        val parentContext: Context? = null
) {
    val tables = mutableMapOf<String, ITable>()

    fun find(tableName: String): ITable? {
        return tables[tableName]
                ?: if (parentContext == null)
                    return null
                else
                    return parentContext.find(tableName)
    }
}