package com.dauma.grokimkartu.general.utils

import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtils
import com.dauma.grokimkartu.general.utils.other.OtherUtils
import com.dauma.grokimkartu.general.utils.sharedstorage.SharedStorageUtils
import com.dauma.grokimkartu.general.utils.string.StringUtils
import com.dauma.grokimkartu.general.utils.time.TimeUtils

interface Utils {
    val imageUtils: ImageUtils
    val stringUtils: StringUtils
    val keyboardUtils: KeyboardUtils
    val timeUtils: TimeUtils
    val sharedStorageUtils: SharedStorageUtils
    val otherUtils: OtherUtils
}