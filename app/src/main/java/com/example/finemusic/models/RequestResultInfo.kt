package com.example.finemusic.models

data class RequestResultInfo<T>(
    val code: Int,
    val message: String,
    val data: T
)