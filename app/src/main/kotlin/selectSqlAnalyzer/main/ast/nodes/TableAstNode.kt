package selectSqlAnalyzer.main.ast.nodes

sealed class TableAstNode

class NamedTableAstNode(
        val tableName: String
) : TableAstNode()

sealed class JoinTableAstNode(
        val t1: TableAstNode,
        val t2: TableAstNode,
) : TableAstNode()

/**
 *  a inner join b on true
 */
class InnerJoinTableAstNode(
        t1: TableAstNode,
        t2: TableAstNode,
        val f1: FieldAstNode,
        val f2: FieldAstNode,
) : JoinTableAstNode(t1,t2)

/**
 * Left outer join
 */
class LeftOuterJoinTableAstNode(
        t1: TableAstNode,
        t2: TableAstNode,
        val f1: FieldAstNode,
        val f2: FieldAstNode,
) : JoinTableAstNode(t1,t2)

/**
 * Right outer join
 */
class RightOuterJoinTableAstNode(
        t1: TableAstNode,
        t2: TableAstNode,
        val f1: FieldAstNode,
        val f2: FieldAstNode,
) : JoinTableAstNode(t1,t2)

/**
 * Full outer join
 */
class FullOuterJoinTableAstNode(
        t1: TableAstNode,
        t2: TableAstNode,
        val f1: FieldAstNode,
        val f2: FieldAstNode,
) : JoinTableAstNode(t1,t2)

/**
 * Cross join examples:
 *      a, b
 *      a cross join b
 */
class CrossJoinTableAstNode(
        t1: TableAstNode,
        t2: TableAstNode,
) : JoinTableAstNode(t1,t2)