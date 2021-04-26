package selectSqlAnalyzer.main

import selectSqlAnalyzer.main.core.Parser
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

            for (q in queries) {
                CustomLogger.logQuery(q)
                Parser(q).start()
            }
        }

    }
}
