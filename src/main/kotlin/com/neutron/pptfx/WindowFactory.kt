package com.neutron.pptfx

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.stage.Modality
import javafx.stage.Stage

class WindowFactory(val stage: Stage) {

	constructor(fxml: String, width: Double, height: Double, t: String):this(Stage()) {
		val loader = FXMLLoader(WindowFactory::class.java.getResource(fxml), bundle)
		val sc = Scene(loader.load(), width, height)
		sc.stylesheets.add(javaClass.getResource(fxml)!!.toExternalForm())

		with(stage) {
			title = t
			scene = sc
			isResizable = false
			initModality(Modality.WINDOW_MODAL)
			initOwner(primaryStage)
			centerOnScreen()
			show()
			toFront()
		}
	}
	constructor(msg: String):this(Stage()) {
		log.debug("Opening message window with text: $msg")
		val txt = Label(msg)
		val pane = AnchorPane(txt)

		AnchorPane.setBottomAnchor(txt, 35.0)
		AnchorPane.setTopAnchor(txt, 35.0)
		AnchorPane.setLeftAnchor(txt, 25.0)
		AnchorPane.setRightAnchor(txt, 25.0)

		val sc = Scene(pane, 200.0, 150.0)
		with(stage) {
			title = "Info"
			scene = sc
			isResizable = false
			initModality(Modality.WINDOW_MODAL)
			initOwner(primaryStage)
			centerOnScreen()
			show()
			toFront()
		}
	}
}