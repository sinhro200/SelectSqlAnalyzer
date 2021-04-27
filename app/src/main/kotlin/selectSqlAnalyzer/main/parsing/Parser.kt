package selectSqlAnalyzer.main.parsing

import selectSqlAnalyzer.main.ParseResult
import selectSqlAnalyzer.main.core.Context
import selectSqlAnalyzer.main.core.Field
import selectSqlAnalyzer.main.core.IField
import selectSqlAnalyzer.main.core.ITable

class Parser(
        query: String,
        parentContext: Context?,
        startPos: Int = 0
) {
    val queryPartsHelper = QueryPartsHelper(query).also { it.parseParts() }
    val context = Context(parentContext)
    fun parse(): ParseResult {
        val table = parseFrom()
        val fields = parseSelect()
        return ParseResult(
                fields,
                table
        )
    }

    private fun parseFrom(): ITable? {
        if (queryPartsHelper.fromString.isEmpty())
            return null

        val ph = ParseHelper(queryPartsHelper.fromString)
        ph.clearSpaces()
        ph.parse("from")
        ph.clearSpaces()

        if (ph.isParse('(')) {
            ph.move()
            val closeBracketPos =
                    wordPositionFindBracketSkip(
                            queryPartsHelper.fromString, ")", ph.pos
                    )
            val innerQueryParser = Parser(ph.text.substring(ph.pos, closeBracketPos), context)
            val parsedTable = innerQueryParser.parse()
            ph.pos = closeBracketPos
            ph.move()
            ph.clearSpaces()
            ph.parse("as")
            ph.clearSpaces()
            val asTableName = ph.parseTableName()
            context.tables[asTableName] = parsedTable
            return parsedTable
        } else {
            val tableName = ph.parseTableName()
            val table = context.find(tableName)
            if (table != null) {
                context.tables[tableName] = table
                return table
            }
            throw ParsingException("Cant find table with name $tableName")
        }
    }

    private fun parseSelect(): List<IField> {
        val res = mutableListOf<IField>()
        val ph = ParseHelper(queryPartsHelper.selectString)
        ph.parse("select")
        while (ph.pos < queryPartsHelper.selectString.length - 1) {
            ph.clearSpaces()
            if (ph.isParse("(")) {
                ph.move()
                ph.clearSpaces()
                val closeBracketPos =
                        wordPositionFindBracketSkip(
                                queryPartsHelper.selectString, ")", ph.pos
                        )
                val innerQueryParser = Parser(ph.text.substring(ph.pos, closeBracketPos), context)
                val parsedTable = innerQueryParser.parse()
                ph.pos = closeBracketPos
                res.add(parsedTable)
//                if (parsedTable.fields.size > 1)
//                    throw ParsingException("Expected one field", ph.pos)
//                val parsedTableField = parsedTable.fields[0]
//                if (!parsedTableField.isStatic)
//                    throw ParsingException("Expected static value, not field name", ph.pos)

//                res.add(Field(parsedTableField.str, true))

                ph.pos = closeBracketPos
                if (ph.isParse(")"))
                    ph.move()
            } else if (ph.isParse('\"')) {
                ph.move()
                val fieldName = ph.parseFieldName()
                ph.parse('\"')
                res.add(Field(fieldName, true))
            } else if (ph.isParse('*')) {
                res.add(Field.ALL)
                ph.move()
            } else {
                val fieldValue = ph.parseFieldValue()
                res.add(Field(fieldValue, false))
            }
            ph.clearSpaces()
            if (ph.isParse(','))
                ph.move()
        }
        return res
    }














}