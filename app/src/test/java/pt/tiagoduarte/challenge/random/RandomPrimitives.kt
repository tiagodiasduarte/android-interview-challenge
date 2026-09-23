package pt.tiagoduarte.challenge.random

import kotlin.random.Random

private const val DEFAULT_STRING_LENGTH = 8
private val STRING_CHARS = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun Random.nextString(length: Int = DEFAULT_STRING_LENGTH): String =
    (1..length).map { STRING_CHARS.random(this) }.joinToString("")

fun Random.nextUrl(): String = "https://example.com/${nextString()}"
