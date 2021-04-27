package selectSqlAnalyzer.main

import selectSqlAnalyzer.main.core.IField
import selectSqlAnalyzer.main.core.ITable
import selectSqlAnalyzer.main.parsing.ParsingException

class ParseResult(
        val IFields: List<IField>,
        val from: ITable?,
        val where: Any? = null,
        val groupBy: Any? = null,
        val having: Any? = null,
        val orderBy: Any? = null,
    ) : IField, ITable {
        var tableName: String? = null

        override fun value(): String {
            if (IFields.size > 1)
                throw ParsingException("Expected one field")
            if (!IFields[0].isStatic())
                throw ParsingException("Expected static value, not field name")
            return IFields[0].value()
        }

        override fun isStatic(): Boolean {
            if (IFields.size > 1)
                throw ParsingException("Expected one field")
            if (!IFields[0].isStatic())
                throw ParsingException("Expected static value, not field name")
            return IFields[0].isStatic()
        }

    }