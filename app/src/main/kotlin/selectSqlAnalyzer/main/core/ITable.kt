package selectSqlAnalyzer.main.core

import selectSqlAnalyzer.main.ast.nodes.Condition
import java.util.*

interface ITable {
    fun name(): String
    fun fields(): List<Pair<String, FieldType>>
    fun data(): LinkedList<TRow>
}

public class TRow : LinkedList<String>() {
    lateinit var table: ITable
    fun valueByFieldName(fieldName: String): Pair<String, FieldType> {
        val fName =
                if (fieldName.contains('.'))
                    fieldName.substringAfter('.')
                else
                    fieldName
        val fields = table.fields()
        val index = fields.indexOfFirst { it.first == fName }

        if (index < 0)
            throw RuntimeException("Cant find field with name $fieldName on table ${table.name()}")

        return Pair(this[index], fields[index].second)
    }

    private fun fullFieldName(fieldName: String): String {
        if (!fieldName.contains('.'))
            return table.name() + "." + fieldName
        val beforePoint = fieldName.substringBefore('.')
        if (beforePoint == table.name())
            return fieldName
        else
            throw RuntimeException("Cant find field with name: $fieldName on table: ${table.name()}")
    }
}

sealed class FieldType

object StringFT : FieldType() {
    fun fromString(str: String) = str.trim()
    override fun toString(): String {
        return this::class.java.simpleName
    }
}

object FloatFT : FieldType() {
    fun fromString(str: String): Float {
        return str.trim().toFloatOrNull()
                ?: throw RuntimeException("Cant convert value $str to float", )
    }

    override fun toString(): String {
        return this::class.java.simpleName
    }
}

object IntFT : FieldType() {
    fun fromString(str: String): Int {
        return str.trim().toIntOrNull() ?: throw RuntimeException("Cant convert value $str to int")
    }

    override fun toString(): String {
        return this::class.java.simpleName
    }
}

object UndefFT : FieldType() {
    fun fromString(obj: Any) = obj.toString().trim()
}

fun compareFieldsWithTypes(p1: Pair<String, FieldType>, p2: Pair<String, FieldType>): Int {
    val (val1, type1) = p1
    val (val2, type2) = p2
    return compareFieldsWithTypes(val1, type1, val2, type2)
}

fun compareFieldsWithTypes(val1: String, type1: FieldType, val2: String, type2: FieldType): Int {
    if (type1 != type2)
        throw java.lang.RuntimeException("Cant compare values with different types. $val1($type1) $val2($type2)")
    when (type1) {
        is FloatFT -> {
            val v1: Float = type1.fromString(val1)
            val v2: Float = type1.fromString(val2)
            return v1.compareTo(v2)
        }
        is IntFT -> {
            val v1: Int = type1.fromString(val1)
            val v2: Int = type1.fromString(val2)
            return v1.compareTo(v2)
        }
        is StringFT -> {
            val v1: String = val1
            val v2: String = val2
            return v1.compareTo(v2)
        }
        is UndefFT -> {
            throw java.lang.RuntimeException("Cant compare values with undefined types")
        }
    }
}