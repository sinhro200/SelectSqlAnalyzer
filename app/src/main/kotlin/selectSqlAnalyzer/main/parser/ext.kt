package selectSqlAnalyzer.main.parser

import java.lang.Exception
import java.lang.StringBuilder
import kotlin.jvm.Throws

@Throws(Exception::class)
fun (String).parseUntil(endSymbols: List<Char>, beginPosition: Int): String
        = parseUntil(endSymbols, this, beginPosition)


fun parseUntil(endSymbols: List<Char>, str: String, beginPosition: Int): String {
    var pos = beginPosition
    return with(StringBuilder()) {
        var cur = str[pos]
        while (cur != ',' && cur != ' ') {
            append(cur)
            cur = str[pos++]
        }

        val result = toString().also {
            if (it.isEmpty())
                throw Exception("empty parse result")
        }
        return@with result
    }
}