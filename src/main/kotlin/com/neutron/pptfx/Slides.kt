package com.neutron.pptfx

import org.apache.poi.xslf.usermodel.XMLSlideShow
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
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
            if(curr.isNotBlank())
                versetexts.add(curr)
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
            slide = this.createSlide()
            slide.background.fillColor = Color.BLACK
            textBox = slide.createTextBox()
            textBox.anchor = Rectangle(HPAD, VPAD, SLIDE_W -2*HPAD, SLIDE_H -2*VPAD)
        }
    }
}

fun XMLSlideShow.scriptureSlide(path: String) {
    val slide = this.createSlide()
    slide.background.fillColor = Color.BLACK

    slide.createTextBox().apply {
        anchor = Rectangle(0, 0, SLIDE_W, SLIDE_H)
    }.addNewTextParagraph().addNewTextRun().run {
        setText(path)
        setFontColor(Color.WHITE)
        fontSize = 64.0
    }
}

fun XMLSlideShow.imageSlide(image: BufferedImage) {
    log.debug("Creating image slide ${image.width}x${image.height}, q=${image.height.toFloat() / image.width.toFloat()}")
    assert(image.width == 800)
    val pd = bufimgToPicData(this, image)

    val slide = this.createSlide()
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
