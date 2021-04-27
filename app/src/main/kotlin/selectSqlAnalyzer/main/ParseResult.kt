package selectSqlAnalyzer.main

import selectSqlAnalyzer.main.core.IField
import selectSqlAnalyzer.main.core.ITable
import selectSqlAnalyzer.main.parsing.ParsingException

class ParseResult(
        val fields: List<IField>,
        val from: ITable?,
        val where: Any? = null,
        val groupBy: Any? = null,
        val having: Any? = null,
        val orderBy: Any? = null,
) : IField, ITable {
    var tableName: String? = null

    //IField func's region
    override fun value(): String {
        if (fields.isEmpty())
            throw ParsingException("Подзапрос должен вернуть один столбец")
        if (fields.size > 1)
            throw ParsingException("Подзапрос должен вернуть только один столбец")
        return fields[0].value()
    }

    override fun isStatic(): Boolean {
        if (fields.size > 1)
            throw ParsingException("Подзапрос должен вернуть только один столбец")
        return fields[0].isStatic()
    }
    //end region

    //ITable func's region
    override fun tableName(): String? {
        return tableName
    }

    override fun fields(): List<String> {
        return fields.map { it.value() }
    }

    override fun data(): List<List<String>> {
        return emptyList()
    }
    //end region

}