package com.neutron

/*import org.crosswire.jsword.book.BookData
import org.crosswire.jsword.book.Books
import org.crosswire.jsword.book.OSISUtil
import org.crosswire.jsword.bridge.BookInstaller
import org.crosswire.jsword.passage.Verse*/
import org.apache.logging.log4j.LogManager
import org.apache.poi.sl.usermodel.PictureData
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFPictureData
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

//"https://enekeskonyv.reformatus.hu/digitalis-reformatus-enekeskonyv/enek/3/"

const val SLIDE_W = 960
const val SLIDE_H = 540

val log = LogManager.getLogger()!!

sealed interface Data

data class Song(val id: Int, val pd: BufferedImage, val verses: List<Int>):Data
data class Passage(val path: String):Data

fun bufimgToPicData(ppt: XMLSlideShow, bufimg: BufferedImage): XSLFPictureData {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bufimg, "jpg", baos)
    baos.close()
    return ppt.addPicture(baos.toByteArray(), PictureData.PictureType.JPEG)
}

fun parse(str:String): ArrayList<Data> {
    val ret = arrayListOf<Data>()
    for(line in str.split("\n")) {
        if(line[0].isDigit()) { // song
            //val id = line.takeWhile { it.isDigit() }.toInt()

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
                    log.debug("'$line' is not slash separated")
                    val foo = line.split("-")
                    ret.add(Song(id, bufimg,(foo[0].toInt()..foo[1].toInt()).toList().dropWhile { it == 1 }))
                    continue
                }
            val verses: List<Int> = if(tmp[0] == 'a') {
                (1..max).toList()
            } else {
                tmp.split(",").map { it.toInt() }
            }
            ret.add(Song(id, bufimg, verses.dropWhile { it == 1 }))
        } else { // passage
            ret.add(Passage(line))
        }
    }
    return ret
}

fun main(args: Array<String>) {
    /*//BookInstaller.main(args)
    val bible = Books.installed().getBook("HunRUF")
    val keys = bible.getKey("2Jn 1:11-13")
    val data = BookData(bible, keys)
    println(OSISUtil.getCanonicalText(data.osisFragment))
    println()
    for(key in keys) {
        val verse = key.to(Verse())
        println(bible.getRawText(verse))
    }
    return*/
    log.info("Creating presentation with dimensions ${SLIDE_W}x$SLIDE_H")
    val ppt = XMLSlideShow()
    ppt.pageSize = Dimension(SLIDE_W, SLIDE_H)

    log.info("Creating first blank slide")
    val slide1 = ppt.createSlide()
    slide1.background.fillColor = Color.BLACK

    try {
        parse(File("instr.txt").readText()).forEach { it ->
            when (it) {
                is Song -> {
                    log.debug("Creating slide(s) for ID=${it.id}")
                    imageSlide(ppt, it.pd)
                    verseSlide(ppt, it.id, it.verses)
                }

                is Passage -> {
                    scriptureSlide(ppt, it.path)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        log.fatal("Couldn't read file 'instr.txt'. ${e.localizedMessage}. Aborting")
        exitProcess(1)
    }

    log.info("Creating last blank slide")
    val endslide = ppt.createSlide()
    endslide.background.fillColor = Color.BLACK
    log.info("Writing PPT to file")
    ppt.write(FileOutputStream("test.pptx"))
}

fun verseSlide(ppt: XMLSlideShow, songId:Int, verses: List<Int>) {
    if(verses.isEmpty()) {
        log.debug("empty verses while creating verse slide, skipping")
        return
    }
    val HPAD = 5
    val VPAD = 5

    var slide = ppt.createSlide()
    slide.background.fillColor = Color.BLACK

    var textBox = slide.createTextBox()
    textBox.anchor = Rectangle(HPAD, VPAD, SLIDE_W-2*HPAD, SLIDE_H-2*VPAD)
    for((index, verse) in verses.withIndex()) {
        log.debug("index=$index verse=$verse")
        val paragraph = textBox.addNewTextParagraph()
        val run = paragraph.addNewTextRun()
        val versetexts = arrayListOf<String>()
        var curr = ""
        try {
            File("lyr/$songId.txt").readLines().forEach {
                if (it.isBlank()) {
                    versetexts.add(curr)
                    curr = ""
                    return@forEach
                }
                curr += it + "\n"
            }
        } catch (e: IOException) {
            log.error("Failed to open lyrics file with id: $songId. ${e.localizedMessage}. Aborting")
            e.printStackTrace()
            exitProcess(2);
        }
        with(run) {
            setText("$verse. ${versetexts[verse-1]}")
            fontSize = 40.0
            setFontColor(Color.WHITE)
        }

        if(index % 2 == 1 && index != verses.size - 1) {
            log.debug("new verse slide, index=$index")
            slide = ppt.createSlide()
            slide.background.fillColor = Color.BLACK
            textBox = slide.createTextBox()
            textBox.anchor = Rectangle(HPAD, VPAD, SLIDE_W-2*HPAD, SLIDE_H-2*VPAD)
        }
    }
}

fun scriptureSlide(ppt: XMLSlideShow, path: String) {
    val slide = ppt.createSlide()
    slide.background.fillColor = Color.BLACK

    slide.createTextBox().apply {
        anchor = Rectangle(0, 0, SLIDE_W, SLIDE_H)
    }.addNewTextParagraph().addNewTextRun().run {
        setText(path)
        setFontColor(Color.WHITE)
        fontSize = 64.0
    }
}

fun imageSlide(ppt: XMLSlideShow, image: BufferedImage) {
    log.debug("Creating image slide ${image.width}x${image.height}, q=${image.height.toFloat() / image.width.toFloat()}")
    assert(image.width == 800)
    val pd = bufimgToPicData(ppt, image)

    val slide = ppt.createSlide()
    slide.background.fillColor = Color.BLACK
    val chordPic = slide.createPicture(pd)


    if(image.height < 700) { // single slide
        log.debug("single slide")
        chordPic.anchor = Rectangle(
            (SLIDE_W - image.width * SLIDE_H / image.height) / 2, 0,
            image.width * SLIDE_H / image.height, SLIDE_H
        )
    } else { // multi slide
        log.debug("multiple slides")
        chordPic.anchor = Rectangle((SLIDE_W - image.width) / 2, 0, image.width, image.height)
        val overflowSlide = ppt.createSlide()
        overflowSlide.background.fillColor = Color.BLACK
        val p = overflowSlide.createPicture(pd)
        p.anchor = Rectangle(
            (SLIDE_W - image.width) / 2, SLIDE_H-image.height,
            image.width, image.height
        )
        if(image.height > 2*SLIDE_H-10) {
            log.info("Image doesn't fit on 2 slides")
            arrayOf(slide, overflowSlide).forEach {
                it.createTextBox().run {
                    text = "BAD"
                    fillColor = Color.RED
                    anchor = Rectangle(0, 0, 75, 50)
                }
            }
        }
    }
}
