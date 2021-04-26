package selectSqlAnalyzer.main.parser

object QueryOperatorWriter {
    fun write(quop: OperatorQuery) {
        write(quop, 1)
    }

    fun write(quop: OperatorQuery, level: Int = 0) {
        LevelWriter.apply {
            println("select",level)
            for (selectParam in quop.operatorSelect.paramSelects) {
                println("      -${if (selectParam.asValue) selectParam.data else "\"${selectParam.data}\""}",level)
            }
            println("  from",level)
            println("      -${quop.operatorFrom?.paramFrom?.tableName}",level)
            println(" where",level)
            println("      -${quop.operatorWhere}",level)
            println("-  -  -  -  -  -  -  -",level)
        }
    }

    object LevelWriter {
        private val levelStrMap = mutableMapOf<Int, String>()
        fun getPrefix(level: Int): String {
            levelStrMap.get(level)?.let {
                return it
            }
            val sb = StringBuffer()
            repeat(level) {
                sb.append("-")
            }
            sb.toString().let {
                levelStrMap[level] = it
                return it
            }
        }

        fun println(text: String,level: Int) {
            println(getPrefix(level) + text)
        }
    }

}