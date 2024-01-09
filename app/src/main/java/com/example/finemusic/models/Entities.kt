package com.example.finemusic.models

data class LoginResultInfo(
    val userId: Int
)

data class LoginInfo(
    val Phone: String,
    val Password: String
)

data class UserInfo(
    val userId: Int,
    val nicename: String,
    val slogan: String,
    val phone: String,
    val photo: String
)

data class UpdateUserInfo(
    val Nicename: String,
    val Slogan: String
)

data class UpdateUserPhotoInfo(
    val Base64: String
)

data class UserIdInfo(
    val UserId: Int
)