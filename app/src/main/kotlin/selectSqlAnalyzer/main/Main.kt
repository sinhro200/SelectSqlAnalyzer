package selectSqlAnalyzer.main

import selectSqlAnalyzer.main.ast.parsing.AstTreeParser
import selectSqlAnalyzer.main.core.Context
import selectSqlAnalyzer.main.core.Table
import selectSqlAnalyzer.main.parsing.ParsingException
import selectSqlAnalyzer.main.parsing.TableSourceParser
import selectSqlAnalyzer.main.tools.CustomLogger
import selectSqlAnalyzer.main.tools.QueriesParser
import java.io.PrintStream




class Main {
    companion object {
        val selectFile = "sqlselect.txt"

        @JvmStatic
        fun main(args: Array<String>) {
            val queries = with(QueriesParser(selectFile)) {
                parse()
                return@with this.queries
            }

            val context = Context().also {ctx->
                val tables = TableSourceParser.parseFromFile("tables.txt")
                tables.forEach{
                    ctx.addTable(it)
                }
            }

            for (q in queries) {
                CustomLogger.logQuery(q)
                try {
                    val parseResult = AstTreeParser(q).parse()

                    CustomLogger.logAstTree(parseResult)

                    context.copyOnlyTables().execute(parseResult)
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
