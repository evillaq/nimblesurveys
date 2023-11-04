package com.vidixmx.nimblesurveys.utils

import com.vidixmx.nimblesurveys.Constants.TOKEN_SECONDS_OFFSET
import java.util.Date

fun isTokenValid(tokenCreationTime: Long, tokenLife: Long): Boolean {
    val currentTime = Date().time / 1_000
    val remainingSeconds = (tokenCreationTime + tokenLife) - currentTime
    return remainingSeconds > TOKEN_SECONDS_OFFSET
}