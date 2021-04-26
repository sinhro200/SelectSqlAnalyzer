package selectSqlAnalyzer.main.parser

import java.lang.Exception
import java.lang.StringBuilder

class Parser(
        val query: String
) {
    private val selectParamsDelimeters = listOf(',', ' ', '\n', ';')
    private val fromParamsDelimeters = listOf(',', ' ', '\n', ';')
    private var pos = 0
    private fun currChar(): Char = query[pos]
    private fun currStr(): String = currChar().toString()

    fun nextNSymbols(cnt: Int): String {
        val left = query.length - pos
        val count = if (left > cnt) cnt else left
        return query.substring(pos, pos + count)
    }

    fun isNext(vararg strings: String): Boolean =
            strings.find { nextNSymbols(it.length) == it } != null

    fun isNext(char: Char) = char == currChar()

    fun clearSpaces() {
        while (currChar() == ' ' || currChar() == '\n')
            pos++
    }

    fun parseUntil(endSymbols: List<Char>): String {
        return with(StringBuilder()) {
            var cur = currChar()
            while (!endSymbols.contains(cur)) {
                append(cur)
                cur = query[++pos]
            }

            val result = toString().also {
                if (it.isEmpty())
                    throw Exception("empty parse result")
            }
            return@with result
        }
    }

    fun asValue(str: String): String? {
        if (str.toIntOrNull() != null)
            return str
        if (str[0] == '\"' && str.last() == '\"')
            return str.substring(1, str.lastIndex )
        return null
    }

    fun queryOperator(): OperatorQuery {
        clearSpaces()
        val selectOp = selectOperator()
        clearSpaces()
        if (isNext(';'))
            return OperatorQuery(selectOp, null, null)

        val fromOp = fromOperator()

        return OperatorQuery(selectOp, fromOp, OperatorWhere())
    }

    fun fromOperator(): OperatorFrom {
        clearSpaces()
        if (!isNext("from"))
            throw Exception("expected 'from'")
        pos += 4



        return OperatorFrom(fromParams()[0])
    }

    fun fromParams(): List<ParamFrom> {
        clearSpaces()
        val params = mutableListOf<ParamFrom>()
        while (!isNext(";", "where")) {
            try {
                val paramName = parseUntil(fromParamsDelimeters)
                params.add(ParamFrom(paramName))
            } catch (e: Exception) {
                if (fromParamsDelimeters.contains(currChar()))
                    pos++
            }
        }
        return params
    }

    /*
        ~select *
         from tname;
     */
    fun selectOperator(): OperatorSelect {
        clearSpaces()
        if (!isNext("select"))
            throw Exception("expected 'select'")
        pos += 6
        val selectParams = selectParams()

        return OperatorSelect(selectParams)
    }

    /*  select~ *
        from tname;
    */
    /*  select~ a,b,c
        from tname;
    */
    fun selectParams(): List<ParamSelect> {
        clearSpaces()
        val params = mutableListOf<ParamSelect>()
        while (!isNext("from", ";")) {
            try {
                val paramString = parseUntil(selectParamsDelimeters)
                val paramAsValue = asValue(paramString)

                params.add(ParamSelect(
                        paramAsValue ?: paramString,
                        paramAsValue != null
                ))
            } catch (e: Exception) {
                if (selectParamsDelimeters.contains(currChar()))
                    pos++
            }
        }
        return params
    }


}