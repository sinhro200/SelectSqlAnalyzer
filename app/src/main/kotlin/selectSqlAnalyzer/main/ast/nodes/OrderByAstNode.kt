package selectSqlAnalyzer.main.ast.nodes

class OrderByAstNode(
        val fields: List<Pair<FieldAstNode, OrderByParam>>
) {
    enum class OrderByParam {
        //по убыванию
        DESC,
        //по возрастанию
        ASC
    }
}