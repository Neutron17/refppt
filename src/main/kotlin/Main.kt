package com.neutron

import org.apache.logging.log4j.LogManager
import org.apache.poi.sl.usermodel.PictureData
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFPictureData
import java.awt.Color
import java.awt.Dimension
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

fun bufimgToPicData(ppt: XMLSlideShow, bufimg: BufferedImage): XSLFPictureData {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bufimg, "jpg", baos)
    baos.close()
    return ppt.addPicture(baos.toByteArray(), PictureData.PictureType.JPEG)
}

fun main(args: Array<String>) {
    log.info("Creating presentation with dimensions ${SLIDE_W}x$SLIDE_H")
    val ppt = XMLSlideShow()
    ppt.pageSize = Dimension(SLIDE_W, SLIDE_H)

    log.info("Creating first blank slide")
    val slide1 = ppt.createSlide()
    slide1.background.fillColor = Color.BLACK

    try {
        File("instr.txt").readText().split("\n").forEach {
            val data = Data.fromString(it)
            when (data) {
                is Song -> {
                    log.debug("Creating slide(s) for ID=${data.id}")
                    ppt.imageSlide(data.pd)
                    ppt.verseSlide(data.id, data.verses)
                }
                is Passage -> {
                    ppt.scriptureSlide(data.path)
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
