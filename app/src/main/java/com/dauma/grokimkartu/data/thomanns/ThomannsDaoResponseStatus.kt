package com.dauma.grokimkartu.data.thomanns

class ThomannsDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        INVALID_VALID_UNTIL_DATE,
        THOMANN_NOT_FOUND,
        FORBIDDEN,
        NOT_JOINABLE_FOR_OWNER,
        NOT_QUITABLE_FOR_OWNER,
        ALREADY_JOINED,
        NOT_A_THOMANN_USER,
        INVALID_AMOUNT,
        USER_ID_NOT_PROVIDED,
        UNKNOWN
    }
}