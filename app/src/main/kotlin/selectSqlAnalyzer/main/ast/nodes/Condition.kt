package selectSqlAnalyzer.main.ast.nodes

sealed class WhereAstNode

class SimpleWhereAstNode(
        val condition: Condition
) : WhereAstNode()

class AndWhereAstNode(
        val whereAstNode1: WhereAstNode,
        val whereAstNode2: WhereAstNode
) : WhereAstNode()

class OrWhereAstNode(
        val whereAstNode1: WhereAstNode,
        val whereAstNode2: WhereAstNode
) : WhereAstNode()

class Condition(
        val f1: FieldAstNode,
        val conditionOp: ConditionOp,
        val f2: FieldAstNode
) {
    enum class ConditionOp(
            val string: String
    ) {
        GT(">"),
        LT("<"),
        GTE(">="),
        LTE("<="),
        EQ("="),
        ;

        companion object {
            fun fromStr(str: String): ConditionOp? {
                return values().firstOrNull { it.string == str }
            }
        }
    }
}