package selectSqlAnalyzer.main.parsing

import java.lang.Exception

class ParsingException(
        override val message: String,
) : Exception() {
    private var pos: Int? = null

    constructor(message: String, pos: Int ) : this(message) {
        this.pos = pos
    }

    override fun getLocalizedMessage(): String {
        pos?.let {
            return "$message at $it position"
        }
        return message
    }
}