package selectSqlAnalyzer.main.tools

import selectSqlAnalyzer.main.ast.nodes.*

object CustomLogger {
    const val ZoneDivider = "-- -- -- -- --"
    fun logQuery(query: String) {
        log(ZoneDivider)
        log(query)
        log(ZoneDivider)
    }

    fun log(text: String) {
        println(text)
    }

    fun bigDivider() {
        log("")
        log("")
    }

    fun logAstTree(san: SelectAstNode) {
        logAstTreeInner(san)
    }

    private fun logAstTreeInner(pr: SelectAstNode, level: Int = 0) {
        LevelLogger(level).apply {
            log("select")
            log("  >fields")
            for (f in pr.fieldAstNodes) {
                log("    -${f.toPrettyString()}")
            }
            log("  >tables")
            pr.from?.let {
                val tanl = TableAstNodeLogger(this)
                tanl.log(it)
                return
            }
            log("    -null")
        }
    }

    private class LevelLogger(
            protected val level: Int
    ) {
        fun log(text: String, forceLevel: Int = level) {
            for (i in 0..forceLevel)
                print("   ")
            println(text)
        }
    }

    private class TableAstNodeLogger(
            val levelLogger: LevelLogger
    ) {
        fun log(tableAstNode: TableAstNode) {
            logInner(tableAstNode)
        }

        fun logInnerSpaces(innerLevel: Int) {
            for (i in 0..innerLevel)
                print("  ")
        }

        private fun logInner(tableAstNode: TableAstNode, tableAstLevel: Int = 1) {
            with(levelLogger) {

                when (tableAstNode) {
                    is NamedTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("table")
                        logInnerSpaces(tableAstLevel)
                        log("- ${tableAstNode.tableName}")
                    }
                    is RightOuterJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("right outer join")

                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logInner(tableAstNode.t2, tableAstLevel + 1)

                        logInnerSpaces(tableAstLevel)
                        log("  on")
                        logInnerSpaces(tableAstLevel)
                        log("  - ${tableAstNode.f1.toPrettyString()}")
                        logInnerSpaces(tableAstLevel)
                        log("  - ${tableAstNode.f2.toPrettyString()}")
                    }
                    is LeftOuterJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("left outer join")

                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logInner(tableAstNode.t2, tableAstLevel + 1)

                        logInnerSpaces(tableAstLevel)
                        log("  on")
                        logInnerSpaces(tableAstLevel)
                        log("  - ${tableAstNode.f1.toPrettyString()}")
                        logInnerSpaces(tableAstLevel)
                        log("  - ${tableAstNode.f2.toPrettyString()}")
                    }
                    is InnerJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("inner join")

                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logInner(tableAstNode.t2, tableAstLevel + 1)

                        logInnerSpaces(tableAstLevel)
                        log("  on")
                        logInnerSpaces(tableAstLevel)
                        log("  - ${tableAstNode.f1.toPrettyString()}")
                        logInnerSpaces(tableAstLevel)
                        log("  - ${tableAstNode.f2.toPrettyString()}")
                    }
                    is FullOuterJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("full outer join")

                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logInner(tableAstNode.t2, tableAstLevel + 1)

                        logInnerSpaces(tableAstLevel)
                        log("  on")
                        logInnerSpaces(tableAstLevel)
                        log("  - ${tableAstNode.f1.toPrettyString()}")
                        logInnerSpaces(tableAstLevel)
                        log("  - ${tableAstNode.f2.toPrettyString()}")
                    }
                    is CrossJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("cross join")

                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logInner(tableAstNode.t2, tableAstLevel + 1)
                    }
                }
            }
            return

        }
    }

    fun FieldAstNode.toPrettyString(): String {
        when (this) {
            is LiteralFieldAstNode ->
                return "literal ${fieldValue}"
            is IdentFieldAstNode ->
                return "ident   ${fieldName}"
            is AllIdentsFieldAstNode ->
                return "        *"
        }
        return "undefined"
    }

}