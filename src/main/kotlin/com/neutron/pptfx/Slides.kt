package com.neutron.pptfx

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.poi.xslf.usermodel.XMLSlideShow
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun XMLSlideShow.verseSlide(songId:Int, verses: List<Int>) {
    if(verses.isEmpty()) {
        log.debug("empty verses while creating verse slide, skipping")
        return
    }
    val HPAD = 5
    val VPAD = 5

    var slide = this.createSlide()
    slide.background.fillColor = Color.BLACK

    var textBox = slide.createTextBox()
    textBox.anchor = Rectangle(HPAD, VPAD, SLIDE_W -2*HPAD, SLIDE_H -2*VPAD)
    verses.readVersesIndexed(songId).forEachIndexed { index, it->
        val paragraph = textBox.addNewTextParagraph()
        val run = paragraph.addNewTextRun()
        with(run) {
            setText("${it.first}. ${it.second}")
            fontSize = 40.0
            setFontColor(Color.WHITE)
        }

        if(index % 2 == 1 && index != verses.size - 1) {
            log.debug("new verse slide, index=$index")
            slide = this.createSlide()
            slide.background.fillColor = Color.BLACK
            textBox = slide.createTextBox()
            textBox.anchor = Rectangle(HPAD, VPAD, SLIDE_W -2*HPAD, SLIDE_H -2*VPAD)
        }
    }
}

@Serializable
data class Verse(val number: Int, val text: String)

fun XMLSlideShow.scriptureSlide(path: String) {
    val slide = this.createSlide()
    slide.background.fillColor = Color.BLACK

    val book = path.split(" ")[0].trim().uppercase()
    val chapter = path.split(" ")[1].split(",")[0].trim().toInt()
    val verses:List<Int> = try {
        val sp = path.split(" ")[1].split(",")[1].trim().split("-")
        (sp[0].toInt()..sp[1].toInt()).toList()
    } catch(e: IndexOutOfBoundsException) {
        listOf(path.split(" ")[1].split(",")[1].trim().toInt())
    }
    val vs = Json.decodeFromString<List<Verse>>(File("ruf/njson/$book/$chapter.json").readText())
    val passage = vs.filter { it.number in verses }.joinToString("") { it.text } + " ($path)"


    slide.createTextBox().apply {
        anchor = Rectangle(0, 0, SLIDE_W, SLIDE_H)
    }.addNewTextParagraph().addNewTextRun().run {
        setText(passage)
        setFontColor(Color.WHITE)
        fontSize = 64.0
    }
}

fun XMLSlideShow.imageSlide(image: BufferedImage, singleSlide: Boolean) {
    log.debug("Creating image slide ${image.width}x${image.height}, q=${image.height.toFloat() / image.width.toFloat()}")
    assert(image.width == 800)
    val pd = bufimgToPicData(this, image)

    val slide = this.createSlide()
    slide.background.fillColor = Color.BLACK
    val chordPic = slide.createPicture(pd)


    if(singleSlide) { // single slide
        log.debug("single slide")
        chordPic.anchor = Rectangle(
            (SLIDE_W - image.width * SLIDE_H / image.height) / 2, 0,
            image.width * SLIDE_H / image.height, SLIDE_H
        )
    } else { // multi slide
        log.debug("multiple slides")
        chordPic.anchor = Rectangle((SLIDE_W - image.width) / 2, 0, image.width, image.height)
        val overflowSlide = this.createSlide()
        overflowSlide.background.fillColor = Color.BLACK
        val p = overflowSlide.createPicture(pd)
        p.anchor = Rectangle(
            (SLIDE_W - image.width) / 2, SLIDE_H -image.height,
            image.width, image.height
        )
        if(image.height > 2* SLIDE_H -10) {
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
fun XMLSlideShow.imageSlide(image: BufferedImage) =
    this.imageSlide(image, image.height<700)
fun XMLSlideShow.imageSlide(id: Int, single: Boolean) =
    imageSlide(ImageIO.read(File("images/$id.jpg")), single)
fun XMLSlideShow.imageSlide(id: Int) =
    imageSlide(ImageIO.read(File("images/$id.jpg")))
