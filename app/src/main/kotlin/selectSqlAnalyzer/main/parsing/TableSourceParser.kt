package selectSqlAnalyzer.main.parsing

import selectSqlAnalyzer.main.core.*
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*
import kotlin.jvm.Throws

/**
 * table_name
 *
 * field_type field_type field_type ...
 * field_name field_name field_name ...
 *
 * data data data ...
 * data data data ...
 * data data data ...
 * data data data ...
 * data data data ...
 * data data data ...
 * ...
 * ---
 * table_name
 *
 * field_type field_type field_type ...
 * field_name field_name field_name ...
 *
 * data data data ...
 * data data data ...
 * data data data ...
 * data data data ...
 * data data data ...
 * data data data ...
 */
object TableSourceParser {
    fun parseFromFile(fileName: String): List<ITable> {
        val sc = Scanner(wholeFileText(fileName))
        val res = LinkedList<ITable>()
        while (sc.hasNextLine()) {
            var nl = sc.nextLine()
            while (nl.isEmpty())
                nl = sc.nextLine()

            val tname = nl.trim()

            nl = sc.nextLine()
            while (nl.isEmpty())
                nl = sc.nextLine()

            val ftypes = nl.trim()
            nl = sc.nextLine()
            val fnames = nl.trim()
            nl = sc.nextLine()

            val fields = createFields(ftypes, fnames)

            val data = LinkedList<TRow>()
            nl = sc.nextLine()
            while (nl.isEmpty())
                nl = sc.nextLine()

            while (nl.trim() != "---") {

                val row = strToTRow(nl.trim(), fields)
                data.add(row)
                nl = sc.nextLine()
                while (nl.isEmpty())
                    nl = sc.nextLine()
            }


            res.add(Table(tname, fields, data))
        }

        return res
    }

    fun strToTRow(str: String, fields: List<Pair<String, FieldType>>): TRow {
        val cellValues = str.split(' ')
        if (cellValues.size < fields.size)
            throw RuntimeException("number of cell values less than number of fields")
        if (cellValues.size > fields.size)
            throw RuntimeException("number of cell values more than number of fields")
        val row = TRow()
        cellValues.map { it.trim() }
                .forEach { row.add(it) }
        return row
    }

    fun createFields(ftypes: String, fnames: String): List<Pair<String, FieldType>> {
        val types = ftypes.split(' ')
        val names = fnames.split(' ')
        if (types.size != names.size)
            throw RuntimeException("Err while parsing. number of types: ${types.size} != number of names ${names.size}")

        val res = LinkedList<Pair<String, FieldType>>()
        for (i in types.indices)
            res.add(Pair(names[i], strToFieldType(types[i])))
        return res
    }

    private fun strToFieldType(str: String): FieldType {
        return when (str) {
            "int" -> IntFT
            "float" -> FloatFT
            "str" -> StringFT
            else -> throw RuntimeException("Cant parse $str as fieldType")
        }
    }

    @Throws(FileNotFoundException::class)
    private fun wholeFileText(filename: String): String {
        javaClass.classLoader.getResource(filename)?.file?.let {
            with(FileReader(it)) {
                val text = readText()
                close()
                return text
            }
        } ?: throw FileNotFoundException(filename)
    }
}