package selectSqlAnalyzer.main.parser

class SelectParam(
        val data: String,
        val asValue: Boolean = false,
){
    override fun toString(): String {
        return "SelectParam(data='$data', asValue=$asValue)"
    }
}

class SelectOperator(
        val params: List<SelectParam>
)