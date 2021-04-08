package selectSqlAnalyzer.main.parser

import java.lang.Exception
import java.lang.StringBuilder

class Parser(
        val query: String
) {
    private val selectParamsDelimeters = listOf(',', ' ','\n')
    private var pos = 0
    private fun currChar(): Char = query[pos]
    private fun currStr(): String = currChar().toString()

    fun nextNSymbols(cnt: Int): String {
        return query.substring(pos, pos + cnt)
    }

    fun isNext(str: String) = str == nextNSymbols(str.length)

    fun isNext(char: Char) = char == currChar()

    fun clearSpaces(){
        while (currChar()==' ' || currChar()=='\n')
            pos++
    }

    /*
        ~select *
         from tname;
     */
    fun selectOperator() : SelectOperator{
        clearSpaces()
        if(!isNext("select"))
            throw Exception("Err")
        pos+=6
        val selectParams = selectParams()

        return SelectOperator(selectParams)
    }

    /*  select~ *
        from tname;
    */
    /*  select~ a,b,c
        from tname;
    */
    fun selectParams(): List<SelectParam> {
        clearSpaces()
        val params = mutableListOf<SelectParam>()
        while (!isNext("from")) {
            try {
                val paramName = parseUntil(selectParamsDelimeters)
                params.add(SelectParam(paramName))
            } catch (e: Exception) {
                if (selectParamsDelimeters.contains(currChar()))
                    pos++
            }
        }
        return params
    }

    fun parseUntil(endSymbols: List<Char>): String {
        return with(StringBuilder()) {
            var cur = currChar()
            while (!selectParamsDelimeters.contains(cur)) {
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


}