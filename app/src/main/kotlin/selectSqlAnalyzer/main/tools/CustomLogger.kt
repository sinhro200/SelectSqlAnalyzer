package selectSqlAnalyzer.main.tools

import selectSqlAnalyzer.main.ParseResult
import selectSqlAnalyzer.main.core.IField
import selectSqlAnalyzer.main.core.ITable

object CustomLogger {
    const val ZoneDivider = "-- -- -- -- --"
    fun logQuery(query: String) {
        log(ZoneDivider)
        log(query)
        log(ZoneDivider)
    }

    fun log(text: String) {
        println(text)
    }

    fun bigDivider() {
        log("")
        log("")
    }

    fun logParseResult(pr: ParseResult) {
        logParseResultInner(pr)
    }

    private fun logParseResultInner(pr: ParseResult, level: Int = 0) {
        LevelLogger(level).apply {
            if (level != 0)
                log(" -(select", level - 1)
            else
                log("select")
            pr.fields.forEach {
                if (it is ParseResult)
                    logParseResultInner(it, level + 1)
                else {
                    log(" - ${it.prettyString()}");
                }
            }

            log("from")
            if (pr.from is ParseResult) {
                logParseResultInner(pr.from, level + 1)
            } else
                log(" - ${pr.from?.prettyString() ?: "none"}")

            if (level != 0 && pr.tableName() != null)
                log("  ) as ${pr.tableName()}", level - 1)

        }
    }

    private class LevelLogger(
            protected val level: Int
    ) {
        fun log(text: String, forceLevel: Int = level) {
            for (i in 0..forceLevel)
                print("   ")
            println(text)
        }
    }

    fun IField.prettyString(): String {
        //return "${this.value()} " + if (!this.isStatic()) " as field" else " as static value"
        val value = this.value()
        return if (this.isStatic()) "\'$value\'"
        else value
    }

    fun ITable.prettyString(): String {
        return "${this.tableName()} [${this.fields().joinToString(",")}]"
    }

}