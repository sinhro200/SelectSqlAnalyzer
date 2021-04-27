package selectSqlAnalyzer.main.parsing

class QueryPartsHelper(
        val query: String
) {
    var selectPosition = 0;
    var fromPosition = -1;
    var wherePosition = -1;
    var groupByPosition = -1;
    var havingPosition = -1;
    var orderByPosition = -1;

    var selectString = ""
    var fromString = ""
    var whereString = ""
    var groupByString = ""
    var havingString = ""
    var orderByString = ""

    fun parseParts() {
        selectString = selectString()

        fromString =
                if (fromPosition != -1)
                    fromString()
                else ""
        whereString =
                if (wherePosition != -1)
                    whereString()
                else ""
        groupByString =
                if (groupByPosition != -1)
                    groupByString()
                else ""
        havingString =
                if (havingPosition != -1)
                    havingString()
                else ""
        orderByString =
                if (orderByPosition != -1)
                    orderByString()
                else ""
    }

    private fun selectString(): String {
        fromPosition = wordPositionFindBracketSkip(query, "from", selectPosition)
        return if (fromPosition == -1)
            query
        else
            query.substring(selectPosition, fromPosition)
    }

    private fun fromString(): String {
        wherePosition = wordPositionFindBracketSkip(query, "where", fromPosition)
        return if (wherePosition == -1)
            query.substring(fromPosition)
        else
            query.substring(fromPosition, wherePosition)
    }

    private fun whereString(): String {
        groupByPosition = wordPositionFindBracketSkip(query, "group by", wherePosition)
        return if (groupByPosition == -1)
            query.substring(wherePosition)
        else
            query.substring(wherePosition, groupByPosition)
    }

    private fun groupByString(): String {
        havingPosition = wordPositionFindBracketSkip(query, "having", groupByPosition)
        return if (havingPosition == -1)
            query.substring(groupByPosition)
        else
            query.substring(groupByPosition, havingPosition)
    }

    private fun havingString(): String {
        orderByPosition = wordPositionFindBracketSkip(query, "order by", havingPosition)
        return if (orderByPosition == -1)
            query.substring(havingPosition)
        else
            query.substring(havingPosition, orderByPosition)
    }

    private fun orderByString(): String {
        return query.substring(orderByPosition, query.length)
    }
}