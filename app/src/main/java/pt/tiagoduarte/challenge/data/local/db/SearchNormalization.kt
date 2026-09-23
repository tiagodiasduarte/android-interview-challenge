package pt.tiagoduarte.challenge.data.local.db

import java.text.Normalizer
import java.util.Locale

private val DIACRITICS = "\\p{Mn}+".toRegex()

fun String.normalizeForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(DIACRITICS, "")
        .lowercase(Locale.ROOT)
