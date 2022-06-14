package com.dauma.grokimkartu.general.utils.other

class OtherUtilsImpl : OtherUtils {
    override fun getRanges(list: List<Int>): List<List<Int>> {
        val result: MutableList<List<Int>> = mutableListOf()
        if (list.isEmpty() == false) {
            var count = 1
            var lastItem = list[0]
            for (i in 1 until list.count()) {
                val item = list[i]
                if (lastItem + 1 == item) {
                    count += 1
                } else {
                    result.add(listOf(lastItem - count + 1, count))
                    count = 1
                }
                lastItem = item
            }
            result.add(listOf(lastItem - count + 1, count))
        }
        return result
    }
}