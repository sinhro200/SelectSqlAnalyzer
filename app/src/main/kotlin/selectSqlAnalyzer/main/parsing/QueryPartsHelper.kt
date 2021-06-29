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
        var prevPos = selectPosition
        fromPosition = findWordPositionSkippingBrackets(query, "from", prevPos)
        if (fromPosition>0)
            prevPos = fromPosition
        wherePosition = findWordPositionSkippingBrackets(query, "where", prevPos)
        if (wherePosition>0)
            prevPos = wherePosition
        groupByPosition = findWordPositionSkippingBrackets(query, "group by", prevPos)
        if (groupByPosition>0)
            prevPos = groupByPosition
        havingPosition = findWordPositionSkippingBrackets(query, "having", prevPos)
        if (havingPosition>0)
            prevPos = havingPosition
        orderByPosition = findWordPositionSkippingBrackets(query, "order by", prevPos)

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
        return if (fromPosition == -1)
            query
        else
            query.substring(selectPosition, fromPosition)
    }

    private fun fromString(): String {
        return if (wherePosition == -1)
            query.substring(fromPosition)
        else
            query.substring(fromPosition, wherePosition)
    }

    private fun whereString(): String {
        return if (groupByPosition == -1)
            if (havingPosition == -1) {
                if (orderByPosition == -1) {
                    query.substring(wherePosition)
                } else {
                    query.substring(wherePosition, orderByPosition)
                }
            } else {
                query.substring(wherePosition, havingPosition)
            }
        else
            query.substring(wherePosition, groupByPosition)
    }

    private fun groupByString(): String {
        return if (havingPosition == -1)
            query.substring(groupByPosition)
        else
            query.substring(groupByPosition, havingPosition)
    }

    private fun havingString(): String {
        return if (orderByPosition == -1)
            query.substring(havingPosition)
        else
            query.substring(havingPosition, orderByPosition)
    }

    private fun orderByString(): String {
        return query.substring(orderByPosition)
    }
}