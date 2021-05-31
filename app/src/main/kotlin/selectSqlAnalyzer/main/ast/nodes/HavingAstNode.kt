package selectSqlAnalyzer.main.ast.nodes

class HavingAstNode(
        f1: FieldAstNode,
        where: Condition.ConditionOp,
        f2: FieldAstNode
)