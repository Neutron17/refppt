package com.neutron.pptfx

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.stage.Modality
import javafx.stage.Stage
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

class Controller:Initializable {
	sealed interface SlideInfo {
		var inited: Boolean
	}
	data class SongInfo(var id:Int, var overflow: Boolean, override var inited: Boolean = true): SlideInfo
	data class BibleInfo(var passage: String, override var inited: Boolean = true): SlideInfo
	class BlankInfo(override var inited: Boolean = true) : SlideInfo

	enum class SlideType(val value: String) {
		SONG("Ének"), BIBLE("Biblia"), BLANK("Üres")
	}

	private fun SlideType.toEmptySlideInfo(): SlideInfo {
		return when(this) {
			SlideType.SONG -> SongInfo(0,false, inited = false)
			SlideType.BIBLE -> BibleInfo("", inited = false)
			SlideType.BLANK -> BlankInfo(true)
		}
	}

	@FXML
	lateinit var listview: ListView<String>
	@FXML
	lateinit var combobox: ComboBox<String>
	@FXML
	lateinit var sTitle: Label
	@FXML
	lateinit var sTitleInput: TextField
	@FXML
	lateinit var sEditButton: Button
	@FXML
	lateinit var sDoneButton: Button
	@FXML
	lateinit var sSeparator: Separator
	@FXML
	lateinit var sFirstlineLabel: Label
	@FXML
	lateinit var infopane: AnchorPane
	@FXML
	lateinit var sPane: Pane

	lateinit var songTitleGroup:Array<Region>
	lateinit var songGroup:Array<Region>
	lateinit var bibleGroup:Array<Region>

	private val slideInfos = arrayListOf<SlideInfo>()
	private var currentSlide = -1

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
		//val lut = arrayOf("Ének","Biblia","Üres")
		//listview.items.add(lut[selected])
		listview.items.add("")

		slideInfos.add(selected.toSlideType().toEmptySlideInfo())
		assert(listview.items.size == slideInfos.size)
		currentSlide = slideInfos.size-1

		refreshListView()
		loadSlide(currentSlide)
		//songTitleGroup.flip()
	}

	fun SlideInfo.setVisibilityEdit() {
		infopane.on()
		when(this) {
			is SongInfo -> {
				log.debug("Setting visibility for song")
				sPane.on()
				sTitle.off()
				sEditButton.off()
				sSeparator.off()
				sDoneButton.on()
				sTitleInput.on()
			}
			is BibleInfo -> {

			}
			is BlankInfo -> {
				infopane.off()
			}
		}
	}
	fun SlideInfo.setVisibility() {
		this.setVisibilityEdit()
		songTitleGroup.flip()
	}

	@FXML
	fun onEditDone() {
		val info = slideInfos[currentSlide]
		if (info !is SongInfo) {
			log.fatal("Stored info is not for a song slide")
			exitProcess(1)
		}
		info.id = try {
			sTitleInput.text.toInt()
		} catch (e: NumberFormatException) {
			log.fatal("Not a number: ${sTitleInput.text}") // TODO
			exitProcess(2)
		}

		info.inited = true
		loadSlide(currentSlide)
		refreshListView()
	}

	private fun saveCB() {
		val info = slideInfos[currentSlide]
		if (info !is SongInfo) {
			log.fatal("Stored info is not for a song slide")
			exitProcess(1)
		}
		info.id = SongEditController.id
		SongEditController.stage.close()

		loadSlide(currentSlide)
		refreshListView()
	}
	@FXML
	fun onEdit() {
		if(slideInfos[currentSlide] !is SongInfo) {
			log.error("not song")
			exitProcess(1)
		}
		val stage = Stage()
		SongEditController.id = (slideInfos[currentSlide] as SongInfo).id
		SongEditController.stage = stage
		SongEditController.saveCB = { saveCB() }
		val loader = FXMLLoader(SongEditController::class.java.getResource("songedit.fxml"))
		val sc = Scene(loader.load(), 400.0, 500.0)
		sc.stylesheets.add(javaClass.getResource("songedit.fxml")!!.toExternalForm())

		with(stage) {
			title = "Edit"
			scene = sc
			isResizable = false
			initModality(Modality.WINDOW_MODAL)
			initOwner(primaryStage)
			centerOnScreen()
			show()
			toFront()
		}
		stage.onCloseRequest = EventHandler {
			saveCB()
		}
		loadSlide(currentSlide)
		refreshListView()
	}

	fun loadSlide(id: Int) {
		val info = slideInfos[id]
		when(info) {
			is SongInfo -> {
				log.debug("Loading song slide ${info.id}")
				if(info.inited) {
					info.setVisibility()
					sTitle.text = "${info.id}. ${if (info.id <= 150) "Zsoltár" else "Dicséret"}"
				} else {
					info.setVisibilityEdit()
					sTitleInput.text = ""
				}
			}
			is BibleInfo -> {

			}
			is BlankInfo -> { info.setVisibility() }
		}
	}
	fun refreshListView() {
		listview.selectionModel.select(currentSlide)
		for ((index, info) in slideInfos.withIndex()) {
			listview.items[index] = when (info) {
				is SongInfo -> if(info.id==0) "új ének" else "${info.id}. ének"
				is BibleInfo -> "biblia"
				is BlankInfo -> "üres"
			}
		}
	}

	private fun Int.toSlideType() = arrayOf(SlideType.SONG, SlideType.BIBLE, SlideType.BLANK)[this]
	private fun String.toSlideType() = when(this) {
		"Ének"->SlideType.SONG
		"Biblia"->SlideType.BIBLE
		"Üres"->SlideType.BLANK
		else -> throw IllegalArgumentException()
	}

	override fun initialize(location: URL?, res: ResourceBundle?) {
		songTitleGroup = arrayOf(sTitle, sEditButton, sDoneButton, sTitleInput, sSeparator)
		songGroup = arrayOf(sFirstlineLabel)
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
			currentSlide = listview.selectionModel.selectedIndex//combobox.selectionModel.selectedIndex
			loadSlide(currentSlide)
		}
		sTitleInput.onKeyReleased = EventHandler<KeyEvent> { event ->
			if(event.code == KeyCode.ENTER)
				onEditDone()
		}
		slideInfos.add(BlankInfo())
		listview.items.add("Üres")
		listview.selectionModel.select(0)
	}
}