package com.neutron

import org.apache.poi.sl.usermodel.PictureData
import org.apache.poi.xslf.usermodel.XMLSlideShow
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO

data class Data(val virtualId: Int, val image: String)

val songs = mutableMapOf<Int, Data>()

//"https://enekeskonyv.reformatus.hu/digitalis-reformatus-enekeskonyv/enek/3/"

fun loadLUT() {
    File("combi_ulti.csv").readLines().forEach {
        val split = it.split(",")
        songs.put(split[0].toInt(), Data(split[1].toInt(), split[2]))
    }
}

val SLIDE_W = 960
val SLIDE_H = 540
val SIDE_PADDING = 50

data class Song(val image: BufferedImage, val verses:List<Int>)

fun parse(str:String): Array<Song> {
    for(line in str.split("\n")) {
        if(line[0].isDigit()) {

        }
    }
}

fun main() {
    /*File("inp.txt").readText().let {
        out -> songs.addAll(out.split("\n").map { it.toInt() })
    }*/
    println("Loading...")
    loadLUT()

    val todo = arrayOf(66, 205)
    val images = arrayListOf<BufferedImage>()
    for(s in todo) {
        val image = ImageIO.read(File("images/${s}.jpg"))
        println("${image.width}x${image.height}\tr=${image.height.toFloat()/image.width.toFloat()}")
        images.add(image)
    }

    println("Creating presentation...")
    val ppt = XMLSlideShow()
    ppt.pageSize = Dimension(SLIDE_W, SLIDE_H)

    val slide1 = ppt.createSlide()
    slide1.background.fillColor = Color.BLACK

    parse(File("instr.txt").readText()).forEach { (image, verses) ->
        imageSlide(ppt, image)
        verseSlide(ppt, verses)
    }

    for(image in images) {
        imageSlide(ppt, image)
    }

    /*val textBox = slide1.createTextBox()
    textBox.anchor = Rectangle(100, 100, 500, 100)

    val paragraph = textBox.addNewTextParagraph()
    val run = paragraph.addNewTextRun()
    run.setText("Hello from Apache POI!")
    run.fontSize = 32.0
    run.setFontColor(Color.BLACK)*/


    ppt.write(FileOutputStream("test.pptx"))
}

fun verseSlide(ppt: XMLSlideShow, verses: List<Int>) {

}

fun imageSlide(ppt: XMLSlideShow, image: BufferedImage) {
    val imageBytes = ByteArrayOutputStream()
    ImageIO.write(image, "jpg", imageBytes)
    imageBytes.close()
    val pictureData: ByteArray = imageBytes.toByteArray()

    val slide = ppt.createSlide()
    slide.background.fillColor = Color.BLACK
    val pd = ppt.addPicture(pictureData, PictureData.PictureType.JPEG)
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
        if(2*image.height > SLIDE_H-15) {
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
