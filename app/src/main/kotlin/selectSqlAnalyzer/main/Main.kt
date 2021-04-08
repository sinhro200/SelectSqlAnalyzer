package selectSqlAnalyzer.main

import com.google.common.io.Resources
import selectSqlAnalyzer.main.parser.Parser
import java.io.FileNotFoundException
import java.io.FileReader
import java.lang.StringBuilder
import java.nio.charset.Charset

class Main {
    companion object {
        val selectFile = "sqlselect.txt"

        @JvmStatic
        fun main(args: Array<String>) {
            javaClass.classLoader.getResource(selectFile)?.file?.let {
                with(FileReader(it)) {
                    beginWith(readText())
                    close()
                }
            } ?: throw FileNotFoundException(selectFile)
        }

        fun beginWith(selectText: String) {
            var remainString = selectText
            var curQuery = remainString.substringBefore(';', "")
            while (curQuery.isNotEmpty()) {
                val p = Parser(curQuery)
                val sop = p.selectOperator()
                println(sop.params.joinToString(separator = ", "))

                remainString = remainString.substringAfter(';', "")
                curQuery = remainString.substringBefore(';', "")
            }

        }
    }
}
