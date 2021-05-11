package selectSqlAnalyzer.main.ast.nodes

class WhereAstNode(
        f1: FieldAstNode,
        condition: Condition,
        f2: FieldAstNode
) {
    enum class Condition(
            val string: String
    ) {
        GT(">"),
        LT("<"),
        GTE(">="),
        LTE("<="),
        EQ("="),
    }
}