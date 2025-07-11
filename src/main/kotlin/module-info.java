module com.neutron.pptfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires kotlinx.serialization.json;
    requires kotlinx.serialization.core;
    requires org.apache.logging.log4j;
    requires java.desktop;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    //requires org.controlsfx.controls;

    opens com.neutron.pptfx to javafx.fxml;
    exports com.neutron.pptfx;
}