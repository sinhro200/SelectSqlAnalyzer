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
        val selectOnlyAstFile = "sqlselects.txt"
        val selectWithExecutionFile = "sqlselects2.txt"

        @JvmStatic
        fun main(args: Array<String>) {
            runOnlyAst()
            runWithExecution()
        }

        fun runOnlyAst(){
            val queries = with(QueriesParser(selectOnlyAstFile)) {
                parse()
                return@with this.queries
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

        fun runWithExecution(){
            println("___________________________________________________________________")
            println("                    - - - - - - - - - - - -")
            println("                    Running with execution")
            println("                    - - - - - - - - - - - -")
            println("___________________________________________________________________")
            val queries = with(QueriesParser(selectWithExecutionFile)) {
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
