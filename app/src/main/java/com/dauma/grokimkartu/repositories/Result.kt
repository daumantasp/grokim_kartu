package com.dauma.grokimkartu.repositories

class Result<T, K>(
    val data: T,
    val error: K
)