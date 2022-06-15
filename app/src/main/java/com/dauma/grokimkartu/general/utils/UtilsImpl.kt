package com.dauma.grokimkartu.general.utils

import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtils
import com.dauma.grokimkartu.general.utils.other.OtherUtils
import com.dauma.grokimkartu.general.utils.sharedstorage.SharedStorageUtils
import com.dauma.grokimkartu.general.utils.string.StringUtils
import com.dauma.grokimkartu.general.utils.time.TimeUtils

class UtilsImpl(
    override val imageUtils: ImageUtils,
    override val stringUtils: StringUtils,
    override val keyboardUtils: KeyboardUtils,
    override val timeUtils: TimeUtils,
    override val sharedStorageUtils: SharedStorageUtils,
    override val otherUtils: OtherUtils
) : Utils