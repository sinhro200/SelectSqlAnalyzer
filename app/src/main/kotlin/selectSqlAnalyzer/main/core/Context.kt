package selectSqlAnalyzer.main.core

class Context(
        val parentContext: Context? = null
) {
    private val tables = mutableMapOf<String?, ITable>()

    fun findTable(tableName: String): ITable? {
        return tables[tableName]
                ?: if (parentContext == null)
                    return null
                else
                    return parentContext.findTable(tableName)
    }

    fun addTable(table: ITable) {
        tables[table.tableName()] = table
    }


}