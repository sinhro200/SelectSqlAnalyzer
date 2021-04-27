package selectSqlAnalyzer.main

import selectSqlAnalyzer.main.core.Context
import selectSqlAnalyzer.main.core.Table
import selectSqlAnalyzer.main.parsing.Parser
import selectSqlAnalyzer.main.tools.CustomLogger
import selectSqlAnalyzer.main.tools.FileParser

class Main {
    companion object {
        val selectFile = "sqlselect.txt"

        @JvmStatic
        fun main(args: Array<String>) {
            val queries = with(FileParser(selectFile)) {
                parse()
                return@with this.queries
            }

            val context = Context().also {
                it.addTable(Table("input", emptyList()))
                it.addTable(Table("t", emptyList()))
            }

            for (q in queries) {
                CustomLogger.logQuery(q)
                val parseResult = Parser(q, context).parse()

                CustomLogger.logParseResult(parseResult)
                CustomLogger.bigDivider()
            }
        }

    }
}
