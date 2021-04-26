package selectSqlAnalyzer.main.core

import selectSqlAnalyzer.main.Context
import selectSqlAnalyzer.main.Field
import selectSqlAnalyzer.main.Table
import java.lang.Exception
import java.lang.StringBuilder
import java.text.ParseException

class Parser(
        query: String,
        val parentContext: Context = Context.Global(),
        startPos: Int = 0
) {
    private val context = Context("")
    private lateinit var helper: Helper
    private var resultTable: Table? = null

    init {
        this.helper = Helper(query, startPos)
    }

    fun getTable(): Table {
        return this.resultTable ?: throw ParsingException("Cant give table, its null", helper.pos)
    }

    private fun parseInnerQuery(): Table {
        val p = Parser(helper.query, context, helper.pos)
        p.start()
        helper.pos = p.helper.pos
        return p.getTable()
    }

    fun start() {
        resultTable = Table("", mutableListOf())
        helper.apply {
            clearSpaces()
            parse("select")

            val sp = selectParams()
            sp.joinToString(separator = ",") { it.toPrettyString() }
        }

    }

    fun selectParams(): MutableList<SelectParam> {
        val selectParams = mutableListOf<SelectParam>()
        helper.apply {
            var selectParam: SelectParam
            do {
                clearSpaces()
                if (isParse('(')) {
                    move(1)
                    selectParam = SelectParam(parseInnerQuery().asValue().toString(), false)
                    clearSpaces()
                    if (isParse(')'))
                        move(1)
                    else
                        throw ParsingException("Expected ')'", pos)
                } else {
                    selectParam = SelectParam(parseUntil(' ', ',', '\n', ';'))
                    if (cur == ';')
                        break
                    move(1)
                    clearSpaces()
                }
                selectParams.add(selectParam)
                val f = Field(selectParam.data)
                resultTable?.fields?.add(f)
                
                 mutableListOf(context.tables.find { it.name=="input" }?.fields)

            } while (!isParse("from", ";"))
        }
        return selectParams
    }

    class Helper(
            val query: String,
            val startPos: Int = 0
    ) {
        var pos: Int = startPos
        var cur: Char = query[startPos]

        fun move(n: Int = 1) {
            pos += n
            cur = query[pos]
        }

        fun canMove(n: Int = 1) = pos + n < query.length

        fun parseUntil(vararg endSymbols: Char): String {
            return with(StringBuilder()) {
                while (!endSymbols.contains(cur)) {
                    append(cur)
                    if (canMove())
                        move()
                    else
                        break
                }

                val result = toString().also {
                    if (it.isEmpty())
                        throw Exception("empty parse result")
                }
                return@with result
            }
        }

        fun clearSpaces() {
            while (cur == ' ' || cur == '\n')
                move()
        }

        fun nextNSymbols(cnt: Int): String {
            val left = query.length - pos
            val count = if (left > cnt) cnt else left
            val substring = query.substring(pos, pos + count)
            return substring
        }

        fun nextSymbol(): Char {
            if (canMove())
                return query[pos + 1]
            throw ParsingException("Cant move", pos)
        }

        fun parse(vararg texts: String): String {
            for (t in texts)
                if (canMove(t.length) && nextNSymbols(t.length) == t) {
                    move(t.length)
                    return t
                }
            throw ParsingException("Cant find ${if (texts.size != 1) "any of " else ""}[${texts.joinToString(",")}]", pos)
        }

        fun isParse(vararg texts: String): Boolean {
            for (t in texts)
                if (canMove(t.length) && nextNSymbols(t.length) == t) {
                    return true
                }
            return false
        }

        fun isParse(vararg texts: Char): Boolean {
            for (t in texts)
                if (cur == t) {
                    return true
                }
            return false
        }
    }
}