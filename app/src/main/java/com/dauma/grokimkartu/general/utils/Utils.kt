package com.dauma.grokimkartu.general.utils

import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtils
import com.dauma.grokimkartu.general.utils.string.StringUtils

interface Utils {
    val imageUtils: ImageUtils
    val stringUtils: StringUtils
    val keyboardUtils: KeyboardUtils
}