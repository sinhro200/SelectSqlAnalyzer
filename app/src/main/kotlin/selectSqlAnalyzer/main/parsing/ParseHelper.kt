package selectSqlAnalyzer.main.parsing

import java.lang.Exception
import java.lang.StringBuilder

class ParseHelper(
        val text: String,
        val startPos: Int = 0
) {
    var pos: Int = startPos
        set(value){
            field = value
            cur = text[value]
        }
    var cur: Char = text[startPos]

    fun move(n: Int = 1) {
        pos += n
//        cur = text[pos]
    }

    fun canMove(n: Int = 1) = pos + n < text.length

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

    fun parseFieldString(): String {
        clearSpaces()
        fun isCorrect(c: Char): Boolean {
            return c.isDigit() || c.isLetter() || listOf('.').contains(c)
        }
        return with(StringBuilder()) {
            while (isCorrect(cur)) {
                append(cur)
                if (canMove())
                    move()
                else
                    return toString()
            }
            return toString()
        }
    }

    fun parseTableName(): String {
        if (cur.isDigit())
            throw ParsingException("Table name should not start with a digit")
        fun isCorrect(c: Char): Boolean {
            return c.isDigit() || c.isLetter()
        }
        return with(StringBuilder()) {
            while (isCorrect(cur)) {
                append(cur)
                move()
            }
            return toString()
        }
    }

    fun clearSpaces() {
        while (pos < text.length - 1 && (cur == ' ' || cur == '\n'))
            move()
    }

    fun nextNSymbols(cnt: Int): String {
        val left = text.length - pos
        val count = if (left > cnt) cnt else left
        val substring = text.substring(pos, pos + count)
        return substring
    }

    fun nextSymbol(): Char {
        if (canMove())
            return text[pos + 1]
        throw ParsingException("Cant move", pos)
    }

    fun parseStr(vararg texts: String) = parseStr(texts.asList())

    fun parseStr(texts: List<String>): String {
        for (t in texts)
            if (canMove(t.length) && nextNSymbols(t.length) == t && !canMove(t.length+1) ||
                    (canMove(t.length+1) && nextNSymbols(t.length) == t  && text[pos+t.length].isWhitespace())) {
                move(t.length)
                return t
            }
        throw ParsingException("Cant find ${if (texts.size != 1) "any of " else ""}[${texts.joinToString(",")}]", pos)
    }

    fun parse(vararg chars: Char): Char = parse(chars.asList())

    fun parse(chars: List<Char>): Char {
        for (c in chars)
            if (canMove() && cur == c) {
                move()
                return c
            }
        throw ParsingException("Cant find ${if (chars.size != 1) "any of " else ""}[${chars.joinToString(",")}]", pos)
    }

    fun isParseStr(vararg texts: String) = isParseStr(texts.asList())

    fun isParseStr(texts: List<String>): Boolean {
        for (t in texts)
            if (canMove(t.length) && nextNSymbols(t.length) == t) {
                return true
            }
        return false
    }

    fun isParse(vararg chars: Char) = isParse(chars.asList())

    fun isParse(chars: List<Char>): Boolean {
        for (t in chars)
            if (cur == t) {
                return true
            }
        return false
    }

}