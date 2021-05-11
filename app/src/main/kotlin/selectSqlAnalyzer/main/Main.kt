package selectSqlAnalyzer.main

import selectSqlAnalyzer.main.ast.parsing.AstTreeParser
import selectSqlAnalyzer.main.core.Context
import selectSqlAnalyzer.main.core.Table
import selectSqlAnalyzer.main.parsing.ParsingException
import selectSqlAnalyzer.main.tools.CustomLogger
import selectSqlAnalyzer.main.tools.FileParser
import java.io.PrintStream




class Main {
    companion object {
        val selectFile = "sqlselects.txt"

        @JvmStatic
        fun main(args: Array<String>) {
            val queries = with(FileParser(selectFile)) {
                parse()
                return@with this.queries
            }

            val context = Context().also {
                it.addTable(Table(emptyList()), "input")
                it.addTable(Table(emptyList()), "t")
            }

            for (q in queries) {
                CustomLogger.logQuery(q)
                try {
                    val parseResult = AstTreeParser(q).parse()

                    CustomLogger.logAstTree(parseResult)
                } catch (pe: ParsingException) {
                    val encoding = System.getProperty("console.encoding", "UTF-8")
                    val ps = PrintStream(System.out, false, encoding)
                    ps.println("Ошибка. " + pe.message)
//                    println("Ошибка. " + pe.message)
                    println(pe.stackTrace.joinToString("\n   "))
                }
                CustomLogger.bigDivider()
            }
        }

    }
}
