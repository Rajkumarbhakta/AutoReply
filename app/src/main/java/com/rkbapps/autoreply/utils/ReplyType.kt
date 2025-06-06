package com.rkbapps.autoreply.utils

enum class ReplyType(
    val value: String,
    val meaning: String = "Reply to $value messages"
) {
    INDIVIDUAL("individual"),
    GROUP("group"),
    BOTH("both", meaning = "Reply to both group and individual messages")
}