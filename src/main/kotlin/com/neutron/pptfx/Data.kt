package com.neutron.pptfx

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

sealed interface Data {
    companion object {
        fun fromString(line: String): Data {
            if(line[0].isDigit()) { // song
                val id = line.split("/")[0].toInt()
                val bufimg = ImageIO.read(File("images/$id.jpg"))

                val lyrfile = File("lyr/$id.txt")
                val max = try {
                    lyrfile.readLines().count { it.isBlank() } // TODO: fix reopening
                } catch(e: IOException) {
                    e.printStackTrace()
                    log.fatal("Couldn't read lyrics file with ID=$id. ${e.localizedMessage}. Aborting")
                    exitProcess(1)
                }

                val tmp = try {
                    line.split("/")[1]
                } catch (_: IndexOutOfBoundsException) {
                    log.fatal("'$line' is not slash separated")
                    exitProcess(4)
                }
                val verses: List<Int> = if(tmp[0] == 'a') {
                    (1..max).toList()
                } else {
                    try {
                        tmp.split(",").map { it.toInt() }
                    } catch (_: Exception) {
                        val foo = tmp.split("-")
                        (foo[0].toInt()..foo[1].toInt()).toList().dropWhile { it == 1 }
                    }
                }
                return Song(id, bufimg, verses.dropWhile { it == 1 })
            } else { // passage
                return Passage(line)
            }
        }
    }
}

data class Song(val id: Int, val pd: BufferedImage, val verses: List<Int>):Data
data class Passage(val path: String):Data

