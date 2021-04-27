package selectSqlAnalyzer.main.tools

import selectSqlAnalyzer.main.ParseResult

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

    fun bigDivider(){
        log("")
        log("")
    }

    fun logParseResult(pr: ParseResult) {
        log(ZoneDivider)
        logParseResultInner(pr)
        log(ZoneDivider)
    }

    private fun logParseResultInner(pr: ParseResult, level: Int = 0) {
        LevelLogger(level).apply {
            log("select")
            pr.IFields.forEach {
                if (it is ParseResult)
                    logParseResultInner(it, level + 1)
                else {
                    log(" - ${it.value()}" + if (!it.isStatic()) " as field" else " as static value")
                }
            }

            log("from")
            if (pr.from is ParseResult)
                logParseResultInner(pr.from, level + 1)
            else
                log(" - ${pr.from}")
        }
    }

    private class LevelLogger(
            protected val level: Int
    ) {
        fun log(text: String) {
            for (i in 0..level)
                print("   ")
            println(text)
        }
    }


}