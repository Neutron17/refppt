package com.neutron.pptfx

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import java.io.File
//import org.controlsfx.control.textfield.TextFields
import java.net.URL
import java.util.ResourceBundle
import kotlin.properties.Delegates

class SongEditController: Initializable {
    @FXML
    lateinit var field: TextField
    @FXML
    lateinit var verseArea: TextArea
    @FXML
    lateinit var imageView: ImageView

    @FXML
    fun onSearch() {
        id = field.text.toInt()

        imageView.image = Image(File("images/$id.jpg").inputStream())
        verseArea.text = File("lyr/$id.txt").readText(Charsets.UTF_8)
    }
    @FXML
    fun onSave() {
        id = field.text.toInt()
        saveCB()
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        field.onKeyReleased = EventHandler<KeyEvent> { event ->
            if(event.code == KeyCode.ENTER)
                onSearch()
        }
        field.text = id.toString()
        onSearch()
        //TextFields.bindAutoCompletion(field, "hello", "hi", "foo", "bar")
        /*field = AutoCompleteTextField()
        field.entries.addAll(listOf("hello", "hi", "foo", "bar"))*/
    }
    companion object {
        var id by Delegates.notNull<Int>()
        var stage by Delegates.notNull<Stage>()
        var saveCB by Delegates.notNull<()->Unit>()
	}
}

