package selectSqlAnalyzer.main.tools

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
}