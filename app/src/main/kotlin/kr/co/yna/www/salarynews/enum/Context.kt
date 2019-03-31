package kr.co.yna.www.salarynews.enum

enum class Context {
    INACTIVE, START, SALARYNEWS, SELECT_NUMBER, SELECT_NUMBER_PLAYING, CLOSING
}

fun str2context(output: String): Context {
    when (output) {
        "" -> return Context.INACTIVE
        "start" -> return Context.START
        "salarynews" -> return Context.SALARYNEWS
        "select_number" -> return Context.SELECT_NUMBER
        "select_number_playing" -> return Context.SELECT_NUMBER_PLAYING
        "closing" -> return Context.CLOSING
        else -> throw IllegalArgumentException("unexpected output_context string")
    }
}

fun context2str(context: Context): String {
    when (context) {
        Context.INACTIVE -> return ""
        Context.START -> return "start"
        Context.SALARYNEWS -> return "salarynews"
        Context.SELECT_NUMBER -> return "select_number"
        Context.SELECT_NUMBER_PLAYING -> return "select_number_playing"
        Context.CLOSING -> return "closing"
    }
}