package selectSqlAnalyzer.main.core

class SelectParam(
        val data: String,
        val isVar: Boolean = true,
){
    override fun toString(): String {
        return "SelectParam(data='$data', isVar=$isVar)"
    }

    fun toPrettyString(): String {
        if (!isVar)
            return "[$data]"
        return data
    }
}