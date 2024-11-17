module com.rikoz99.beatlessongsranking {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.rikoz99.beatlessongsranking to javafx.fxml;
    exports com.rikoz99.beatlessongsranking;
}