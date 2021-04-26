package selectSqlAnalyzer.main.parser

class OperatorQuery (
        val operatorSelect: OperatorSelect,
        val operatorFrom: OperatorFrom?,
        val operatorWhere: OperatorWhere?
)