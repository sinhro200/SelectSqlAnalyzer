package selectSqlAnalyzer.main.parser

class ParamFrom(
        val tableName: String
){
    override fun toString(): String {
        return "ParamFrom(tableName='$tableName')"
    }
}