package selectSqlAnalyzer.main.ast.nodes

import selectSqlAnalyzer.main.core.Context
import selectSqlAnalyzer.main.parsing.ParsingException
import kotlin.jvm.Throws

class SelectAstNode(
        val fieldAstNodes: List<FieldAstNode>,
        val from: TableAstNode?,
        val where: Any? = null,
        val groupBy: Any? = null,
        val having: Any? = null,
        val orderBy: Any? = null,
) : FieldAstNode() {
    @Throws()
    fun asLiteralFieldAstNode(): LiteralFieldAstNode {
        if (fieldAstNodes.isEmpty())
            throw ParsingException("Подзапрос должен вернуть один столбец")
        if (fieldAstNodes.size > 1)
            throw ParsingException("Подзапрос должен вернуть только один столбец")

        //todo
        return LiteralFieldAstNode(
                "{must be calculated}"
        )
    }

    fun asTableAstNode(tableName: String): TableAstNode {
        return NamedTableAstNode(tableName)
    }

    fun execute(context: Context) {

    }
}