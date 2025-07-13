package com.neutron.pptfx

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextArea
import org.apache.poi.xslf.usermodel.XMLSlideShow
import java.awt.Color
import java.awt.Dimension
import java.io.FileOutputStream
import java.net.URL
import java.util.*

class FastController:Initializable {
	@FXML
	lateinit var textarea: TextArea
	@FXML
	lateinit var biblecombo: ChoiceBox<String>

	@FXML
	fun onInsert() {
		with(textarea) {
			text = text.substring(0, caretPosition) +
					booksHU[biblecombo.selectionModel.selectedItem] +
					text.substring(caretPosition)
		}
	}

	@FXML
	fun onSave() {
		log.info("Creating presentation with dimensions ${SLIDE_W}x$SLIDE_H")
		val ppt = XMLSlideShow()
		ppt.pageSize = Dimension(SLIDE_W, SLIDE_H)

		log.info("Creating first blank slide")
		val slide1 = ppt.createSlide()
		slide1.background.fillColor = Color.BLACK

		textarea.text.split("\n").forEach {
			val data = Data.fromString(it.trim())
			when(data) {
				is Song -> {
					ppt.imageSlide(data.id)
					ppt.verseSlide(data.id, data.verses)
				}
				is Passage -> {
					ppt.scriptureSlide(data.path)
				}
			}
		}
		log.info("Creating last blank slide")
		val endslide = ppt.createSlide()
		endslide.background.fillColor = Color.BLACK
		log.info("Writing PPT to file")
		ppt.write(FileOutputStream("test.pptx"))
		WindowFactory(bundle.getString("write_succ"))
	}

	override fun initialize(location: URL?, resources: ResourceBundle?) {
		biblecombo.items.addAll(booksHU.keys)
	}
}