package selectSqlAnalyzer.main.core

import java.util.*

class Table(
        val name: String,
        val fields: List<Pair<String, FieldType>>,
        val data: LinkedList<TRow>
) : ITable {
    init {
        data.forEach { it.table = this }
    }
    override fun name(): String {
        return name
    }

    override fun fields(): List<Pair<String, FieldType>> {
        return fields
    }

    override fun data(): LinkedList<TRow> {
        return data
    }

}