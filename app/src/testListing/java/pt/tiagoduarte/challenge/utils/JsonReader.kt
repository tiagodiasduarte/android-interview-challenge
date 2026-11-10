package pt.tiagoduarte.challenge.utils

import pt.tiagoduarte.challenge.data.remote.RetrofitClient

fun readFromJSONToString(jsonFile: String): String =
    requireNotNull(JsonReader::class.java.classLoader?.getResource("json/$jsonFile")) {
        "Missing test resource: json/$jsonFile"
    }.readText()

inline fun <reified T> readFromJSONToModel(jsonFile: String): T =
    RetrofitClient.json.decodeFromString<T>(readFromJSONToString(jsonFile))

private object JsonReader
