package selectSqlAnalyzer.main.ast.nodes

open class FieldAstNode

class IdentFieldAstNode(val fieldName: String) : FieldAstNode()

class LiteralFieldAstNode(val fieldValue: String) : FieldAstNode()

class AllIdentsFieldAstNode : FieldAstNode()