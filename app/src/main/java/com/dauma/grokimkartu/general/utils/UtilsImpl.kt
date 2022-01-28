package com.dauma.grokimkartu.general.utils

import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.dauma.grokimkartu.general.utils.string.StringUtils

class UtilsImpl(
    override val imageUtils: ImageUtils,
    override val stringUtils: StringUtils,
) : Utils {
}