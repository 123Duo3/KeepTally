package me.konyaco.keeptally.ui.component.addrecord

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

internal fun validateNumberString(str: String): Boolean {
    var hasDot = false
    var decimalLength = 0

    for (c in str) {
        if (c == '.') {
            if (hasDot) return false
            else hasDot = true
        } else if (c in '0'..'9') {
            if (hasDot) {
                decimalLength += 1
                if (decimalLength > 2) {
                    return false
                }
            }
        } else {
            return false
        }
    }
    return true
}

/**
 * Trim '0'
 * 0043.1
 * 43.1
 */
internal fun normalizeNumberString(value: TextFieldValue): TextFieldValue {
    val text = value.text
    return if (text == "0") {
        value
    } else if (text.getOrNull(text.indexOf('.') - 1) == '0') {
        value
    } else {
        var zeros = 0
        for (c in text) {
            if (c == '0') zeros++
            else break
        }
        value.copy(
            text = text.substring(zeros until text.length),
            selection = TextRange(value.selection.start - zeros, value.selection.end - zeros)
        )
    }
}
