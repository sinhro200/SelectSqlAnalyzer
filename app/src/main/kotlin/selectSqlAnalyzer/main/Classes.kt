package selectSqlAnalyzer.main

import selectSqlAnalyzer.main.core.ParsingException
import java.lang.instrument.ClassDefinition
import java.text.ParseException

class Field(
        val name: String,
        val type: Class<Any> = String.javaClass
)

class Entry(
        val values: MutableMap<String, String> = mutableMapOf()
)

open class Table(
        val name: String,
        val fields: MutableList<Field>,
        val data: MutableMap<Field, List<Any>> = mutableMapOf()
) {
    fun asValue(): Any? {
        if (fields.size > 1)
            throw ParsingException("Table contains more than 1 field, but must 1")
        if (fields.size == 0)
            throw ParsingException("Table contains no fields, but must 1")
        val datas = data[fields[0]]
        if (datas == null || datas.size == 0 )
            return null
        if (datas.size > 1)
            throw ParsingException("Table contains more than 1 entry")
        return datas[0]
    }
}

class Context : Table {
    val parentContext: Context?
    val tables: MutableList<Table> = mutableListOf()

    constructor(
            name: String,
            parentContext: Context? = null
    ) : super(name, mutableListOf()) {
        this.parentContext = parentContext
    }


    companion object {
        fun Global(): Context {
            return Context("Global").also { fillDefault(it) }
        }

        fun fillDefault(context: Context) {
            context.tables.apply {
                add(Table(
                        "tname",
                        mutableListOf(
                                Field("a"),
                                Field("b"),
                                Field("c"),
                        ),
                ))
                add(Table(
                        "t",
                        mutableListOf(
                                Field("d"),
                                Field("e"),
                        ),
                ))
            }
        }
    }
}