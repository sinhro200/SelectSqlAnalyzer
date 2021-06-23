package selectSqlAnalyzer.main.tools

import selectSqlAnalyzer.main.ast.nodes.*

object CustomLogger {
    const val ZoneDivider = "-- -- -- -- --"
    private var query: String? = null
    fun logQuery(query: String) {
        this.query = query
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
            log("- select")
            log("  >fields")
            for (f in pr.fieldAstNodes) {
                logFieldAstNode(f, level + 1)
//                log("    -${f.toPrettyString()}")
            }

            if (pr.from != null) {
                log("  >tables")
                val tanl = TableAstNodeLogger(this)
                tanl.log(pr.from)
            } else {
                log("    -null")
            }

            pr.where?.let { whereNode ->
                log("  >where")
                logWhereNode(whereNode, level + 1)
                /*do {
                    when (whereNode) {
                        is SimpleWhereAstNode -> {
                            log("   -${whereNode.condition.f1.toPrettyString()} " +
                                    whereNode.condition.conditionOp.string +
                                    " ${whereNode.condition.f1.toPrettyString()}")
                        }
                        is AndWhereAstNode -> {

                        }
                        is OrWhereAstNode -> {

                        }
                    }
                } while (whereNode !is SimpleWhereAstNode)*/
            }
        }
    }

    private fun logFieldAstNode(fieldAstNode: FieldAstNode, level: Int = 0) {
        LevelLogger(level).apply {
            fieldAstNode.apply {
                when (this) {
                    is LiteralFieldAstNode ->
                        log("- literal ${fieldValue}")
                    is IdentFieldAstNode ->
                        log("- ident   ${fieldName}")
                    is SelectAstNode -> {
                        logAstTreeInner(this, level)
                    }
                    is AllIdentsFieldAstNode ->
                        log("- *")
                    else -> {
                        log("undefined")
                    }
                }
            }
        }
    }

    private fun logWhereNode(whereAstNode: WhereAstNode, level: Int) {
        LevelLogger(level).apply {
            whereAstNode.apply {
                when (this) {
                    is SimpleWhereAstNode -> {
                        log("${condition.conditionOp.string}")
                        logFieldAstNode(condition.f1, level + 1)
                        logFieldAstNode(condition.f2, level + 1)
//                        log("- ${condition.f1.toPrettyString()} ")
//                        log("- ${condition.f2.toPrettyString()}")
                    }
                    is AndWhereAstNode -> {
                        log("and")
                        for (n in nodes) {
                            logWhereNode(n, level + 1)
                        }
                    }
                    is OrWhereAstNode -> {
                        log("or")
                        for (n in nodes) {
                            logWhereNode(n, level + 1)
                        }
                    }
                }
            }
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
                        tableAstNode.asSelect?.let {
                            log("as ", tableAstLevel + 1)
                            logAstTreeInner(it, tableAstLevel + 1)
                        }
                    }
                    is RightOuterJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("right outer join")

                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logInner(tableAstNode.t2, tableAstLevel + 1)

                        logInnerSpaces(tableAstLevel)
                        log("  on")
//                        logInnerSpaces(tableAstLevel)
                        logFieldAstNode(tableAstNode.f1, tableAstLevel + 1)
//                        log("  - ${tableAstNode.f1.toPrettyString()}")
//                        logInnerSpaces(tableAstLevel)
                        logFieldAstNode(tableAstNode.f2, tableAstLevel + 1)
//                        log("  - ${tableAstNode.f2.toPrettyString()}")
                    }
                    is LeftOuterJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("left outer join")

                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logInner(tableAstNode.t2, tableAstLevel + 1)

                        logInnerSpaces(tableAstLevel)
                        log("  on")
//                        logInnerSpaces(tableAstLevel)
                        logFieldAstNode(tableAstNode.f1, tableAstLevel + 1)
//                        log("  - ${tableAstNode.f1.toPrettyString()}")
//                        logInnerSpaces(tableAstLevel)
                        logFieldAstNode(tableAstNode.f2, tableAstLevel + 1)
//                        log("  - ${tableAstNode.f2.toPrettyString()}")
                    }
                    is InnerJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("inner join")

                        logFieldAstNode(tableAstNode.f1, tableAstLevel + 1)
//                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logFieldAstNode(tableAstNode.f2, tableAstLevel + 1)
//                        logInner(tableAstNode.t2, tableAstLevel + 1)

                        logInnerSpaces(tableAstLevel)
                        log("  on")
//                        logInnerSpaces(tableAstLevel)
//                        log("  - ${tableAstNode.f1.toPrettyString()}")
                        logFieldAstNode(tableAstNode.f1, tableAstLevel + 1)
//                        logInnerSpaces(tableAstLevel)
//                        log("  - ${tableAstNode.f2.toPrettyString()}")
                        logFieldAstNode(tableAstNode.f2, tableAstLevel + 1)
                    }
                    is FullOuterJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("full outer join")

                        logFieldAstNode(tableAstNode.f1, tableAstLevel + 1)
//                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logFieldAstNode(tableAstNode.f2, tableAstLevel + 1)
//                        logInner(tableAstNode.t2, tableAstLevel + 1)

                        logInnerSpaces(tableAstLevel)
                        log("  on")
//                        logInnerSpaces(tableAstLevel)
//                        log("  - ${tableAstNode.f1.toPrettyString()}")
                        logFieldAstNode(tableAstNode.f1, tableAstLevel + 1)
//                        logInnerSpaces(tableAstLevel)
//                        log("  - ${tableAstNode.f2.toPrettyString()}")
                        logFieldAstNode(tableAstNode.f1, tableAstLevel + 1)
                    }
                    is CrossJoinTableAstNode -> {
                        logInnerSpaces(tableAstLevel)
                        log("cross join")

                        logInner(tableAstNode.t1, tableAstLevel + 1)

                        logInner(tableAstNode.t2, tableAstLevel + 1)
                    }
                    else ->{
                        //pass
                    }
                }
            }
            return

        }
    }

}