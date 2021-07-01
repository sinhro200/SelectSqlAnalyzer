package selectSqlAnalyzer.main.core

import selectSqlAnalyzer.main.ast.nodes.*
import java.lang.RuntimeException
import java.util.*

class Context(
        val parentContext: Context? = null
) {

    var resultFields = LinkedList<Pair<String, FieldType>>()
    var resultData = LinkedList<List<Pair<String, FieldType>>>()

    private val tables = mutableMapOf<String, ITable>()

    fun copyOnlyTables(): Context {
        val n = Context(parentContext)
        for (t in tables)
            n.addTable(t.value)
        return n
    }
//    private val tables = mutableMapOf<String, Pair<ITable, Int>>()
//
//    fun addTable(table: ITable, tableName: String) {
//        tables[tableName] = Pair(table, 0)
//    }
//    fun execute(parseResult: SelectAstNode){
//        //todo
//    }


    fun indexOfField(fName: String): Int {
        return resultFields.indexOfFirst { it.first == fName }
    }

    fun valueOfFieldInRow(fName: String, row: List<Pair<String, FieldType>>): Pair<String, FieldType> {
        val i = indexOfField(fName)
        if (i < 0)
            throw RuntimeException("Cant find field $fName")
        return row[i]
    }

    fun addTable(table: ITable, tableName: String) {
        tables[tableName] = table
    }

    fun addTable(table: ITable) {
        tables[table.name()] = table
    }

    fun execute(parseResult: SelectAstNode) {
        resultData.clear()
        resultFields.clear()
        val tname = processTableName(parseResult)
        val table = tables[tname]
                ?: throw RuntimeException("Cant find table $tname in context")
        processWhere(parseResult, table)
        resultFields.clear()
        resultFields.addAll(table.fields())
        processOrdering(parseResult)
        processFields(parseResult, table)


        val sb = StringBuilder()
        resultFields.forEach {
            val (name, type) = it
            sb.append(String.format("%10s", type.toString()))
        }
        sb.append('\n')
        resultFields.forEach {
            val (name, type) = it
            sb.append(String.format("%10s", name))
        }
        sb.append('\n')
        resultFields.forEach {
            sb.append(String.format("%10s", "__________"))
        }
        sb.append('\n')
        val indices = mutableListOf<Int>()
        for (resField in resultFields) {
            val ind = table.fields().indexOfFirst { it.first == resField.first }
            if (ind == -1) {

            } else {
                indices.add(ind)
            }
        }
        resultData.forEach { row ->
//            for (i in row.indices) {
//                if (i in indices) {
//                    val (value, type) = row[i]
//                    sb.append(String.format("%10s", value))
//                }
//            }
            row.forEach {
                val (value, type) = it
                sb.append(String.format("%10s", value))
            }
            sb.append('\n')
        }
        println("-- -- -- -- --")
        println(sb.toString())
    }

    private fun processTableName(parseResult: SelectAstNode): String {
//        if (parseResult.from == null) {
//            //Нету таблицы во from
//            parseResult.fieldAstNodes.forEach {
//                when (it) {
//                    is IdentFieldAstNode -> throw RuntimeException("There is no table to get $it from")
//                    is LiteralFieldAstNode -> resultFields.add(Pair(it.fieldValue, UndefFT))
//                    is AllIdentsFieldAstNode -> throw RuntimeException("There is no table to get all(*) from")
//                }
//            }
//        } else {
        when (val tableAstNode = parseResult.from) {
            is NamedTableAstNode -> {
                if (tableAstNode.isSelect) {
                    throw RuntimeException("select in 'from' region not supported now")
                    //создать таблицу, добавить её в этот контекст и вернуть её имя
                    //todo
                } else {
//                        val selectLines = processWhere(tables[tableAstNode.tableName]
//                                ?: throw RuntimeException("Cant find table ${tableAstNode.tableName} in context"))
//                        resultData.addAll(selectLines)
                    return tableAstNode.tableName
                }
            }
            else -> {
                throw RuntimeException("joins not supported now")
                //создать таблицу, добавить её в этот контекст и вернуть её имя
                /**
                 * todo
                 * need to handle:
                 *      CrossJoinTableAstNode
                 *      FullOuterJoinTableAstNode
                 *      RightOuterJoinTableAstNode
                 *      LeftOuterJoinTableAstNode
                 *      InnerJoinTableAstNode
                 */
            }
        }
//        }
    }

    private fun processWhere(parseResult: SelectAstNode, fromTable: ITable) {
        if (parseResult.where != null)
            fromTable.data().forEach { row ->
                if (processWhereInner(parseResult.where, row, fromTable)) {
                    val rowAsList = mutableListOf<Pair<String, FieldType>>()
                    for (ind in row.indices) {
                        rowAsList.add(Pair(row[ind], fromTable.fields()[ind].second))
                    }
                    resultData.add(rowAsList)
                }

            }
        else {
            resultData.clear()
            //добавим все значения
            resultData.addAll(fromTable.data().map { tRaw ->
                val rowAsList = mutableListOf<Pair<String, FieldType>>()
                for (ind in tRaw.indices) {
                    rowAsList.add(Pair(tRaw[ind], fromTable.fields()[ind].second))
                }
                return@map rowAsList
            }.toCollection(LinkedList<List<Pair<String, FieldType>>>()))
        }
    }

    private fun processWhereInner(parentWhereAstNode: WhereAstNode, row: TRow, fromTable: ITable): Boolean {
        when (parentWhereAstNode) {
            is SimpleWhereAstNode -> {
                return isOkUsingCondition(parentWhereAstNode.condition, row)
            }
            is OrWhereAstNode -> {
                for (whereAstNode in parentWhereAstNode.nodes) {
                    if (processWhereInner(whereAstNode, row, fromTable))
                        return true
                }
                return false
            }
            is AndWhereAstNode -> {
                for (whereAstNode in parentWhereAstNode.nodes) {
                    if (!processWhereInner(whereAstNode, row, fromTable))
                        return false
                }
                return true
            }
        }
    }

    private fun isOkUsingCondition(condition: Condition, row: TRow): Boolean {
        when (condition.f1) {
            is IdentFieldAstNode -> {
                val (value1, fType1) = row.valueByFieldName(condition.f1.fieldName)

                when (condition.f2) {
                    is IdentFieldAstNode -> {
                        val (value2, fType2) = row.valueByFieldName(condition.f2.fieldName)
                        return compareIsOk(value1, fType1, value2, fType2, condition.conditionOp)
                    }
                    is LiteralFieldAstNode -> {
                        val value2 = condition.f2.fieldValue
                        var fType2: FieldType = StringFT
                        value2.toFloatOrNull()?.let {
                            fType2 = FloatFT
                        }
                        value2.toIntOrNull()?.let {
                            fType2 = IntFT
                        }

                        return compareIsOk(value1, fType1, value2, fType2, condition.conditionOp)
                    }
                    else -> {
                        throw RuntimeException("unsupported field node ${condition.f1}")
                    }
                }
            }
            is LiteralFieldAstNode -> {
                val value1 = condition.f1.fieldValue
                var fType1: FieldType = StringFT
                value1.toFloatOrNull()?.let {
                    fType1 = FloatFT
                }
                value1.toIntOrNull()?.let {
                    fType1 = IntFT
                }


                when (condition.f2) {
                    is IdentFieldAstNode -> {
                        val (value2, fType2) = row.valueByFieldName(condition.f2.fieldName)
                        return compareIsOk(value1, fType1, value2, fType2, condition.conditionOp)
                    }
                    is LiteralFieldAstNode -> {
                        val value2 = condition.f2.fieldValue
                        var fType2: FieldType = StringFT
                        value2.toFloatOrNull()?.let {
                            fType2 = FloatFT
                        }
                        value2.toIntOrNull()?.let {
                            fType2 = IntFT
                        }

                        return compareIsOk(value1, fType1, value2, fType2, condition.conditionOp)
                    }
                    else -> {
                        throw RuntimeException("unsupported field node ${condition.f1}")
                    }
                }
            }
            else -> {
                throw RuntimeException("unsupported field node ${condition.f1}")
            }
        }
    }

    private fun compareIsOk(val1: String, type1: FieldType, val2: String, type2: FieldType, conditionOp: Condition.ConditionOp): Boolean {
        if (type1 != type2)
            throw RuntimeException("Cant compare values with different types. $val1($type1) $val2($type2)")
        when (type1) {
            is FloatFT -> {
                val v1: Float = type1.fromString(val1)
                val v2: Float = type1.fromString(val2)
                when (conditionOp) {
                    Condition.ConditionOp.GT -> {
                        return v1 > v2
                    }
                    Condition.ConditionOp.EQ -> {
                        return v1 == v2
                    }
                    Condition.ConditionOp.GTE -> {
                        return v1 >= v2
                    }
                    Condition.ConditionOp.LT -> {
                        return v1 < v2
                    }
                    Condition.ConditionOp.LTE -> {
                        return v1 <= v2
                    }
                }
            }
            is IntFT -> {
                val v1: Int = type1.fromString(val1)
                val v2: Int = type1.fromString(val2)
                when (conditionOp) {
                    Condition.ConditionOp.GT -> {
                        return v1 > v2
                    }
                    Condition.ConditionOp.EQ -> {
                        return v1 == v2
                    }
                    Condition.ConditionOp.GTE -> {
                        return v1 >= v2
                    }
                    Condition.ConditionOp.LT -> {
                        return v1 < v2
                    }
                    Condition.ConditionOp.LTE -> {
                        return v1 <= v2
                    }
                }
            }

            is StringFT -> {
                val v1: String = type1.fromString(val1)
                val v2: String = type1.fromString(val2)
                when (conditionOp) {
                    Condition.ConditionOp.GT -> {
                        return v1 > v2
                    }
                    Condition.ConditionOp.EQ -> {
                        return v1 == v2
                    }
                    Condition.ConditionOp.GTE -> {
                        return v1 >= v2
                    }
                    Condition.ConditionOp.LT -> {
                        return v1 < v2
                    }
                    Condition.ConditionOp.LTE -> {
                        return v1 <= v2
                    }
                }
            }
            is UndefFT -> {
                throw RuntimeException("Cant compare values with undefined type")
            }
        }
    }

    fun processFields(parseResult: SelectAstNode, table: ITable) {

        resultFields.clear()
        resultFields.addAll(table.fields())
        if (parseResult.fieldAstNodes.size == 1 && parseResult.fieldAstNodes[0] is AllIdentsFieldAstNode) {

        } else {
            val resData = LinkedList<List<Pair<String, FieldType>>>()
            for (row in resultData) {
                val newRow = mutableListOf<Pair<String, FieldType>>()
                for (fieldNode in parseResult.fieldAstNodes) {
                    when (fieldNode) {
                        is IdentFieldAstNode -> {
                            newRow.add(valueOfFieldInRow(fieldNode.fieldName, row))
                        }
                        is LiteralFieldAstNode -> {
                            newRow.add(Pair(fieldNode.fieldValue, UndefFT))
                        }
                        is AllIdentsFieldAstNode -> {
                            throw RuntimeException("Cant select ALL fields with specific field")
                        }
                    }
                }
                resData.add(newRow)
            }
            resultData.clear()
            resultData.addAll(resData)

            resultFields.clear()
            for (fieldNode in parseResult.fieldAstNodes) {
                when (fieldNode) {
                    is IdentFieldAstNode -> {
                        val fieldFromTable = table.fields().find { it.first == fieldNode.fieldName }
                                ?: throw RuntimeException("Cant find field with name ${fieldNode.fieldName}")
                        resultFields.add(fieldFromTable)
                    }
                    is LiteralFieldAstNode -> {
                        resultFields.add(Pair(fieldNode.fieldValue, StringFT))
                    }
                    is AllIdentsFieldAstNode -> {
                        throw RuntimeException("Cant select ALL fields with specific field")
                    }
                }
            }
        }
    }

    fun processOrdering(parseResult: SelectAstNode) {
        parseResult.orderBy?.let { ordBy ->
            val sortedResultData = resultData.toList().sortedWith(
                    kotlin.Comparator { r1: List<Pair<String, FieldType>>,
                                        r2: List<Pair<String, FieldType>> ->
                        for (f in ordBy.fields) {
                            when (val field = f.first) {
                                is IdentFieldAstNode -> {
                                    val value1 = valueOfFieldInRow(field.fieldName, r1)
                                    val value2 = valueOfFieldInRow(field.fieldName, r2)
                                    val compareResult = compareFieldsWithTypes(value1, value2)
                                    if (compareResult == 0)
                                        continue
                                    when (f.second) {
                                        OrderByAstNode.OrderByParam.DESC -> {
                                            return@Comparator -compareResult
                                        }
                                        OrderByAstNode.OrderByParam.ASC -> {
                                            return@Comparator compareResult
                                        }
                                    }
                                }
                                is LiteralFieldAstNode -> {

                                }
                                is AllIdentsFieldAstNode -> {

                                }
                            }
                        }
                        return@Comparator 0
                    }
            )
            resultData.clear()
            resultData.addAll(sortedResultData)
        }
    }

}