package selectSqlAnalyzer.main.parser

class ParamSelect(
        val data: String,
        val asValue: Boolean = false,
){
    override fun toString(): String {
        return "SelectParam(data='$data', asValue=$asValue)"
    }
}