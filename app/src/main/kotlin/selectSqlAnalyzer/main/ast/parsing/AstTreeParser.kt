package selectSqlAnalyzer.main.ast.parsing

import selectSqlAnalyzer.main.ast.nodes.*
import selectSqlAnalyzer.main.parsing.ParseHelper
import selectSqlAnalyzer.main.parsing.ParsingException
import selectSqlAnalyzer.main.parsing.QueryPartsHelper
import selectSqlAnalyzer.main.parsing.findWordPositionSkippingBrackets

class AstTreeParser(
        query: String,

        ) {
    private val queryPartsHelper = QueryPartsHelper(query).also { it.parseParts() }

    fun parse(): SelectAstNode {
        return SelectAstNode(
                parseSelect(),
                parseFrom(),
                parseWhere(),
                parseGroup(),
                parseHaving(),
                parseOrderByAstNode()
        )
    }

    private fun parseSelect(): List<FieldAstNode> {
        val res = mutableListOf<FieldAstNode>()
        with(ParseHelper(queryPartsHelper.selectString.toLowerCase())) {
            clearSpaces()
            parseStr("select")
            while (canMove()) {
                clearSpaces()
                if (isParse('(')) {
                    move()
                    clearSpaces()
                    val closeBracketPos =
                            findWordPositionSkippingBrackets(
                                    queryPartsHelper.selectString, ")", pos
                            )
                    val innerQueryParser = AstTreeParser(text.substring(pos, closeBracketPos))
                    val innerSelectAstNode = innerQueryParser.parse()
                    res.add(innerSelectAstNode)
                    pos = closeBracketPos
                    if (isParse(')'))
                        move()
                } else if (isParse('\"')) {
                    //todo use next method. parseFieldLiteralOrIdent()
                    move()
                    val fieldValue = parseUntil('\"')
                    parseStr("\"")
                    res.add(LiteralFieldAstNode(fieldValue))
                } else if (isParse('*')) {
                    move()
                    res.add(AllIdentsFieldAstNode())
                } else {
                    val fieldString = parseFieldString()
                    if (fieldString.toIntOrNull() != null)
                        res.add(LiteralFieldAstNode(fieldString))
                    else
                        res.add(IdentFieldAstNode(fieldString))
                }
                clearSpaces()
                if (isParse(','))
                    move()
            }
        }
        return res
    }

    private fun ParseHelper.parseFieldLiteralOrIdent(
            callbackOnLiteral: (fieldValue: String) -> Unit,
            callbackOnIdent: (fieldName: String) -> Unit,
    ) {
        if (isParse('\"')) {
            move()
            val fieldValue = parseUntil('\"')
            parseStr("\"")
            callbackOnLiteral.invoke(fieldValue)
        } else {
            val fieldString = parseFieldString()
            if (fieldString.toIntOrNull() != null)
                callbackOnLiteral.invoke(fieldString)
            else
                callbackOnIdent.invoke(fieldString)
        }
    }

    private fun parseFrom(): TableAstNode? {
        if (queryPartsHelper.fromString.isEmpty())
            return null

        var resultTableAstNode: TableAstNode? = null
        with(ParseHelper(queryPartsHelper.fromString.toLowerCase())) {
            clearSpaces()
            parseStr("from")
            val joins = listOf<String>(
                    "inner join",
                    "left join",
                    "right join",
                    "full join",
                    "cross join",
                    ","
            )
            while (canMove()) {
                clearSpaces()
                if (isParseStr(joins)) {
                    val joinStr = parseStr(joins)
                    clearSpaces()
                    val tName = parseTableName()
                    if (listOf("cross join", ",").contains(joinStr)) {
                        resultTableAstNode = CrossJoinTableAstNode(
                                resultTableAstNode ?: throw ParsingException("Cant join"),
                                NamedTableAstNode(tName))
                    } else {
                        clearSpaces()
                        parseStr("on")
                        clearSpaces()
                        val f1 = IdentFieldAstNode(parseFieldString())
                        clearSpaces()
                        parseStr("=")
                        clearSpaces()
                        val f2 = IdentFieldAstNode(parseFieldString())
                        resultTableAstNode = when (joinStr) {
                            "inner join" -> InnerJoinTableAstNode(
                                    resultTableAstNode ?: throw ParsingException("Cant join"),
                                    NamedTableAstNode(tName),
                                    f1, f2)
                            "left join" -> LeftOuterJoinTableAstNode(
                                    resultTableAstNode ?: throw ParsingException("Cant join"),
                                    NamedTableAstNode(tName),
                                    f1, f2)
                            "right join" -> RightOuterJoinTableAstNode(
                                    resultTableAstNode ?: throw ParsingException("Cant join"),
                                    NamedTableAstNode(tName),
                                    f1, f2)
                            "full join" -> FullOuterJoinTableAstNode(
                                    resultTableAstNode ?: throw ParsingException("Cant join"),
                                    NamedTableAstNode(tName),
                                    f1, f2)
                            else -> throw ParsingException("Unexpected err in join")
                        }
                    }
                } else if (isParse('(')) {
                    move()
                    val closeBracketPos =
                            findWordPositionSkippingBrackets(
                                    queryPartsHelper.fromString, ")", pos
                            )
                    val innerQueryParser = AstTreeParser(text.substring(pos, closeBracketPos))
                    val parsedAstTreeNode = innerQueryParser.parse()
                    pos = closeBracketPos
                    move()
                    clearSpaces()
                    try {
                        parseStr("as")
                    } catch (e: Exception) {
                        throw ParsingException("Таблица должна иметь алиас")
                    }

                    clearSpaces()
                    val asTableName = parseTableName()
                    if (asTableName.isEmpty())
                        throw ParsingException("Таблица должна иметь алиас")

                    //todo
                    // add {parsedAstTreeNode} to context
                    //using name {asTableName}

                    resultTableAstNode = parsedAstTreeNode.asTableAstNode(asTableName)
                } else {
                    val tName = parseTableName()
                    resultTableAstNode = NamedTableAstNode(tName)
                }
                clearSpaces()
            }
        }
        return resultTableAstNode
    }

    private fun ParseHelper.parseWhereCondition(): Condition {
        clearSpaces()
        var field: FieldAstNode? = null
        parseFieldLiteralOrIdent(
                { literalStr ->
                    field = LiteralFieldAstNode(literalStr)
                },
                { identStr ->
                    field = LiteralFieldAstNode(identStr)
                }
        )
        clearSpaces()
        val op = parseStr(Condition.ConditionOp.values().map { it.string })
        clearSpaces()
        var field2: FieldAstNode? = null
        parseFieldLiteralOrIdent(
                { literalStr ->
                    field2 = LiteralFieldAstNode(literalStr)
                },
                { identStr ->
                    field2 = LiteralFieldAstNode(identStr)
                }
        )

        return Condition(
                field
                        ?: throw ParsingException("Field 1 in cond cant be null"),
                Condition.ConditionOp.fromStr(op)
                        ?: throw ParsingException("Operation in cond cant be null"),
                field2
                        ?: throw ParsingException("Field 2 in cond cant be null")
        )
    }

    private fun parseWhere(): WhereAstNode? {
        if (queryPartsHelper.whereString.isEmpty())
            return null



        fun ParseHelper.parseWhereInner(): WhereAstNode {
            val astNodesWithOr = mutableListOf<WhereAstNode>()

            var astNodesWithAnd = mutableListOf<WhereAstNode>()

//            with(ParseHelper(whereString)) {
//                clearSpaces()
//                parseStr("where")
            clearSpaces()
            while (canMove()) {
                clearSpaces()



                val astNode = if (isParse('(')) {
                    move()
                    val innerAstNodeText = text.substring(this.pos).substringBefore(')')
                    move(innerAstNodeText.length+1)
                    clearSpaces()
                    with(ParseHelper(innerAstNodeText)){
                        parseWhereInner()
                    }
                } else {
                    val condition = parseWhereCondition()
                    clearSpaces()
                    SimpleWhereAstNode(condition)
                }

                var andOr: String? = null
                try {
                    andOr = parseStr("and", "or")
                    clearSpaces()
                } catch (ignored: java.lang.Exception) {
                }

                when (andOr) {
                    "and" -> {
                        astNodesWithAnd.add(astNode)
                    }
                    "or" -> {
                        if (astNodesWithAnd.isNotEmpty()) {
                            astNodesWithAnd.add(astNode)
                            astNodesWithOr.add(AndWhereAstNode(astNodesWithAnd.toList()))
                        } else {
                            astNodesWithOr.add(astNode)
                        }
                        astNodesWithAnd.clear()
                    }
                    else -> {
                        if (astNodesWithAnd.isNotEmpty()) {
                            astNodesWithAnd.add(astNode)
                            astNodesWithOr.add(AndWhereAstNode(astNodesWithAnd.toList()))
                        } else {
                            astNodesWithOr.add(astNode)
                        }
                    }
                }
                clearSpaces()

            }
            return if (astNodesWithOr.size == 1) astNodesWithOr[0] else OrWhereAstNode(astNodesWithOr.toList())
        }

        val str = queryPartsHelper.whereString.toLowerCase()
        with(ParseHelper(str)) {
            clearSpaces()
            parseStr("where")
            return parseWhereInner()
        }

//        return parseWhereInner(queryPartsHelper.whereString.toLowerCase())
    }

    private fun parseGroup(): GroupByAstNode? {
        if (queryPartsHelper.groupByString.isEmpty())
            return null
        //todo
        val ph = ParseHelper(queryPartsHelper.groupByString.toLowerCase())
        ph.clearSpaces()
        ph.parseStr("group by")
        while (ph.canMove()) {

        }
        return null
    }

    private fun parseHaving(): HavingAstNode? {
        if (queryPartsHelper.havingString.isEmpty())
            return null
        //todo
        val ph = ParseHelper(queryPartsHelper.groupByString.toLowerCase())
        ph.clearSpaces()
        ph.parseStr("having")
        while (ph.canMove()) {

        }
        return null
    }

    private fun parseOrderByAstNode(): OrderByAstNode? {
        if (queryPartsHelper.orderByString.isEmpty())
            return null
        //todo
        val ph = ParseHelper(queryPartsHelper.groupByString.toLowerCase())
        ph.clearSpaces()
        ph.parseStr("order by")
        while (ph.canMove()) {

        }
        return null
    }
}