package selectSqlAnalyzer.main.parsing


public fun findWordPositionSkippingBrackets(
        input: String,
        wordToFind: String,
        startPos: Int = 0,
): Int {
    val ph = ParseHelper(input, startPos)
    var cnt = 0
    var index = input.indexOf(wordToFind, startPos)
    if (index == -1)
        return index
    while (ph.pos < index || cnt != 0) {
        if (ph.cur == '(')
            cnt++
        if (ph.cur == ')')
            cnt--
        ph.move()
    }
    if (ph.pos != index)
        index = input.indexOf(wordToFind, startPos + ph.pos)
    if (index == -1)
        return index
    return index
}
