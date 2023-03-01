package com.dauma.grokimkartu.data

data class DaoResult<T, K>(
    val data: T,
    val status: K
)