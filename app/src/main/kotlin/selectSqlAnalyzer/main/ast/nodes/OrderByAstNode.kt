package selectSqlAnalyzer.main.ast.nodes

class OrderByAstNode(
        val fields: List<Pair<FieldAstNode, OrderByParam>>
) {
    enum class OrderByParam {
        DESC,
        ASC
    }
}