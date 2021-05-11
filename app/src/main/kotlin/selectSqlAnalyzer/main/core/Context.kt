package selectSqlAnalyzer.main.core

import selectSqlAnalyzer.main.ast.nodes.SelectAstNode

class Context(
        val parentContext: Context? = null
) {
    private val tables = mutableMapOf<String, Pair<ITable, Int>>()

    fun addTable(table: ITable, tableName: String) {
        tables[tableName] = Pair(table, 0)
    }

    fun execute(parseResult: SelectAstNode){
        //todo
    }

}