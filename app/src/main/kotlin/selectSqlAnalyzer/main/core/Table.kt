package selectSqlAnalyzer.main.core

class Table(
        var fields: List<String>,
        var data: List<List<String>> = listOf<List<String>>(listOf<String>())
) : ITable {

    override fun fields(): List<String> = fields

    override fun data(): List<List<String>> {
        return data
    }

}