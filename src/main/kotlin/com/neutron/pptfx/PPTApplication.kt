package com.neutron.pptfx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.apache.poi.sl.usermodel.PictureData
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFPictureData
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

//"https://enekeskonyv.reformatus.hu/digitalis-reformatus-enekeskonyv/enek/3/"

const val SLIDE_W = 960
const val SLIDE_H = 540

val log = LogManager.getLogger()!!
lateinit var primaryStage: Stage
var bundle: ResourceBundle = ResourceBundle.getBundle("ppt", Locale.getDefault())

fun bufimgToPicData(ppt: XMLSlideShow, bufimg: BufferedImage): XSLFPictureData {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bufimg, "jpg", baos)
    baos.close()
    return ppt.addPicture(baos.toByteArray(), PictureData.PictureType.JPEG)
}

class PPTApplication: Application() {
    override fun start(stage: Stage) {
        primaryStage = stage
        val loader = FXMLLoader(PPTApplication::class.java.getResource("view.fxml"), bundle)
        val sc = Scene(loader.load(), 900.0, 600.0)
        sc.stylesheets.add(javaClass.getResource("view.fxml")!!.toExternalForm())

        with(stage) {
            title = "ppt"
            scene = sc
            isResizable=false
            centerOnScreen()
            show()
            toFront()
        }
    }
}

fun main(args: Array<String>) {
    if(args.size>1)
        bundle = ResourceBundle.getBundle("ppt", Locale(args[1]))
    println(bundle.locale.country)
    Application.launch(PPTApplication::class.java)
}
