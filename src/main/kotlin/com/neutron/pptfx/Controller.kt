package com.neutron.pptfx

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Region
import org.apache.commons.math3.exception.NotANumberException
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

class Controller:Initializable {
    @FXML
    lateinit var listview: ListView<String>
    @FXML
    lateinit var combobox: ComboBox<String>
    @FXML
    lateinit var title: Label
    @FXML
    lateinit var titleInput: TextField
    @FXML
    lateinit var editButton: Button
    @FXML
    lateinit var doneButton: Button
    @FXML
    lateinit var separator: Separator
    @FXML
    lateinit var firstlineLabel: Label
    @FXML
    lateinit var infopane: AnchorPane

    lateinit var songTitleGroup:Array<Region>
    lateinit var songGroup:Array<Region>
    lateinit var bibleGroup:Array<Region>
    private fun Array<Region>.flip() {
        this.forEach {
            it.isDisable = !it.isDisable
            it.isVisible = !it.isVisible
        }
    }
    private fun Region.off() {
        this.isDisable = true
        this.isVisible = false
    }
    private fun Region.on() {
        this.isDisable = false
        this.isVisible = true
    }

    @FXML
    fun onNew() {
        infopane.on()
        val selected = combobox.selectionModel.selectedIndex
        val lut = arrayOf("Ének","Biblia","Üres")
        listview.items.add(lut[selected])
        selected.toSlideType().toSlideInfo().setVisibility()

        slideInfos.add(selected.toSlideType().toSlideInfo())
        assert(listview.items.size == slideInfos.size)
        currentSlide = slideInfos.size-1
    }

    fun SlideInfo.setVisibility() {
        infopane.on()
        when(this) {
            is SongInfo -> {
                title.off()
                editButton.off()
                separator.off()
                doneButton.on()
                titleInput.on()
            }
            is BibleInfo -> {

            }
            is BlankInfo -> {
                infopane.off()
            }
        }

    }

    @FXML
    fun onEditDone() {
        val info = slideInfos[currentSlide]
        if(info !is SongInfo) {
            log.fatal("Stored info is not for a song slide")
            exitProcess(1)
        }

        info.id = try {
            titleInput.text.toInt()
        } catch (e: NumberFormatException) {
            log.fatal("Not a number: ${titleInput.text}")
            exitProcess(2)
        }

        refreshCurrentSlide()
        refreshListView()
        songTitleGroup.flip()
    }
    @FXML
    fun onEdit() {
        refreshCurrentSlide()
        refreshListView()
    }

    fun refreshCurrentSlide() {
        val info = slideInfos[currentSlide]
        info.setVisibility()
        when(info) {
            is SongInfo -> {
                title.text = "${info.id}. ${if(info.id<=150) "Zsoltár" else "Dicséret"}"
            }
            is BibleInfo -> {

            }
            is BlankInfo -> {}
        }
    }
    fun refreshListView() {
        for ((index, info) in slideInfos.withIndex()) {
            listview.items[index] = when (info) {
                is SongInfo -> "${info.id}. ének"
                is BibleInfo -> "biblia"
                is BlankInfo -> "üres"
            }
        }
    }

    sealed interface SlideInfo
    data class SongInfo(var id:Int, var overflow: Boolean): SlideInfo
    data class BibleInfo(var passage: String): SlideInfo
    class BlankInfo: SlideInfo

    enum class SlideType(val value: String) {
        SONG("Ének"), BIBLE("Biblia"), BLANK("Üres")
    }

    private fun SlideType.toSlideInfo(): SlideInfo {
        return when(this) {
            SlideType.SONG -> SongInfo(0,false)
            SlideType.BIBLE -> BibleInfo("")
            SlideType.BLANK -> BlankInfo()
        }
    }

    private val slideInfos = arrayListOf<SlideInfo>()
    private var currentSlide = -1

    private fun Int.toSlideType() = arrayOf(SlideType.SONG, SlideType.BIBLE, SlideType.BLANK)[this]
    private fun String.toSlideType() = when(this) {
        "Ének"->SlideType.SONG
        "Biblia"->SlideType.BIBLE
        "Üres"->SlideType.BLANK
        else -> throw IllegalArgumentException()
    }

    private fun loadSlide() {
        val info = slideInfos[currentSlide]
        when(info) {
            is SongInfo -> {
                title.text = "${info.id}. ${if(info.id<=150) "Zsoltár" else "Dicséret"}"
            }
            is BibleInfo -> {

            }
            is BlankInfo -> {

            }
        }
    }

    override fun initialize(location: URL?, res: ResourceBundle?) {
        songTitleGroup = arrayOf(title, editButton, doneButton, titleInput, separator)
        songGroup = arrayOf(firstlineLabel)
        songGroup.forEach { it.off() }
        songTitleGroup.forEach { it.off() }
        combobox.items = FXCollections.observableArrayList(
            SlideType.SONG.value,
            SlideType.BIBLE.value,
            SlideType.BLANK.value
        )
        combobox.selectionModel.select(0)
        listview.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            println("Selected $oldValue -> $newValue")
            currentSlide = combobox.selectionModel.selectedIndex
            loadSlide()
        }
        titleInput.onKeyReleased = EventHandler<KeyEvent> { event ->
            if(event.code == KeyCode.ENTER)
                onEditDone()
        }
        slideInfos.add(BlankInfo())
        listview.items.add("Üres")
        listview.selectionModel.select(0)
    }
}