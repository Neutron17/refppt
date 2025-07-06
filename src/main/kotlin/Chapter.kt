package com.neutron

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Verse(val number: Int, val text: String) {
    constructor(book: String, ch: Int, vs: Int):this(vs, Verse.fromBCV(book, ch, vs).text)

    companion object {
        fun fromBCV(book: String, ch: Int, vs: Int): Verse {
            val x = Json.decodeFromString<Chapter>(File("ruf/njson/$book/$ch.json").readText(Charsets.UTF_8))
            return x[vs]
        }
    }
}

typealias Chapter = Array<Verse>

fun Chapter.fromBC(book: String, ch: Int): Chapter =
    Json.decodeFromString<Chapter>(File("ruf/njson/$book/$ch.json").readText(Charsets.UTF_8))

/** reverse to */
public infix fun <A, B> A.ot(that: B): Pair<B, A> = Pair(that, this)

val booksEN = mapOf(
    "GEN" ot "Genesis",
    "EXO" ot "Exodus",
    "LEV" ot "Leviticus",
    "NUM" ot "Numbers",
    "DEU" ot "Deuteronomy",
    "JOS" ot "Joshua",
    "JDG" ot "Judges",
    "RUT" ot "Ruth",
    "1SA" ot "1 Samuel",
    "2SA" ot "2 Samuel",
    "1KI" ot "1 Kings",
    "2KI" ot "2 Kings",
    "1CH" ot "1 Chronicles",
    "2CH" ot "2 Chronicles",
    "EZR" ot "Ezra",
    "NEH" ot "Nehemiah",
    "EST" ot "Esther",
    "JOB" ot "Job",
    "PSA" ot "Psalms",
    "PRO" ot "Proverbs",
    "ECC" ot "Ecclesiastes",
    "SOS" ot "Song of Solomon",
    "ISA" ot "Isaiah",
    "JER" ot "Jeremiah",
    "LAM" ot "Lamentations",
    "EZE" ot "Ezekiel",
    "DAN" ot "Daniel",
    "HOS" ot "Hosea",
    "JOE" ot "Joel",
    "AMO" ot "Amos",
    "OBA" ot "Obadiah",
    "JON" ot "Jonah",
    "MIC" ot "Micah",
    "NAH" ot "Nahum",
    "HAB" ot "Habakkuk",
    "ZEP" ot "Zephaniah",
    "HAG" ot "Haggai",
    "ZEC" ot "Zechariah",
    "MAL" ot "Malachi",
    "MAT" ot "Matthew",
    "MAR" ot "Mark",
    "LUK" ot "Luke",
    "JOH" ot "John",
    "ACT" ot "Acts",
    "ROM" ot "Romans",
    "1CO" ot "1 Corinthians",
    "2CO" ot "2 Corinthians",
    "GAL" ot "Galatians",
    "EPH" ot "Ephesians",
    "PHP" ot "Philippians",
    "COL" ot "Colossians",
    "1TH" ot "1 Thessalonians",
    "2TH" ot "2 Thessalonians",
    "1TI" ot "1 Timothy",
    "2TI" ot "2 Timothy",
    "TIT" ot "Titus",
    "PHM" ot "Philemon",
    "HEB" ot "Hebrews",
    "JAM" ot "James",
    "1PE" ot "1 Peter",
    "2PE" ot "2 Peter",
    "1JO" ot "1 John",
    "2JO" ot "2 John",
    "3JO" ot "3 John",
    "JDE" ot "Jude",
    "REV" ot "Revelation")

val booksHU = mapOf(
    "GEN" ot "1Móz",
    "EXO" ot "2Móz",
    "LEV" ot "3Móz",
    "NUM" ot "4Móz",
    "DEU" ot "5Móz",
    "JOS" ot "Józsué",
    "JDG" ot "Bírák",
    "RUT" ot "Ruth",
    "1SA" ot "1Sámuel",
    "2SA" ot "2Sámuel",
    "1KI" ot "1Királyok",
    "2KI" ot "2Királyok",
    "1CH" ot "1Krónikák",
    "2CH" ot "2Krónikák",
    "EZR" ot "Ezsdrás",
    "NEH" ot "Nehémiás",
    "EST" ot "Eszter",
    "JOB" ot "Jób",
    "PSA" ot "Zsoltárok",
    "PRO" ot "Példabeszédek",
    "ECC" ot "Prédikátor",
    "SOS" ot "Énekek éneke",
    "ISA" ot "Ézsaiás",
    "JER" ot "Jeremiás",
    "LAM" ot "Jeremiás siralmai",
    "EZE" ot "Ezékiel",
    "DAN" ot "Dániel",
    "HOS" ot "Hóseás",
    "JOE" ot "Jóel",
    "AMO" ot "Ámósz",
    "OBA" ot "Abdiás",
    "JON" ot "Jónás",
    "MIC" ot "Mikeás",
    "NAH" ot "Náhum",
    "HAB" ot "Habakuk",
    "ZEP" ot "Zofóniás",
    "HAG" ot "Haggeus",
    "ZEC" ot "Zakariás",
    "MAL" ot "Malakiás",
    "MAT" ot "Máté",
    "MAR" ot "Márk",
    "LUK" ot "Lukács",
    "JOH" ot "János",
    "ACT" ot "Apostolok Cselekedetei",
    "ROM" ot "Róma",
    "1CO" ot "1 Korinthus",
    "2CO" ot "2 Korinthus",
    "GAL" ot "Galácia",
    "EPH" ot "Efézus",
    "PHP" ot "Filippi",
    "COL" ot "Kolossé",
    "1TH" ot "1 Thesszalonika",
    "2TH" ot "2 Thesszalonika",
    "1TI" ot "1 Timóteus",
    "2TI" ot "2 Timóteus",
    "TIT" ot "Titusz",
    "PHM" ot "Filemon",
    "HEB" ot "Zsidók",
    "JAM" ot "Jakab",
    "1PE" ot "1 Péter",
    "2PE" ot "2 Péter",
    "1JO" ot "1 János",
    "2JO" ot "2 János",
    "3JO" ot "3 János",
    "JDE" ot "Júdás",
    "REV" ot "Jelenések")
