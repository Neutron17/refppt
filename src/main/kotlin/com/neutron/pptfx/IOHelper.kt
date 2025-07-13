package com.neutron.pptfx

import javafx.scene.image.Image
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun Int.isValidSongId():Boolean = File("images/$this.jpg").exists()
fun <T: Exception> Int.isValidSongIdOrThrow(exceptionProvider: ()->T):Boolean {
	if(!this.isValidSongId())
		throw exceptionProvider()
	return true
}
fun Int.doesSongOverflow():Boolean = ImageIO.read(File("images/$this.jpg")).height > 700
fun Image.doesSongOverflow():Boolean = this.height > 700

fun Int.readVersesIndexed():List<Pair<Int, String>> {
	val lines = try {
		File("lyr/$this.txt").readLines()
	} catch (_: IOException) {
		log.fatal("Failed to open lyrics file $this")
		exitProcess(1)
	}
	val verses = arrayListOf<Pair<Int,String>>()
	var curr = ""
	lines.forEach {
		if (it.isBlank()) {
			verses.add(verses.size+1 to curr)
			curr = ""
			return@forEach
		}
		curr += it + "\n"
	}
	if(curr.isNotBlank())
		verses.add(verses.size+1 to curr)
	return verses
}
fun Int.readVerses():List<String> = this.readVersesIndexed().map { it.second }
fun List<Int>.readVerses(id: Int):List<String> = id.readVerses().filterIndexed { index, _ -> index+1 in this }
fun List<Int>.readVersesIndexed(id: Int):List<Pair<Int,String>> = id.readVersesIndexed().filterIndexed { index, _ -> index+1 in this }
