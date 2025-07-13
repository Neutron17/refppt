package com.neutron.pptfx

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
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
    lateinit var verseList: ListView<String>
    @FXML
    lateinit var splitbox: CheckBox

    @FXML
    fun onSearch() {
        id = field.text.toInt()

        val lines: List<String>
        verseList.items.clear()

        try {
            imageView.image = Image(File("images/$id.jpg").inputStream())
            val txt = File("lyr/$id.txt")
            verseArea.text = txt.readText(Charsets.UTF_8)
            lines = txt.readLines()
        } catch (_: Exception) {
            WindowFactory(bundle.getString("no_song"))
            return
        }
        if (imageView.image.doesSongOverflow())
            splitbox.isSelected = true
        var next = true
        for (i in 0..(lines.count { it.isBlank() } - 1)) {
            verseList.items.add(
                "${i + 1}. ${
                    lines.filter {
                        if (next) {
                            next = false; return@filter true
                        } else {
                            if (it.isBlank()) {
                                next = true
                            }; return@filter false
                        }}[i].take(32)}")
        }
        verseList.selectionModel.selectAll()

    }
    @FXML
    fun onSave() {
        id = field.text.toInt()
        verses = verseList.selectionModel.selectedItems.map { it.takeWhile { it.isDigit() }.toInt() }
        doSplit = splitbox.isSelected
        saveCB()
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        verseList.selectionModel.selectionMode = SelectionMode.MULTIPLE
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
        var verses by Delegates.notNull<List<Int>>()
        var doSplit by Delegates.notNull<Boolean>()
	}
}

