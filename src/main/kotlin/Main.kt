package com.neutron

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
import javax.imageio.ImageIO
import kotlin.contracts.contract

//"https://enekeskonyv.reformatus.hu/digitalis-reformatus-enekeskonyv/enek/3/"

val SLIDE_W = 960
val SLIDE_H = 540
val SIDE_PADDING = 50

data class Song(val id:Int, val pd: BufferedImage, val verses:List<Int>)

fun bufimgToPicData(ppt: XMLSlideShow, bufimg: BufferedImage): XSLFPictureData {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bufimg, "jpg", baos)
    baos.close()
    return ppt.addPicture(baos.toByteArray(), PictureData.PictureType.JPEG)
}

fun parse(str:String): ArrayList<Song> {
    val ret = arrayListOf<Song>()
    for(line in str.split("\n")) {
        if(line[0].isDigit()) { // song
            //val id = line.takeWhile { it.isDigit() }.toInt()

            val id = line.split("/")[0].toInt()
            val bufimg = ImageIO.read(File("images/$id.jpg"))
            println("${bufimg.width}x${bufimg.height}\tq=${bufimg.height.toFloat()/bufimg.width.toFloat()}")

            val lyrfile = File("lyr/$id.txt")
            val max = lyrfile.readLines().count { it.isBlank() }

            val tmp = try {
                    line.split("/")[1]
                } catch (_: IndexOutOfBoundsException) {
                    val foo = line.split("-")
                    ret.add(Song(id, bufimg,(foo[0].toInt()..foo[1].toInt()).toList().dropWhile { it == 1 }))
                    continue
                }
            val verses: List<Int> = if(tmp[0] == 'a') {
                (1..max).toList()
            } else {
                tmp.split(",").map { it.toInt() }
            }
            ret.add(Song(id, bufimg, verses))
        }
    }
    return ret
}

fun main() {
    val ppt = XMLSlideShow()
    ppt.pageSize = Dimension(SLIDE_W, SLIDE_H)

    val slide1 = ppt.createSlide()
    slide1.background.fillColor = Color.BLACK

    parse(File("instr.txt").readText()).forEach { (id:Int, image:BufferedImage, verses) ->
        imageSlide(ppt, image)
        verseSlide(ppt, id, verses)
    }

    val endslide = ppt.createSlide()
    endslide.background.fillColor = Color.BLACK
    ppt.write(FileOutputStream("test.pptx"))
}

fun verseSlide(ppt: XMLSlideShow, songId:Int, verses: List<Int>) {
    val HPAD = 5
    val VPAD = 5

    var slide = ppt.createSlide()
    slide.background.fillColor = Color.BLACK

    var textBox = slide.createTextBox()
    textBox.anchor = Rectangle(HPAD, VPAD, SLIDE_W-2*HPAD, SLIDE_H-2*VPAD)
    for((index, verse) in verses.withIndex()) {
        val paragraph = textBox.addNewTextParagraph()
        val run = paragraph.addNewTextRun()
        val versetexts = arrayListOf<String>()
        var curr = ""
        File("lyr/$songId.txt").readLines().forEach {
            if(it.isBlank()) {
                versetexts.add(curr)
                curr = ""
                return@forEach
            }
            curr += it+"\n"
        }
        with(run) {
            setText("$verse. ${versetexts[verse-1]}")
            fontSize = 40.0
            setFontColor(Color.WHITE)
        }

        if(index % 2 == 1 && index != verses.size - 1) {
            slide = ppt.createSlide()
            slide.background.fillColor = Color.BLACK
            textBox = slide.createTextBox()
            textBox.anchor = Rectangle(HPAD, VPAD, SLIDE_W-2*HPAD, SLIDE_H-2*VPAD)
        }
    }
}

fun scriptureSlide(ppt: XMLSlideShow, path: String) {

}

fun imageSlide(ppt: XMLSlideShow, image: BufferedImage) {
    assert(image.width == 800)
    val pd = bufimgToPicData(ppt, image)

    val slide = ppt.createSlide()
    slide.background.fillColor = Color.BLACK
    val chordPic = slide.createPicture(pd)


    if(image.height < 700) { // single slide
        println("single")
        chordPic.anchor = Rectangle(
            (SLIDE_W - image.width * SLIDE_H / image.height) / 2, 0,
            image.width * SLIDE_H / image.height, SLIDE_H
        )
    } else { // multi slide
        println("multi")
        /*chordPic.anchor = Rectangle(
            (SIDE_PADDING), 0,
            SLIDE_W - 2*SIDE_PADDING, SLIDE_H
        )*/
        chordPic.anchor = Rectangle((SLIDE_W - image.width) / 2, 0, image.width, image.height)
        val overflowSlide = ppt.createSlide()
        overflowSlide.background.fillColor = Color.BLACK
        val p = overflowSlide.createPicture(pd)
        p.anchor = Rectangle(
            (SLIDE_W - image.width) / 2, SLIDE_H-image.height,
            image.width, image.height
        )
        if(image.height > SLIDE_H-10) {
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
