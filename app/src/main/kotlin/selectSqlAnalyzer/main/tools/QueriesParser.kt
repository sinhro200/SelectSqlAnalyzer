package selectSqlAnalyzer.main.tools

import java.io.FileNotFoundException
import java.io.FileReader
import java.lang.StringBuilder
import kotlin.jvm.Throws

class QueriesParser(
        val filename: String
) {
    var queries: MutableList<String> = mutableListOf()

    fun parse() {
        parseQueries(wholeFileText())
    }

    private fun parseQueries(textToParse: String) {
        var text = textToParse
        while (text.trim().isNotEmpty()) {
            var query = with(StringBuilder()) {
                var pos = 0
                var cur: Char
                do {
                    cur = text[pos]
                    append(text[pos])
                    pos++
                } while (pos < text.length && cur != ';')
                return@with toString()
            }

            if (text.length > query.length && text[query.length] == ';') {
                text = text.substring( query.length + 1)
                query += ';'
            } else
                text = text.substring( query.length)
            queries.add(query.trim())
        }
    }

    @Throws(FileNotFoundException::class)
    private fun wholeFileText(): String {
        javaClass.classLoader.getResource(filename)?.file?.let {
            with(FileReader(it)) {
                val text = readText()
                close()
                return text
            }
        } ?: throw FileNotFoundException(filename)
    }
}